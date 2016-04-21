package com.kyriakosalexandrou.bluetoothtoolkit.bt.searchhelpers.blesearch;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

import com.kyriakosalexandrou.bluetoothtoolkit.bt.BtAdapterHelperCallback;

import java.util.List;
import java.util.UUID;

/**
 * It uses the old android BLE API which can support from Jelly bean MR2 to Lollipop
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleSearchPostJellyBeanMr2Helper extends BleSearchBaseHelper {
    private final static String TAG = "BleSearchPostJellyBeanMr2Helper";

    /**
     * @param btAdapter
     * @param btAdapterHelperCallback class to give callbacks to
     * @throws NullPointerException if one of the parameters is null
     */
    public BleSearchPostJellyBeanMr2Helper(BluetoothAdapter btAdapter,
                                           BtAdapterHelperCallback btAdapterHelperCallback) {
        super(btAdapter, btAdapterHelperCallback);
    }

    @Override
    public void stopScan() {
        if (mIsScanning) {
            mBtAdapter.stopLeScan(mLeScanCallback);
            setPropertiesToDefault();
        } else {
            Log.i(TAG, "BLE Scanning has already been stopped!");
        }
    }

    @Override
    public boolean startScan() {
        mIsScanning = mBtAdapter.startLeScan(mLeScanCallback);
        if (mIsScanning) {
            stopScanTimeout();
        }
        return mIsScanning;
    }

    @Override
    public boolean startScan(final UUID[] serviceUuids) {
        mSpecificUuidServicesThatAreInsideTheDevice = serviceUuids;
        mShouldFilterFoundDevices = true;

        mIsScanning = mBtAdapter.startLeScan(mLeScanCallback);

        if (mIsScanning) {
            stopScanTimeout();
        } else {
            mShouldFilterFoundDevices = false;
            mSpecificUuidServicesThatAreInsideTheDevice = null;
        }

        return mIsScanning;
    }

    @Override
    public boolean startBleScanPeriodically() {
        mIsScanningPeriodically = startScan();
        /*
        stop scanning and then after SCAN_PERIODICAL_INTERVAL start BLE scanning
		 */
        mScanPeriodicalTimeout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtAdapter.stopLeScan(mLeScanCallback);

                /*
                while this is true it will continue starting the BLE scan the
                mIsScanningPeriodically becomes false when the SCAN_TIMEOUT runs out
                 */
                if (mIsScanningPeriodically) {
                    startBleScanPeriodically();
                }
            }
        }, SCAN_PERIODICAL_INTERVAL);

        return mIsScanningPeriodically;
    }

    @Override
    public boolean startBleScanPeriodically(final UUID[] serviceUuids) {
        mIsScanningPeriodically = startScan(serviceUuids);

        /*
        stop scanning and then after SCAN_PERIODICAL_INTERVAL start BLE
        scanning
		 */
        mScanPeriodicalTimeout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtAdapter.stopLeScan(mLeScanCallback);

                /*
                while this is true it will continue starting the BLE scan the
                mIsScanningPeriodically becomes false when the SCAN_TIMEOUT runs out
                 */
                if (mIsScanningPeriodically) {
                    startBleScanPeriodically(serviceUuids);
                }
            }
        }, SCAN_PERIODICAL_INTERVAL);

        return mIsScanningPeriodically;
    }

    /**
     * defines callback for BLE scanning results
     */
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        // comes from: startLeScan
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            Log.i(TAG, "A BLE device was found");

            if (!mShouldFilterFoundDevices) {
                mBleSearchHelperCallback.onBleDeviceFound(device, rssi, scanRecord);
            } else {
                /*
                doing a specific service UUIDs BLE devices scan get the service's UUIDs that the
                remote device is advertising
				 */
                List<UUID> deviceAdvertisedServices = parseUuids(scanRecord);

                /*
                displaying to console the UUIDs that are advertised in the advertise data of the
                remote BLE device
                 */
                for (UUID currentUuidFromDevice : deviceAdvertisedServices) {
                    Log.i(TAG, "UUID advertised without connecting: " + currentUuidFromDevice);
                }

				/*
                filtering, checking if the remote BLE device has the UUIDs asked for
				 */
                for (int i = 0; i < mSpecificUuidServicesThatAreInsideTheDevice.length; i++) {
                    if (deviceAdvertisedServices
                            .contains(mSpecificUuidServicesThatAreInsideTheDevice[i])) {
                        mBleSearchHelperCallback.onBleDeviceFound(
                                device, rssi, scanRecord);
                    }
                }
            }
        }
    };
}