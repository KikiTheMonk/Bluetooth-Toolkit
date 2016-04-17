package com.kyriakosalexandrou.bluetoothtoolkit.bt;

import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleSearch.BleSearchHelperCallback;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.classic.BtcSearchHelperCallback;

/**
 * Callback's for BT classic and BLE search operations
 */
public interface BtAdapterHelperCallback extends BtcSearchHelperCallback, BleSearchHelperCallback {
}