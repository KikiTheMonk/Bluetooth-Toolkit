package com.kyriakosalexandrou.bluetoothtoolkit.bt.classic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.kyriakosalexandrou.bluetoothtoolkit.bt.BtAdapterHelperCallback;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.BtSearchHelper;

/**
 * BT classic search helper
 */
public class BtcSearchHelper extends BtSearchHelper {
    private final static String TAG = "BtcSearchHelper";

    private Context mContext;
    private BluetoothAdapter mBtAdapter;
    private BtcSearchHelperCallback mBtcSearchHelperCallback;

    /**
     * @param context                 the context
     * @param btAdapterHelperCallback class to give callbacks to
     * @throws NullPointerException if one of the parameters is null
     */
    public BtcSearchHelper(Context context,
                           BluetoothAdapter btAdapter,
                           BtAdapterHelperCallback btAdapterHelperCallback) {
        if (context == null || btAdapterHelperCallback == null)
            throw new NullPointerException(
                    "context or btAdapterHelperCallback object passed is NULL");

        mContext = context;
        mBtAdapter = btAdapter;
        mBtcSearchHelperCallback = btAdapterHelperCallback;
    }

//    public BluetoothAdapter getBluetoothAdapter() {
//        return mBtAdapter;
//    }

//    /**
//     * checking if a discovery is in progress, discovery scans for all Bluetooth
//     * device types
//     *
//     * @return true if discovering
//     */
//    public boolean isDiscovering() {
//        return mIsBtcScanning;
////        return mBtAdapter.isDiscovering();
//    }

    /**
     * initiates BT Classic scan operation. The scan time operation is 12
     * seconds and this cannot be changed. <br/>
     * The method
     * {@link BtAdapterHelperCallback#onDiscoveryDeviceFound(BluetoothDevice, int)}
     * will be called when a device is found
     *
     * @return true on success, false on error
     */
    public boolean startDiscovery() {
        boolean isDiscoveryInitiated = mBtAdapter.startDiscovery();

        if (isDiscoveryInitiated) {
            mIsScanning = true;
            Log.v(TAG, "startDiscovery success");
            registerReceiver();
        }
        return isDiscoveryInitiated;
    }

    /**
     * Cancel the current device discovery process. <br>
     * Because discovery is a heavyweight procedure for the Bluetooth adapter,
     * this method should always be called before attempting to connect to a
     * remote device.
     *
     * @return true on success, false on error
     */
    public boolean stopDiscovery() {
        mIsScanning = false;
        return mBtAdapter.cancelDiscovery();
    }

    /**
     * Create a BroadcastReceiver for ACTION_FOUND callback for discovery
     * operations (Bluetooth classic and BLE devices scanning)
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mIsScanning = true;
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                mIsScanning = false;
                mBtcSearchHelperCallback.onDiscoveryStop();
                unregisterReceiver();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // When discovery inquiry finds a device
                // Get the BluetoothDevice object from the Intent
                int rssi = (int) intent.getShortExtra(
                        BluetoothDevice.EXTRA_RSSI, (short) 0);
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mBtcSearchHelperCallback.onDiscoveryDeviceFound(device,
                        rssi);
            }
        }
    };

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }

    public void setCallback(BtAdapterHelperCallback callback) {
        this.mBtcSearchHelperCallback = callback;
    }
}
