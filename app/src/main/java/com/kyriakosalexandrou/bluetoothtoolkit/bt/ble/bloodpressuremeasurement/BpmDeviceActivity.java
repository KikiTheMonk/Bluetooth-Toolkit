package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.bloodpressuremeasurement;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseSingleDeviceActivity;

public class BpmDeviceActivity extends BleBaseSingleDeviceActivity implements BpmDeviceManagerUiCallback {
    private BpmDeviceManager mBpmDeviceManager;
    private BpmGraph mGraph;
    private TextView mSystolicResult, mDiastolicResult, mArterialPressureResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_bpm);

        mBpmDeviceManager = new BpmDeviceManager(this, mActivity);

        initialiseDialogAbout(getResources().getString(
                R.string.about_blood_pressure));
        initialiseDialogFoundDevices(getString(R.string.blood_pressure), getResources().getDrawable(R.drawable.ic_bpm));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bpm, menu);
        getActionBar().setIcon(R.drawable.ic_bpm);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void bindViews() {
        super.bindViews();

        bindCommonViews();

        mSystolicResult = (TextView) findViewById(R.id.systolic_value);
        mDiastolicResult = (TextView) findViewById(R.id.diastolic_value);
        mArterialPressureResult = (TextView) findViewById(R.id.arterial_pressure_value);

        View wholeScreenView = ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        mGraph = new BpmGraph(mActivity, wholeScreenView);
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
                mSystolicResult.setText(getResources().getString(
                        R.string.non_applicable));
                mDiastolicResult.setText(getResources().getString(
                        R.string.non_applicable));
                mArterialPressureResult.setText(getResources().getString(
                        R.string.non_applicable));

                if (mGraph != null) {
                    mGraph.clearGraph();
                }

            }
        });
        mGraph.setStartTime(0);
    }

    @Override
    public void onUiBpmFound(boolean isFound) {
        if (!isFound) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            mActivity,
                            getResources().getString(R.string.bpm_full_name) + " " + getResources().getString(R.string.characteristic_not_found),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onUIBloodPressureRead(final float valueSystolic,
                                      final float valueDiastolic, final float valueArterialPressure) {
        mGraph.startTimer();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSystolicResult.setText(mBpmDeviceManager.getFormattedSystolicValue());
                mDiastolicResult.setText(mBpmDeviceManager.getFormattedDiastolicValue());
                mArterialPressureResult.setText(mBpmDeviceManager.getFormattedArterialPressureValue());

                mGraph.addNewData(valueSystolic, valueDiastolic,
                        valueArterialPressure);
            }
        });
    }
}
