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