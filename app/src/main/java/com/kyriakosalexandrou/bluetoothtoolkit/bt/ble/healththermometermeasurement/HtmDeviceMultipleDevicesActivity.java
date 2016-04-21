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

package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.healththermometermeasurement;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseMultipleDevicesActivity;

public class HtmDeviceMultipleDevicesActivity extends
        BleBaseMultipleDevicesActivity implements
        HtmDeviceManagerUICallback {
    private final static String TAG = "HtmDeviceMultipleDevicesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_multiple_devices);

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