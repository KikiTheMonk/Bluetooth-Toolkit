package com.kyriakosalexandrou.bluetoothtoolkit.ui.activities;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.ui.adapters.BpmMultipleDevicesAdapter;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.base.BleBaseDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.BpmDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.BpmDeviceManagerUiCallback;

public class BpmDeviceMultipleDevicesActivity extends
        BleBaseMultipleDevicesActivity implements
        BpmDeviceManagerUiCallback {
    private final static String TAG = "BpmDeviceMultipleDevicesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,
                R.layout.activity_multiple_ble_devices);

        initialiseDialogAbout(getResources().getString(
                R.string.about_multiple_bloodpressure));
        initialiseDialogFoundDevices("BPM", getResources().getDrawable(R.drawable.ic_toolbar_multiple_bpm));

        setHintTextValue(R.string.about_multiple_bloodpressure);
    }

    @Override
    public void setAdapters() {
        mListConnectedDevicesAdapter = new BpmMultipleDevicesAdapter(
                this);
        super.setAdapters();
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

        BleBaseDeviceManager bpmManager = new BpmDeviceManager(
                this, mActivity);
        boolean isDeviceAddedToTheList = mListConnectedDevicesAdapter
                .addDevice(bpmManager);

        if (isDeviceAddedToTheList) {
            bpmManager.connect(device, false);
            uiInvalidateViewsState();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.multiple_bpm, menu);
        if (getActionBar() != null) {
            getActionBar().setIcon(R.drawable.ic_toolbar_multiple_bpm);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return true;
    }

    public void onUiBpmFound(boolean isFound) {
        if (!isFound) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            mActivity,
                            "Blood pressure measurement "
                                    + "characteristic was not found",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onUIBloodPressureRead(float mValueBloodPressureSystolicResult,
                                      float mValueBloodPressureDiastolicResult,
                                      float mValueBloodPressureArterialPressureResult) {
        uiInvalidateViewsState();
    }


}