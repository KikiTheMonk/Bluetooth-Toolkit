package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleSearch;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.util.Log;

import com.kyriakosalexandrou.bluetoothtoolkit.bt.BtAdapterHelperCallback;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.BtSearchHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Base implementation class for {@link BleSearchPostJellyBeanMr2Helper} and {@link BleSearchPostLollipopHelper}.
 * In this way we can use this class to initiate operations without knowing if it's pre or post Lollipop
 */
public abstract class BleSearchBaseHelper extends BtSearchHelper {
    private final static String TAG = "BleSearchBaseHelper";

    protected final BluetoothAdapter mBtAdapter;
    protected BleSearchHelperCallback mBleSearchHelperCallback;

    protected static long SCAN_TIMEOUT = 20 * 1000;
    /*
     * the BLE scan interval for stopping and starting BLE scanning while doing
     * a periodically scan. use this to get updated RSSI values from android
     * devices that only fetch a remote device once in there callback with each
     * BLE scan
     */
    protected static long SCAN_PERIODICAL_INTERVAL = 1 * 1000;
    protected final Handler mScanTimeout = new Handler();
    protected final Handler mScanPeriodicalTimeout = new Handler();

    protected boolean mIsScanningPeriodically = false;

    protected boolean mShouldFilterFoundDevices = false;
    protected UUID[] mSpecificUuidServicesThatAreInsideTheDevice;

    /**
     * @param btAdapter
     * @param btAdapterHelperCallback class to give callbacks to
     * @throws NullPointerException if one of the parameters is null
     */
    public BleSearchBaseHelper(BluetoothAdapter btAdapter,
                               BtAdapterHelperCallback btAdapterHelperCallback) {
        if (btAdapterHelperCallback == null)
            throw new NullPointerException(
                    "btAdapter or btAdapterHelperCallback object passed is NULL");

        mBtAdapter = btAdapter;
        mBleSearchHelperCallback = btAdapterHelperCallback;
    }

    /**
     * get the scanning time of the BLE scan operation <br>
     * note: default scanning time is {@value BleSearchBaseHelper#SCAN_TIMEOUT} ms if it hasn't been changed
     *
     * @return the BLE scan timeout
     */
    public long getScanTimeout() {
        return SCAN_TIMEOUT;
    }

    /**
     * get the interval time between the BLE scans when using the periodical
     * scan. <br>
     * note: by default the interval time is {@value BleSearchBaseHelper#SCAN_PERIODICAL_INTERVAL} ms
     *
     * @return the interval time
     */
    public long getScanPeriodicalInterval() {
        return SCAN_PERIODICAL_INTERVAL;
    }

    /**
     * make sure that potential BLE scanning will take no longer than
     * scanningTimeout seconds. <br>
     * note: default scanning time is 20 seconds if it hasn't been changed
     *
     * @param scanTimeout
     */
    public void setScanTimeout(long scanTimeout) {
        SCAN_TIMEOUT = scanTimeout;
    }

    /**
     * use it to set the interval for the {@link #startBleScanPeriodically()} or
     * the {@link #startBleScanPeriodically(UUID[])} methods
     *
     * @param scanPeriodicalInterval the time to set the interval to
     */
    public void setScanPeriodicalInterval(long scanPeriodicalInterval) {
        SCAN_PERIODICAL_INTERVAL = scanPeriodicalInterval;
    }

    protected void setPropertiesToDefault() {
        mIsScanning = false;
        mIsScanningPeriodically = false;
        mShouldFilterFoundDevices = false;
        mScanTimeout.removeCallbacksAndMessages(null);
        mScanPeriodicalTimeout.removeCallbacksAndMessages(null);
        mBleSearchHelperCallback.onBleStopScan();
    }

    /**
     * stops BLE scan operation after a pre-defined scan period.
     */
    protected final void stopScanTimeout() {
        mScanTimeout.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "stopScan after timeout of BLE scanning");
                stopScan();
            }
        }, SCAN_TIMEOUT);
    }

    /**
     * finds the UUIDs that are in the advertised data of a remote BLE device
     *
     * @param advertisedData the data to search for UUIDs
     * @return the list of found UUIDs that are in the advertised data
     */
    protected List<UUID> parseUuids(byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<>();

        ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(
                ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0)
                break;

            byte type = buffer.get();
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (length >= 2) {
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb",
                                buffer.getShort())));
                        length -= 2;
                    }
                    break;

                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;

                default:
                    buffer.position(buffer.position() + length - 1);
                    break;
            }
        }
        return uuids;
    }

    public void setCallback(BtAdapterHelperCallback callback) {
        this.mBleSearchHelperCallback = callback;
    }


    /**
     * initiates stop scanning operation, the onBleStopScan callback gets called
     * whenever the BLE scanning operations stops/finishes by either calling
     * this method or when the timeout of the BLE scanning operation runs out
     */
    public abstract void stopScan();

    /**
     * initiates BLE scan operation, the callback
     * {@link BtAdapterHelperCallback#onBleStopScan()} gets called when
     * the {@link #stopScan()} method is called or when the SCAN_TIMEOUT
     * runs out
     *
     * @return true, if the scan was started successfully
     */
    public abstract boolean startScan();

    /**
     * initiates a BLE scan operation that scans for ALL BLE devices, it then goes through their UUIDS
     * and if it finds at least one of the given UUIDs then it notifies by calling the
     * <p/>
     * {@link BtAdapterHelperCallback#onBleStopScan()} when
     * the {@link #stopScan()} method is called or when the BLE_SCAN_TIMEOUT
     * runs out
     *
     * @param serviceUuids the services UUIDs to search BLE devices, Make sure that the
     *                     BLE device is actually advertising the UUIDs looking for in
     *                     it's advertisement data
     * @return true, if the scan was started successfully, otherwise false
     */
    public abstract boolean startScan(final UUID[] serviceUuids);

    /**
     * start a BLE scan operation that in the background it actually starts and
     * stop the scanning every SCAN_PERIODICAL_INTERVAL,
     * <p/>
     * The callback {@link BtAdapterHelperCallback#onBleStopScan()} gets
     * called when the {@link #stopScan()} method is called or when the
     * SCAN_TIMEOUT runs out
     *
     * @return true, if the scan was started successfully, otherwise false
     */
    public abstract boolean startBleScanPeriodically();

    /**
     * start a BLE scan operation for BLE devices that are advertising the given
     * UUIDs given. This method in the background actually starts and stop the
     * scanning every SCAN_PERIODICAL_INTERVAL
     * <p/>
     * <p/>
     * The callback {@link BtAdapterHelperCallback#onBleStopScan()} gets
     * called when the {@link #stopScan()} method is called or when the
     * SCAN_TIMEOUT runs out
     *
     * @param serviceUuids the services UUIDs to search BLE devices
     * @return true, if the scan was started successfully, otherwise false
     */
    public abstract boolean startBleScanPeriodically(final UUID[] serviceUuids);
}