package com.kyriakosalexandrou.bluetoothtoolkit.bt.searchhelpers.blesearch;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.util.Log;

import com.kyriakosalexandrou.bluetoothtoolkit.bt.BtAdapterHelperCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * It uses the new android BLE API which is only available fromLollipop and above
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BleSearchPostLollipopHelper extends BleSearchBaseHelper {
    private final static String TAG = "BleSearchPostLollipopHelper";
    private final BluetoothLeScanner mBleScanner;

    /**
     * @param btAdapter
     * @param btAdapterHelperCallback class to give callbacks to
     * @throws NullPointerException if one of the parameters is null
     */
    public BleSearchPostLollipopHelper(BluetoothAdapter btAdapter,
                                       BtAdapterHelperCallback btAdapterHelperCallback) {
        super(btAdapter, btAdapterHelperCallback);
        mBleScanner = mBtAdapter.getBluetoothLeScanner();
    }

    @Override
    public void stopScan() {
        if (mIsScanning) {
            Log.v(TAG, "stopScan");
            mBleScanner.stopScan(mScanCallback);
            setPropertiesToDefault();
        } else {
            Log.v(TAG, "BLE Scanning has already been stopped!");
        }
    }

    @Override
    public boolean startScan() {
        mBleScanner.startScan(mScanCallback);
        Log.v(TAG, "startScan");
        mIsScanning = true;
        stopScanTimeout();
        return mIsScanning;
    }

    @Override
    public boolean startScan(final UUID[] serviceUuids) {
        mSpecificUuidServicesThatAreInsideTheDevice = serviceUuids;
        mShouldFilterFoundDevices = true;

        mIsScanning = startScan();

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
         * stop scanning and then after SCAN_PERIODICAL_INTERVAL start BLE
		 * scanning
		 */
        mScanPeriodicalTimeout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBleScanner.stopScan(mScanCallback);

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
         * stop scanning and then after
		 * SCANNING_PERIODICALLY_INTERVAL_TIMEOUT start BLE scanning
		 */
        mScanPeriodicalTimeout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBleScanner.stopScan(mScanCallback);
                /*
                 * while this is true it will continue starting the BLE scan.
				 * the mIsScanPeriodically becomes false when the
				 * SCANNING_TIMEOUT runs out
				 */
                if (mIsScanningPeriodically) {
                    startBleScanPeriodically(serviceUuids);
                }
            }
        }, SCAN_PERIODICAL_INTERVAL);

        return mIsScanningPeriodically;
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.v(TAG, "A BLE device was found");
            super.onScanResult(callbackType, result);

            if (!mShouldFilterFoundDevices) {

                mBleSearchHelperCallback.onBleDeviceFound(
                        result.getDevice(),
                        result.getRssi(),
                        result.getScanRecord().getBytes());
            } else {
                /*
                doing a specific service UUIDs BLE devices scan get the service's UUIDs that the
                remote device is advertising
				 */
                List<UUID> deviceAdvertisedServices = new ArrayList<>();

                if (result.getScanRecord() != null) {
                    deviceAdvertisedServices = parseUuids(result.getScanRecord().getBytes());
                }

                /*
                displaying to console the UUIDs that are advertised in the advertise data of the
                remote BLE device
                 */
                for (UUID currentUuidFromDevice : deviceAdvertisedServices) {
                    Log.v(TAG, "UUID advertised by remote device without connecting: " + currentUuidFromDevice);
                }

				/*
                 * filtering, checking if the remote BLE device has the UUIDs
				 * asked for
				 */
                for (int i = 0; i < mSpecificUuidServicesThatAreInsideTheDevice.length; i++) {
                    if (deviceAdvertisedServices
                            .contains(mSpecificUuidServicesThatAreInsideTheDevice[i])) {
                        mBleSearchHelperCallback.onBleDeviceFound(
                                result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                    }
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.v(TAG, "onBatchScanResults");
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.v(TAG, "onScanFailed");
            super.onScanFailed(errorCode);
        }
    };
}