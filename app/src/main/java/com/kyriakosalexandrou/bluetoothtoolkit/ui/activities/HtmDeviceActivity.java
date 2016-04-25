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

package com.kyriakosalexandrou.bluetoothtoolkit.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.managers.HtmDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.managers.HtmDeviceManagerUICallback;
import com.kyriakosalexandrou.bluetoothtoolkit.graphs.HtmGraph;

public class HtmDeviceActivity extends BleBaseSingleDeviceActivity implements
        HtmDeviceManagerUICallback {
    private HtmDeviceManager mHtmDeviceManager;
    private HtmGraph mGraph;
    private TextView mValueTempMeasurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_htm, new HtmDeviceManager(this, this));

        mHtmDeviceManager = (HtmDeviceManager) mBleBaseDeviceManager;

        initialiseDialogAbout(getResources().getString(
                R.string.about_thermometer));
        initialiseDialogFoundDevices(getString(R.string.thermometer), getResources().getDrawable(R.drawable.ic_toolbar_htm));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.htm, menu);
        getActionBar().setIcon(R.drawable.ic_toolbar_htm);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void bindViews() {
        super.bindViews();

        bindCommonViews();

        // Bind all generic views in the super. Bind specific views here.
        mValueTempMeasurement = (TextView) findViewById(R.id.tempMeasurementValue);
        /*
         * getting the view of the whole activity, this is then passed to the
		 * base graph class to find the view for displaying the graph
		 */
        View wholeScreenView = ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        mGraph = new HtmGraph(mActivity, wholeScreenView);
    }

    @Override
    public void onUiConnected() {
        super.onUiConnected();
        uiInvalidateViewsState();
    }

    @Override
    public void onUiDisconnected(int status) {
        super.onUiDisconnected(status);

        // set views to default
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mValueTempMeasurement.setText(getResources().getString(
                        R.string.dash));

                if (mGraph != null) {
                    mGraph.clearGraph();
                }
            }
        });
        mGraph.setStartTime(0);
    }

    @Override
    public void onUiHtmFound(boolean isFound) {
        if (!isFound) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            mActivity,
                            "Temperature measurement "
                                    + "characteristic was not found",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onUiTemperatureChange(final float result) {
        mGraph.startTimer();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mValueTempMeasurement.setText(mHtmDeviceManager.getFormattedHtmValue());
                mGraph.addNewData(result);
            }
        });
    }
}
