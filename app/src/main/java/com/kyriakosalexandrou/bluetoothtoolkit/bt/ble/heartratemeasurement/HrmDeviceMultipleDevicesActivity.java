package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.heartratemeasurement;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseMultipleDevicesActivity;

public class HrmDeviceMultipleDevicesActivity extends
        BleBaseMultipleDevicesActivity implements
        HrmDeviceManagerUiCallback {
    private final static String TAG = "HrmDeviceMultipleDevicesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,
                R.layout.activity_multiple_devices);

        initialiseDialogAbout(getResources().getString(
                R.string.about_multiple_heart_rate));
        initialiseDialogFoundDevices("HRM", getResources().getDrawable(R.drawable.ic_multiple_hrm));

        setHintTextValue(R.string.about_multiple_heart_rate);
    }

    @Override
    public void setAdapters() {
        mListConnectedDevicesAdapter = new HrmMultipleDevicesAdapter(
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

        BleBaseDeviceManager heartRateManager = new HrmDeviceManager(
                this, mActivity);
        boolean isDeviceAddedToTheList = mListConnectedDevicesAdapter
                .addDevice(heartRateManager);

        if (isDeviceAddedToTheList) {
            heartRateManager.connect(device, false);
            uiInvalidateViewsState();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.multiple_hrm, menu);
        if (getActionBar() != null) {
            getActionBar().setIcon(R.drawable.ic_multiple_hrm);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        return true;
    }

    @Override
    public void onUiBodySensorLocation(final int valueBodySensorLocation) {
        uiInvalidateViewsState();
    }

    @Override
    public void onUiHrmFound(boolean isFound) {
        if (!isFound) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            mActivity,
                            "Heart rate measurement "
                                    + "characteristic was not found",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onUiHRM(final int hrmValue) {
        uiInvalidateViewsState();
    }
}