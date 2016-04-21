package com.kyriakosalexandrou.bluetoothtoolkit.ui.activities;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.btc.SPPManager;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.btc.SPPManagerUiCallback;
import com.kyriakosalexandrou.bluetoothtoolkit.ui.components.ClearableEditTextComponent;

public class SppActivity extends BtcBaseActivity implements SPPManagerUiCallback {
    private static final String TAG = "SppActivity";

    private SPPManager mSppManager;

    private boolean isPrefClearTextAfterSending = false;
    private boolean isPrefSendCR = true;

    private int mCounterRx = 0;
    private int mCounterTx = 0;

    private Button mBtnSend;

    private ScrollView mScrollViewConsoleOutput;
    private ClearableEditTextComponent mInputBox;
    private TextView mValueConsoleOutputTv, mValueRxCounterTv,
            mValueTxCounterTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_btc_spp);
        initialiseDialogAbout(getResources().getString(R.string.about_spp));
        initialiseDialogFoundDevices(getString(R.string.bt_classic), getResources().getDrawable(R.drawable.ic_toolbar_spp));
        mSppManager = new SPPManager(getApplicationContext(), this);
    }

    @Override
    public void bindViews() {
        super.bindViews();
        mBtnSend = (Button) findViewById(R.id.btnSend);

        mScrollViewConsoleOutput = (ScrollView) findViewById(R.id.scrollViewConsoleOutput);

        mInputBox = (ClearableEditTextComponent) findViewById(R.id.inputBox);
        mValueConsoleOutputTv = (TextView) findViewById(R.id.valueConsoleOutputTv);
        mValueRxCounterTv = (TextView) findViewById(R.id.valueRxCounterTv);
        mValueTxCounterTv = (TextView) findViewById(R.id.valueTxCounterTv);
    }

    @Override
    public void setListeners() {
        super.setListeners();

        mBtnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = null;

                if (isPrefSendCR) {
                    data = mInputBox.getEditText().getText().toString() + "\r";
                } else if (!isPrefSendCR) {
                    data = mInputBox.getEditText().getText().toString();
                }

                if (data != null) {
                    if (mValueConsoleOutputTv.getText().length() <= 0) {
                        mValueConsoleOutputTv.append(data + "\n");
                    } else {
                        mValueConsoleOutputTv.append(data + "\n");
                    }

                    mCounterTx += data.length();

                    mSppManager.writeDataToRemoteDevice((data).getBytes());

                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    if (isPrefClearTextAfterSending) {
                        mInputBox.getEditText().setText("");
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mValueTxCounterTv.setText("" + mCounterTx);
                            mScrollViewConsoleOutput.smoothScrollTo(0,
                                    mValueConsoleOutputTv.getBottom());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDialogFoundDevicesItemClick(AdapterView<?> arg0,
                                              View view, int position, long id) {
        super.onDialogFoundDevicesItemClick(arg0, view, position, id);
        final BluetoothDevice device = mDialogFoundBtDevices.getListFoundDevicesHandler()
                .getDevice(position);
        getBtAdapterHelper().getBtcSearchHelper().stopDiscovery();
        mDialogFoundBtDevices.dismiss();
        if (device == null)
            return;

        mSppManager.setBluetoothDevice(device);
        mSppManager.connect();
        uiInvalidateViewsState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.spp, menu);
        getActionBar().setIcon(R.drawable.ic_toolbar_spp);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                mValueConsoleOutputTv.setText("");

                mCounterRx = 0;
                mCounterTx = 0;

                mValueRxCounterTv.setText("" + mCounterRx);
                mValueTxCounterTv.setText("" + mCounterTx);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void uiInvalidateViewsState() {
        super.uiInvalidateViewsState();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mSppManager == null) {
                    mBtnSend.setEnabled(false);
                } else if (!mSppManager.isConnected()
                        && !mSppManager.isConnecting()) {
                    mBtnSend.setEnabled(false);
                } else if (!mSppManager.isConnected()
                        && mSppManager.isConnecting()) {
                    mBtnSend.setEnabled(false);
                } else if (mSppManager.isConnected()
                        && !mSppManager.isConnecting()) {
                    mBtnSend.setEnabled(true);
                }

                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onUiRemoteDeviceRead(final String result) {
        mCounterRx += result.length();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mValueConsoleOutputTv.append(result);
                mValueRxCounterTv.setText("" + mCounterRx);
                mScrollViewConsoleOutput.smoothScrollTo(0,
                        mValueConsoleOutputTv.getBottom());
            }
        });
    }

    @Override
    protected void loadPref() {
        super.loadPref();
        isPrefClearTextAfterSending = mSharedPreferences.getBoolean(
                "pref_clear_text_after_sending", false);
        isPrefSendCR = mSharedPreferences.getBoolean(
                "pref_append_/r_at_end_of_data", true);
    }
}