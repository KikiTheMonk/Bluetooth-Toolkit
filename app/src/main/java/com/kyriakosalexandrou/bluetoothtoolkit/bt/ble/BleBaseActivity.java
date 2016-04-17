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