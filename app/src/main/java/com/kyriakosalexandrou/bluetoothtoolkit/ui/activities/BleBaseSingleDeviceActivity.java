package com.kyriakosalexandrou.bluetoothtoolkit.ui.activities;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.base.BleBaseDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.ui.components.ComponentCommonBleDevicePropertiesDetails;

/**
 * Base activity for all the activities that can connect to single BLE devices
 *
 * @author Kyriakos.Alexandrou
 */
public abstract class BleBaseSingleDeviceActivity extends BleBaseActivity {
    private final static String TAG = "BleBaseSingleDeviceActivity";
    protected BleBaseDeviceManager mBleBaseDeviceManager;
    protected ComponentCommonBleDevicePropertiesDetails mComponentCommonBleDevicePropertiesDetails;

    public void onCreate(Bundle savedInstanceState, int layoutResID, BleBaseDeviceManager bleBaseDeviceManager) {
        super.onCreate(savedInstanceState, layoutResID);
        mBleBaseDeviceManager = bleBaseDeviceManager;
    }

    @Override
    public void bindViews() {
        super.bindViews();
        bindCommonViews();
    }

    protected void bindCommonViews() {
        mComponentCommonBleDevicePropertiesDetails =
                (ComponentCommonBleDevicePropertiesDetails) findViewById(R.id.common_ble_properties_component);
        mComponentCommonBleDevicePropertiesDetails.setBleDeviceManager(mBleBaseDeviceManager);
    }

    @Override
    protected void setListeners() {
        mBtnScan.setOnClickListener(new BleScanBtnListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_scan: {
                        if (!super.isDeviceReadyForBleScan()) {
                            return;
                        }

                        if (mBleBaseDeviceManager.getConnectionState() != BluetoothProfile.STATE_CONNECTED
                                && mBleBaseDeviceManager.getConnectionState() != BluetoothProfile.STATE_CONNECTING) {

                            if (isPrefPeriodicalScan) {
                                getBtAdapterHelper().getBleSearchBaseHelper().startBleScanPeriodically();
                            } else {
                                getBtAdapterHelper().getBleSearchBaseHelper().startScan();
                            }
                            mDialogFoundBtDevices.show();
                        } else if (mBleBaseDeviceManager.getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
                            mBleBaseDeviceManager.disconnect();

                        } else if (mBleBaseDeviceManager.getConnectionState() == BluetoothProfile.STATE_CONNECTING) {
                            Toast.makeText(getApplication(),
                                    R.string.wait_for_connection, Toast.LENGTH_SHORT)
                                    .show();
                        }

                        uiInvalidateViewsState();
                        break;
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                mBleBaseDeviceManager.disconnect();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isPrefRunInBackground) {
            // let the app run normally in the background
        } else {
            // stop scanning or disconnect if we are connected
            if (getBtAdapterHelper().getBleSearchBaseHelper().isScanning()) {
                getBtAdapterHelper().getBleSearchBaseHelper().stopScan();
            } else if (mBleBaseDeviceManager != null) {
                if (mBleBaseDeviceManager.getConnectionState() == BluetoothProfile.STATE_CONNECTING
                        || mBleBaseDeviceManager.getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
                    mBleBaseDeviceManager.disconnect();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        mBleBaseDeviceManager.disconnect();
        super.onBackPressed();
    }

    @Override
    public void uiInvalidateBtnState() {
        if (mBleBaseDeviceManager.getConnectionState() != BluetoothProfile.STATE_CONNECTED
                && mBleBaseDeviceManager.getConnectionState() != BluetoothProfile.STATE_CONNECTING) {
            mBtnScan.setText(R.string.btn_scan);
        } else if (mBleBaseDeviceManager.getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
            mBtnScan.setText(R.string.btn_disconnect);
        } else if (mBleBaseDeviceManager.getConnectionState() == BluetoothProfile.STATE_CONNECTING) {
            mBtnScan.setText(R.string.btn_connecting);
        }
    }

    @Override
    public void onUiDeviceManagerInitialised(BleBaseDeviceManager bleBaseDeviceManager) {
        mBleBaseDeviceManager = bleBaseDeviceManager;
//        mCommonBleDevicePropertiesDetailsComponent.setBleDeviceManager(mBleBaseDeviceManager);
    }

    @Override
    public void onUiConnecting() {
        super.onUiConnecting();
    }

    @Override
    public void onUiDisconnecting() {
        super.onUiDisconnecting();
    }

    @Override
    public void onUiConnected() {
        super.onUiConnected();
        setViewsToConnectedState();
    }

    private void setViewsToConnectedState() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO not sure if this is actually necessary as we are also invalidating the UI changes automatically
                mBtnScan.setText(R.string.btn_disconnect);

                if (mComponentCommonBleDevicePropertiesDetails != null) {
                    mComponentCommonBleDevicePropertiesDetails.updateCommonViews();
                }
            }
        });
    }

    @Override
    public void onUiDisconnected(final int status) {
        super.onUiDisconnected(status);
        setViewsToDefaultState(status);
    }

    private void setViewsToDefaultState(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO not sure if this is actually necessary as we are also invalidating the UI changes automatically
                Log.i(TAG, "onUiDisconnected status: " + status);
                mBtnScan.setText(getResources().getString(R.string.btn_scan));

                if (mComponentCommonBleDevicePropertiesDetails != null) {
                    mComponentCommonBleDevicePropertiesDetails.setCommonViewsToNonApplicable();
                }
            }
        });
    }

    @Override
    public void onUiBatteryRead(final int batteryValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mComponentCommonBleDevicePropertiesDetails.updateCommonViews();
            }
        });
    }

    @Override
    public void onUiReadRemoteRssi(final int rssiValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mComponentCommonBleDevicePropertiesDetails.updateCommonViews();
            }
        });
    }

    @Override
    public void onDialogFoundDevicesItemClick(AdapterView<?> arg0,
                                              View view, int position, long id) {
        final BluetoothDevice device = mDialogFoundBtDevices.getListFoundDevicesHandler()
                .getDevice(position);
        if (device == null)
            return;

        getBtAdapterHelper().getBleSearchBaseHelper().stopScan();
        mDialogFoundBtDevices.dismiss();

        mBleBaseDeviceManager.connect(device, false);
        uiInvalidateViewsState();
    }
}