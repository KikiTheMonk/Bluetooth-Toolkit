package com.kyriakosalexandrou.bluetoothtoolkit.ui.activities;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.ui.adapters.HtmMultipleDevicesAdapter;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.base.BleBaseDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.HtmDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.HtmDeviceManagerUICallback;

public class HtmDeviceMultipleDevicesActivity extends
        BleBaseMultipleDevicesActivity implements
        HtmDeviceManagerUICallback {
    private final static String TAG = "HtmDeviceMultipleDevicesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_multiple_ble_devices);

        initialiseDialogAbout(getResources().getString(
                R.string.about_multiple_thermometer));
        initialiseDialogFoundDevices("HTM", getResources().getDrawable(R.drawable.ic_toolbar_multiple_htm));

        setHintTextValue(R.string.about_multiple_thermometer);
    }

    @Override
    public void setAdapters() {
        mListConnectedDevicesAdapter = new HtmMultipleDevicesAdapter(
                this);
        super.setAdapters();
    }

    @Override
    public void onDialogFoundDevicesItemClick(AdapterView<?> arg0, View view, int position, long id) {
        final BluetoothDevice device = mDialogFoundBtDevices.getListFoundDevicesHandler()
                .getDevice(position);
        if (device == null)
            return;

        getBtAdapterHelper().getBleSearchBaseHelper().stopScan();
        mDialogFoundBtDevices.dismiss();

        BleBaseDeviceManager thermometerManager = new HtmDeviceManager(this,
                mActivity);
        boolean isDeviceAddedToTheList = mListConnectedDevicesAdapter
                .addDevice(thermometerManager);

        if (isDeviceAddedToTheList) {
            thermometerManager.connect(device, false);
            uiInvalidateViewsState();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.multiple_htm, menu);
        if (getActionBar() != null) {
            getActionBar().setIcon(R.drawable.ic_toolbar_multiple_htm);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        return true;
    }

    @Override
    public void onUiHtmFound(boolean isFound) {
        if (!isFound) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            mActivity,
                            "Health htm measurement "
                                    + "characteristic was not found",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onUiTemperatureChange(final float result) {
        uiInvalidateViewsState();
    }
}