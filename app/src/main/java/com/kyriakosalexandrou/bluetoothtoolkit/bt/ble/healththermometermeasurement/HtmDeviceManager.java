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

package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.healththermometermeasurement;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.os.Build;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseDeviceManagerUiCallback;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleUUIDs;

import java.util.UUID;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class HtmDeviceManager extends BleBaseDeviceManager {
    private static final String TAG = "HtmDeviceManager";

    private HtmDeviceManagerUICallback mHtmDeviceManagerUICallback;
    private float mTempMeasurementValue;
    private boolean isTempMeasurementFound = false;

    public HtmDeviceManager(
            BleBaseDeviceManagerUiCallback bleBaseDeviceManagerUiCallback,
            Activity activity) {
        super(activity, bleBaseDeviceManagerUiCallback);
        mHtmDeviceManagerUICallback = (HtmDeviceManagerUICallback) bleBaseDeviceManagerUiCallback;
    }

    public float getHtmValue() {
        return mTempMeasurementValue;
    }

    public String getFormattedHtmValue() {
        return mTempMeasurementValue + " " + mContext.getString(R.string.celsius_unit);
    }

    @Override
    protected void onCharFound(BluetoothGattCharacteristic characteristic) {
        if (BleUUIDs.Service.HEALTH_THERMOMETER
                .equals(characteristic.getService().getUuid())) {
            if (BleUUIDs.Characteristic.TEMPERATURE_MEASUREMENT
                    .equals(characteristic.getUuid())) {
                isTempMeasurementFound = true;
                addCharToQueue(characteristic);
            }
        } else {
            super.onCharFound(characteristic);
        }
    }

    @Override
    protected void onCharsFoundCompleted() {
        mHtmDeviceManagerUICallback.onUiHtmFound(isTempMeasurementFound);

        if (!isTempMeasurementFound) {
            disconnect();
        } else {
            // call execute after all services added to queue
            executeCharacteristicsQueue();
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                        int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    readRssiPeriodicaly(true, RSSI_UPDATE_INTERVAL);
                    break;

                case BluetoothProfile.STATE_DISCONNECTED:
                    isTempMeasurementFound = false;
                    break;
            }
        } else {
            isTempMeasurementFound = false;
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic ch) {
        super.onCharacteristicChanged(gatt, ch);
        UUID currentCharUUID = ch.getUuid();

        if (BleUUIDs.Characteristic.TEMPERATURE_MEASUREMENT.equals(currentCharUUID)) {
            mTempMeasurementValue = ch.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
            mHtmDeviceManagerUICallback.onUiTemperatureChange(mTempMeasurementValue);
        }
    }
}
