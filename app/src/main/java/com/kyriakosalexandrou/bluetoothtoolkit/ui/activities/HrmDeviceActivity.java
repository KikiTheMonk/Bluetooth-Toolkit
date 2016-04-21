package com.kyriakosalexandrou.bluetoothtoolkit.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.ui.graphs.HrmGraph;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.HrmDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.HrmDeviceManagerUiCallback;

public class HrmDeviceActivity extends BleBaseSingleDeviceActivity implements
        HrmDeviceManagerUiCallback {
    private HrmDeviceManager mHrmDeviceManager;
    private HrmGraph mGraph;
    private TextView mValueHRM, mValueBodySensorPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_ble_hrm);

        mHrmDeviceManager = new HrmDeviceManager(this, this);
//        setBleBaseDeviceManager(mHrmDeviceManager);

        initialiseDialogAbout(getResources().getString(
                R.string.about_heart_rate));
        initialiseDialogFoundDevices("HRM", getResources().getDrawable(R.drawable.ic_toolbar_hrm));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hrm, menu);
        getActionBar().setIcon(R.drawable.ic_toolbar_hrm);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void bindViews() {
        super.bindViews();
        bindCommonViews();

        mValueHRM = (TextView) findViewById(R.id.valueHRM);
        mValueBodySensorPosition = (TextView) findViewById(R.id.valueBodySensor);
        /*
         * getting the view of the whole activity, this is then passed to the
		 * base graph class to find the view for displaying the graph
		 */
        View wholeScreenView = ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        mGraph = new HrmGraph(this, wholeScreenView);
    }

    @Override
    public void onUiConnected() {
        super.onUiConnected();
        uiInvalidateViewsState();
    }

    @Override
    public void onUiDisconnected(int status) {
        super.onUiDisconnected(status);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mValueHRM.setText(getResources().getString(R.string.dash));
                mValueBodySensorPosition.setText(getResources().getString(
                        R.string.non_applicable));

                if (mGraph != null) {
                    mGraph.clearGraph();
                }
            }
        });
        mGraph.setStartTime(0);
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
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onUiHRM(final int hrmValue) {
        mGraph.startTimer();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mValueHRM.setText(mHrmDeviceManager.getFormattedHtmValue());
                mGraph.addNewData(hrmValue);
            }
        });
    }

    @Override
    public void onUiBodySensorLocation(final int valueBodySensorLocation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mValueBodySensorPosition.setText(mHrmDeviceManager.getFormattedBodySensorValue());
            }
        });
    }
}
