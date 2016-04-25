/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Kyriakos Alexandrou (Kiki)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.kyriakosalexandrou.bluetoothtoolkit.helpers.bt.bleSearch;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.util.Log;

import com.kyriakosalexandrou.bluetoothtoolkit.helpers.bt.BtAdapterHelperCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * It uses the new android BLE API which is only available fromLollipop and above
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BlePostLollipopSearchHelper extends BleBaseSearchHelper {
    private final static String TAG = "BlePostLollipopSearchHelper";
    private final BluetoothLeScanner mBleScanner;

    /**
     * @param btAdapter
     * @param btAdapterHelperCallback class to give callbacks to
     * @throws NullPointerException if one of the parameters is null
     */
    public BlePostLollipopSearchHelper(BluetoothAdapter btAdapter,
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