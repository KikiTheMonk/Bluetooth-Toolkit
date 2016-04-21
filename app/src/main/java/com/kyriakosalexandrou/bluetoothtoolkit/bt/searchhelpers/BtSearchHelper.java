package com.kyriakosalexandrou.bluetoothtoolkit.bt.searchhelpers;

import android.bluetooth.BluetoothAdapter;

public abstract class BtSearchHelper {
    /**
     * This is used for BLE and BT Classic scanning operation.
     * The BT Classic API already has methods to check if a discovery
     * (a BT classic search operation) is in process BUT it seems that it does not immediately
     * get set to true. It gets set to true ONLY after the BroadcastReceiver receives the action
     * {@link BluetoothAdapter#ACTION_DISCOVERY_STARTED} and this seems to be a problem as it
     * doesn't happen immediately
     */
    protected boolean mIsScanning = false;

    /**
     * Checks if currently a scanning operation is running
     *
     * @return true if scanning, otherwise false
     */
    public boolean isScanning() {
        return mIsScanning;
    }

}
