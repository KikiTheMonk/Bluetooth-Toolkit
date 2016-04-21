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
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;

import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleSearch.BleSearchBaseHelper;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleSearch.BleSearchPostJellyBeanMr2Helper;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleSearch.BleSearchPostLollipopHelper;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.classic.BtcSearchHelper;

/**
 * This class is a wrapper for the Android BluetoothAdapter class
 * <p/>
 * Responsible for:
 * <ul>
 * <li>initialising the local Bluetooth hardware</li>
 * <li>initialising {@link BtCheckerHelper}</li>
 * <li>initialising {@link BleSearchBaseHelper}</li>
 * <li>initialising {@link BtcSearchHelper}</li>
 * </ul>
 */
public class BtAdapterHelper {
    private final static String TAG = "BtAdapterHelper";

    private BluetoothAdapter mBtAdapter;
    private BluetoothManager mBtManager;
    private BtCheckerHelper mBtCheckerHelper;
    private final Context mContext;
    private BleSearchBaseHelper mBleSearchBaseHelper;
    private BtcSearchHelper mBtcSearchHelper;

    public BtAdapterHelper(Context context) {
        if (context == null)
            throw new NullPointerException(
                    "context object passed is NULL");

        mContext = context;

        if (!initialiseBtAdapterAndManager(mContext))
            throw new NullPointerException(
                    "BT adapter could not be initialised");

        mBtCheckerHelper = new BtCheckerHelper(mContext, mBtAdapter);
    }

    /**
     * initialises the search helpers for BLE and BT classic and there callback
     * @param btAdapterHelperCallback
     */
    public void setCallback(BtAdapterHelperCallback btAdapterHelperCallback) {
        if (mBleSearchBaseHelper == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.w(TAG, "initialising post lollipop helper");
                mBleSearchBaseHelper = new BleSearchPostLollipopHelper(mBtAdapter, btAdapterHelperCallback);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Log.w(TAG, "initialising post jelly bean helper");
                mBleSearchBaseHelper = new BleSearchPostJellyBeanMr2Helper(mBtAdapter, btAdapterHelperCallback);
            }
            mBtcSearchHelper = new BtcSearchHelper(mContext, mBtAdapter, btAdapterHelperCallback);
        } else {
            mBleSearchBaseHelper.setCallback(btAdapterHelperCallback);
            mBtcSearchHelper.setCallback(btAdapterHelperCallback);
        }
    }

    public BluetoothManager getBtManager() {
        return mBtManager;
    }

    public BluetoothAdapter getBtAdapter() {
        return mBtAdapter;
    }

    public BtCheckerHelper getBtChecker() {
        return mBtCheckerHelper;
    }

    public BleSearchBaseHelper getBleSearchBaseHelper() {
        return mBleSearchBaseHelper;
    }

    public BtcSearchHelper getBtcSearchHelper() {
        return mBtcSearchHelper;
    }

    /**
     * Initialise local Bluetooth hardware.
     * <p/>
     * BluetoothAdapter must be initialised successfully before doing any
     * start/stop scanning operations.
     *
     * @return boolean false if it failed to initialise local Bluetooth
     * hardware, otherwise returns true.
     */
    public boolean initialiseBtAdapterAndManager(Context context) {
        boolean isInitialised;
        /**
         *From the android docs:
         *
         *-To get a BluetoothAdapter representing the local Bluetooth adapter, when running on JELLY_BEAN_MR1 and below,
         *call the static getDefaultAdapter() method;
         *-When running on JELLY_BEAN_MR2 and higher, retrieve it through getSystemService(String) with BLUETOOTH_SERVICE.
         */
        if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBtManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBtManager != null) {
                mBtAdapter = mBtManager.getAdapter();
                isInitialised = true;
            } else {
                isInitialised = false;
            }
        } else {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBtAdapter != null) {
                isInitialised = true;
            } else {
                isInitialised = false;
            }
        }

        if (isInitialised) {
            Log.w(TAG, "Successfully initialised BT adapter");
        } else {
            Log.w(TAG, "Failed to initialise BT adapter");
        }

        return isInitialised;
    }
}