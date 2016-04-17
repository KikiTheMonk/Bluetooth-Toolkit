package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleSearch;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

/**
 * Callback's for BLE search operations.
 */
public interface BleSearchHelperCallback {

    void onBleStopScan();

    /**
     * Callback reporting a BLE device found during a scan that was initiated by one of the following
     * <br>
     * {@link BleSearchBaseHelper#startScan()}<br>
     * {@link BleSearchBaseHelper#startScan(UUID[])}<br>
     * {@link BleSearchBaseHelper#startBleScanPeriodically()}<br>
     * {@link BleSearchBaseHelper#startBleScanPeriodically(UUID[])}
     *
     * @param device     Identifies the remote device
     * @param rssi       The RSSI value for the remote device as reported by the
     *                   Bluetooth hardware. 0 if no RSSI value is available.
     * @param scanRecord The content of the advertisement record offered by the remote
     *                   device.
     */
    void onBleDeviceFound(final BluetoothDevice device, final int rssi,
                          final byte[] scanRecord);
}