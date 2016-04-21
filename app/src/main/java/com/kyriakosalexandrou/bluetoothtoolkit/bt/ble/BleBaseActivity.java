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

package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.BtBaseActivity;

/**
 * Base activity for all the activities that will be doing BLE functionality
 *
 * @author Kyriakos.Alexandrou
 */
public abstract class BleBaseActivity extends BtBaseActivity implements
        BleBaseDeviceManagerUiCallback {
    private final static String TAG = "BleBaseActivity";
    protected boolean isPrefPeriodicalScan = true;


    @Override
    public void onCreate(Bundle savedInstanceState, int layoutResID) {
        super.onCreate(savedInstanceState, layoutResID);

        isPermissionRequestGranted(Manifest.permission.ACCESS_COARSE_LOCATION,
                getString(R.string.permission_access_coarse_location_msg));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class BleScanBtnListener extends ScanBtnListener {
        public boolean isDeviceReadyForBleScan() {
            if (!isGpsEnabled()) {
                Log.i(TAG, "GPS must be on to be able to do a BLE scanning");

                Toast.makeText(mActivity,
                        R.string.gps_not_enabled,
                        Toast.LENGTH_SHORT).show();
            }

            return isBtEnabled() && isCoarseLocationPermissionRequestGranted() && isGpsEnabled();
        }
    }

    protected boolean isCoarseLocationPermissionRequestGranted() {
        return isPermissionRequestGranted(Manifest.permission.ACCESS_COARSE_LOCATION,
                getString(R.string.permission_access_coarse_location_msg));
    }

    @Override
    public void onUiConnecting() {
        uiInvalidateViewsState();
    }

    @Override
    public void onUiDisconnecting() {
        uiInvalidateViewsState();
    }

    @Override
    public void onUiConnected() {
        uiInvalidateViewsState();
    }

    @Override
    public void onUiDisconnected(final int status) {
        uiInvalidateViewsState();
    }

    @Override
    public void onDialogFoundDevicesCancel(DialogInterface arg0) {
        getBtAdapterHelper().getBleSearchBaseHelper().stopScan();
        uiInvalidateViewsState();
    }

    @Override
    protected void loadPref() {
        super.loadPref();
        isPrefPeriodicalScan = mSharedPreferences.getBoolean(
                "pref_periodical_scan", true);
    }
}