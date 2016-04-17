package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble;

import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;

/**
 * Base activity for all the activities that are able to connect to multiple BLE
 * devices
 *
 * @author Kyriakos.Alexandrou
 */
public abstract class BleBaseMultipleDevicesActivity extends BleBaseActivity {
    private final static String TAG = "BleBaseMultipleDevicesActivity";

    protected BleBaseMultipleDevicesAdapter mListConnectedDevicesAdapter = null;
    protected ListView mLvConnectedDevices;
    private TextView mHintTv;

    public void setHintTextValue(int hintTextValueRes) {
        String hintTextValue = getResources().getString(hintTextValueRes);
        mHintTv.setText(hintTextValue);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, int layoutResID) {
        super.onCreate(savedInstanceState, layoutResID);
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        mHintTv = (TextView) findViewById(R.id.hint);
        mLvConnectedDevices = (ListView) findViewById(R.id.connected_devices_listview);
    }

    @Override
    protected void setAdapters() {
        mLvConnectedDevices.setAdapter(mListConnectedDevicesAdapter);
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

                        if (isPrefPeriodicalScan) {
                            getBtAdapterHelper().getBleSearchBaseHelper().startBleScanPeriodically();
                        } else {
                            getBtAdapterHelper().getBleSearchBaseHelper().startScan();
                        }
                        mDialogFoundBtDevices.show();
                        uiInvalidateViewsState();
                        break;
                    }
                }
            }
        });

        mLvConnectedDevices.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    final int position, long id) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        BleBaseDeviceManager device = mListConnectedDevicesAdapter
                                .getDevice(position);

                        if (device.getConnectionState() == BluetoothProfile.STATE_CONNECTED
                                || device.getConnectionState() == BluetoothProfile.STATE_CONNECTING) {
                            Log.i("TAG", "disconnect with device at position: "
                                    + position);
                            device.disconnect();
                        } else {
                            Log.i("TAG",
                                    "already disconnected, just remove the device form the listview at position: "
                                            + position);
                            mListConnectedDevicesAdapter.remove(position);
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mListConnectedDevicesAdapter.disconnectFromDevices();
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
            mListConnectedDevicesAdapter.disconnectFromDevices();
        }
    }

    @Override
    public void onBackPressed() {
        mListConnectedDevicesAdapter.disconnectFromDevices();
        finish();
    }

    @Override
    public void uiInvalidateBtnState() {
    }

    @Override
    public void onUiDeviceManagerInitialised(BleBaseDeviceManager bleBaseDeviceManager) {
    }

    @Override
    public void onDialogFoundDevicesItemClick(AdapterView<?> arg0,
                                              View view, int position, long id) {
        /*
         * override this to create the specific device (HtmDeviceActivity,
		 * HearRateManager etc.) that is needed once it's clicked
		 */

        //TODO make it work just like the BleBaseActivity
    }

    @Override
    public void onUiDisconnected(int status) {
        super.onUiDisconnected(status);
        removeDisconnectedDeviceFromView();
    }

    @Override
    public void onUiBatteryRead(final int result) {
        uiInvalidateViewsState();
    }

    @Override
    public void onUiReadRemoteRssi(final int rssi) {
        uiInvalidateViewsState();
    }

    private void removeDisconnectedDeviceFromView() {
        /*
        remove the device that just got disconnected from the UI
		 */
        for (int i = 0; i < mListConnectedDevicesAdapter.getCount(); i++) {
            final int tempI = i;

            if (mListConnectedDevicesAdapter.getDevice(i).getConnectionState() == BluetoothProfile.STATE_DISCONNECTED) {

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListConnectedDevicesAdapter.remove(tempI);
                        mListConnectedDevicesAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    @Override
    public void uiInvalidateViewsState() {
        super.uiInvalidateViewsState();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListConnectedDevicesAdapter.notifyDataSetChanged();
                toggleHint();
            }
        });
    }

    private void toggleHint() {
        if (mListConnectedDevicesAdapter == null
                || mListConnectedDevicesAdapter.getCount() <= 0) {
            mHintTv.setVisibility(View.VISIBLE);
        } else {
            mHintTv.setVisibility(View.GONE);
        }
    }
}
