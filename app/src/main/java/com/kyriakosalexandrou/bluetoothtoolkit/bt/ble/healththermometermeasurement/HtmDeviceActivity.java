package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.healththermometermeasurement;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseSingleDeviceActivity;

public class HtmDeviceActivity extends BleBaseSingleDeviceActivity implements
        HtmDeviceManagerUICallback {
    private HtmDeviceManager mHtmDeviceManager;
    private HtmGraph mGraph;
    private TextView mValueTempMeasurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_htm, new HtmDeviceManager(this, this));

        mHtmDeviceManager = (HtmDeviceManager) mBleBaseDeviceManager;
//        setBleBaseDeviceManager(mHtmDeviceManager);

        initialiseDialogAbout(getResources().getString(
                R.string.about_thermometer));
        initialiseDialogFoundDevices(getString(R.string.thermometer), getResources().getDrawable(R.drawable.ic_htm));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.htm, menu);
        getActionBar().setIcon(R.drawable.ic_htm);
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

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                updateCommonViews();
//            }
//        });
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
