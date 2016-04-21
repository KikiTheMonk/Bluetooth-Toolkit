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

package com.kyriakosalexandrou.bluetoothtoolkit.bt;

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
