package com.kyriakosalexandrou.bluetoothtoolkit.bt;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

/**
 * <li>Checking if the BT hardware is currently enabled</li>
 * <li>Checking if BT Classic, BLE central and peripheral modes are supported</li>
 */
public class BtCheckerHelper {
    private final static String TAG = "BtCheckerHelper";
    private final BluetoothAdapter mBluetoothAdapter;
    private final Context mContext;

    public BtCheckerHelper(
            Context context, BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null)
            throw new NullPointerException("bluetoothAdapter parameter is null");

        mBluetoothAdapter = bluetoothAdapter;
        mContext = context;
    }

    /**
     * Before any action check if BT is turned ON and enabled, call this in
     * onResume to be always sure that BT is ON when Your application is put
     * into the foreground
     * <p/>
     * <p/>
     * Note: Also always check if BT in enabled just before a scanning procedure
     * starts. The user could have disabled BT without actually sending the
     * activity to the background, if this happens then the onResume will not be
     * called and the app would try to do BT stuff without BT being enabled.
     *
     * @return boolean false if not currently enabled, true otherwise.
     */
    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * check if this device supports BT Classic.
     *
     * @return boolean false if BT classic is not supported, otherwise true
     */
    public boolean checkBtClassicSupport() {
        boolean hasBt = mContext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH);
        Log.i(TAG, "BT Classic support: " + hasBt);
        return hasBt;
    }

    /**
     * check if this device has BT Classic and BLE hardware functionality.
     *
     * @return boolean false if BLE hardware is not available, otherwise true
     */
    public boolean checkBleCentralSupport() {
        boolean hasBle = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            hasBle = mContext.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_BLUETOOTH_LE);
        }
        Log.i(TAG, "BLE Central support: " + hasBle);
        return hasBle;
    }

    /**
     * checks if this device supports peripheral mode (advertisements)
     *
     * @return true if supports peripheral mode, false otherwise
     */
    public boolean checkBlePeripheralSupport() {
        boolean hasPeripheral = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hasPeripheral = mBluetoothAdapter
                    .isMultipleAdvertisementSupported();
        }
        Log.i(TAG, "BLE peripheral support: " + hasPeripheral);

        return hasPeripheral;
    }
}
