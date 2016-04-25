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

package com.kyriakosalexandrou.bluetoothtoolkit.managers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.os.Build;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.BleNamesResolver;
import com.kyriakosalexandrou.bluetoothtoolkit.BleUUIDs;

import java.util.UUID;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class HrmDeviceManager extends BleBaseDeviceManager {

    private static final String TAG = "HrmDeviceManager";
    private static final int BODY_SENSOR_LOCATION_FAILED_VALUE = 0;
    private HrmDeviceManagerUiCallback mHrActivityUiCallback;

    private int mHrmValue, mBodySensorLocationValue;
    private boolean isHrMeasurementFound = false;

    public HrmDeviceManager(
            BleBaseDeviceManagerUiCallback bleBaseDeviceManagerUiCallback,
            Activity mActivity) {
        super(mActivity, bleBaseDeviceManagerUiCallback);
        mHrActivityUiCallback = (HrmDeviceManagerUiCallback) bleBaseDeviceManagerUiCallback;
    }

    public int getBodySensorLocationValue() {
        return mBodySensorLocationValue;
    }

    public int getHrmValue() {
        return mHrmValue;
    }

    public String getFormattedHtmValue() {
        return getHrmValue() + mContext.getString(R.string.beats_per_minute);
    }

    public String getFormattedBodySensorValue() {
        return BleNamesResolver.resolveHeartRateSensorLocation(getBodySensorLocationValue());
    }

    @Override
    protected void onCharFound(BluetoothGattCharacteristic characteristic) {
        if (BleUUIDs.Service.HEART_RATE.equals(characteristic
                .getService().getUuid())) {
            if (BleUUIDs.Characteristic.HEART_RATE_MEASUREMENT
                    .equals(characteristic.getUuid())) {
                isHrMeasurementFound = true;
                addCharToQueue(characteristic);

            } else if (BleUUIDs.Characteristic.BODY_SENSOR_LOCATION
                    .equals(characteristic.getUuid())) {
                addCharToQueue(characteristic);
            }
        } else {
            super.onCharFound(characteristic);
        }
    }

    @Override
    protected void onCharsFoundCompleted() {
        mHrActivityUiCallback.onUiHrmFound(isHrMeasurementFound);

        if (!isHrMeasurementFound) {
            disconnect();
        } else {
            // call execute after all characteristics added to queue
            executeCharacteristicsQueue();
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);

        UUID characteristicUUID = characteristic.getUuid();

        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:

                if (BleUUIDs.Characteristic.BODY_SENSOR_LOCATION
                        .equals(characteristicUUID)) {
                    int result = characteristic.getIntValue(
                            BluetoothGattCharacteristic.FORMAT_UINT8, 0);

                    mBodySensorLocationValue = result;
                    mHrActivityUiCallback
                            .onUiBodySensorLocation(mBodySensorLocationValue);
                }

                break;

            default:
                // failed for reasons other than requiring bonding etc.
                if (BleUUIDs.Characteristic.BODY_SENSOR_LOCATION
                        .equals(characteristicUUID)) {
                    mBodySensorLocationValue = BODY_SENSOR_LOCATION_FAILED_VALUE;
                    mHrActivityUiCallback.onUiBodySensorLocation(mBodySensorLocationValue);
                }
                break;
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
                    isHrMeasurementFound = false;
                    break;
            }
        } else {
            isHrMeasurementFound = false;
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic ch) {
        super.onCharacteristicChanged(gatt, ch);
        UUID currentCharUUID = ch.getUuid();

        if (BleUUIDs.Characteristic.HEART_RATE_MEASUREMENT
                .equals(currentCharUUID)) {
            mHrmValue = ch.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
            mHrActivityUiCallback.onUiHRM(mHrmValue);
        }
    }
}
