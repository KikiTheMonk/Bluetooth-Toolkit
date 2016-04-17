package com.kyriakosalexandrou.bluetoothtoolkit.bt.classic;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.BtBaseActivity;

/**
 * Base activity for all the activities that will be doing BT classic functionalities
 *
 * @author Kyriakos.Alexandrou
 */
public abstract class BtcBaseActivity extends BtBaseActivity implements
        BtcBaseDeviceManagerUiCallback {
    private final static String TAG = "BtcBaseActivity";
    private BtcBaseDeviceManager mBtcBaseDeviceManager;

    @Override
    public void onCreate(Bundle savedInstanceState, int layoutResID) {
        super.onCreate(savedInstanceState, layoutResID);
    }

    @Override
    public void bindViews() {
        super.bindViews();
    }

    @Override
    protected void setListeners() {
        mBtnScan.setOnClickListener(new ScanBtnListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_scan: {
                        if (!super.onClick()) {
                            return;
                        }

                        if (!mBtcBaseDeviceManager.isConnected()
                                && !mBtcBaseDeviceManager.isConnecting()) {
                        /*
                         * not connected
						 */
                            getBtAdapterHelper().getBtcSearchHelper().startDiscovery();
                            mDialogFoundBtDevices.show();
                        } else if (mBtcBaseDeviceManager.isConnected()
                                && !mBtcBaseDeviceManager.isConnecting()) {
                        /*
                         * connected
						 */
                            mBtcBaseDeviceManager.disconnect();
                        }

                        uiInvalidateViewsState();
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isPrefRunInBackground) {
            // let the app run normally in the background
        } else {
            // stop scanning or disconnect if we are connected
            if (getBtAdapterHelper().getBtcSearchHelper().isScanning()) {
                getBtAdapterHelper().getBtcSearchHelper().stopDiscovery();
            } else if (mBtcBaseDeviceManager.isConnected()) {
                mBtcBaseDeviceManager.disconnect();
            }
        }
    }

    @Override
    public void uiInvalidateViewsState() {
        super.uiInvalidateViewsState();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mBtcBaseDeviceManager == null) {
                    mBtnScan.setText(R.string.btn_scan);
                } else if (!mBtcBaseDeviceManager.isConnected()
                        && !mBtcBaseDeviceManager.isConnecting()) {
                    mBtnScan.setText(R.string.btn_scan);
                } else if (!mBtcBaseDeviceManager.isConnected()
                        && mBtcBaseDeviceManager.isConnecting()) {
                    mBtnScan.setText(R.string.btn_connecting);
                } else if (mBtcBaseDeviceManager.isConnected()
                        && !mBtcBaseDeviceManager.isConnecting()) {
                    mBtnScan.setText(R.string.btn_disconnect);
                }
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onDialogFoundDevicesItemClick(AdapterView<?> arg0,
                                              View view, int position, long id) {
    }

    @Override
    public void onDialogFoundDevicesCancel(DialogInterface arg0) {
        getBtAdapterHelper().getBtcSearchHelper().stopDiscovery();
        super.onDialogFoundDevicesCancel(arg0);
    }

    @Override
    public void onBackPressed() {
        if (mBtcBaseDeviceManager != null && mBtcBaseDeviceManager.isConnected()) {
            mBtcBaseDeviceManager.disconnect();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                if (mBtcBaseDeviceManager != null && mBtcBaseDeviceManager.isConnected()) {
                    mBtcBaseDeviceManager.disconnect();
                    invalidateOptionsMenu();
                } else {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUiDeviceManagerInitialised(BtcBaseDeviceManager btcBaseDeviceManager) {
        mBtcBaseDeviceManager = btcBaseDeviceManager;
    }

    @Override
    public void onUiBtcRemoteDeviceConnected() {
        uiInvalidateViewsState();
    }

    @Override
    public void onUiBtcRemoteDeviceDisconnected() {
        uiInvalidateViewsState();
    }

    @Override
    public void onUiBtcConnectFailed() {
        uiInvalidateViewsState();
    }
}