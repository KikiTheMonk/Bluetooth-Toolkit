package com.kyriakosalexandrou.bluetoothtoolkit.bt;

import com.kyriakosalexandrou.bluetoothtoolkit.bt.searchhelpers.blesearch.BleSearchHelperCallback;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.searchhelpers.btcsearch.BtcSearchHelperCallback;

/**
 * Callback's for BT classic and BLE search operations
 */
public interface BtAdapterHelperCallback extends BtcSearchHelperCallback, BleSearchHelperCallback {
}