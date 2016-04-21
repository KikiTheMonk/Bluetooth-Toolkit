package com.kyriakosalexandrou.bluetoothtoolkit.bt.searchhelpers.btcsearch;

import android.bluetooth.BluetoothDevice;

/**
 * Callback's for BT classic search operations.
 */
public interface BtcSearchHelperCallback {

    /**
     * callback indicating that a BT classic scanning operation stopped
     */
    void onDiscoveryStop();

    /**
     * Called when a BT classic device was found during a scan.
     *
     * @param device The BT device
     * @param rssi   The RSSI value for the remote device as reported by the
     *               Bluetooth hardware. 0 if no RSSI value is available.
     */
    void onDiscoveryDeviceFound(final BluetoothDevice device,final int rssi);
}