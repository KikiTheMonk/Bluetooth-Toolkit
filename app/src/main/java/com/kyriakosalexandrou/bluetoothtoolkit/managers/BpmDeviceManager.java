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

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.kyriakosalexandrou.bluetoothtoolkit.BleUUIDs;

import java.util.UUID;

public class BpmDeviceManager extends BleBaseDeviceManager {
    private static final String TAG = "BpmDeviceManager";

    private BpmDeviceManagerUiCallback mBpmDeviceManagerUiCallback;

    private float mValueSystolic, mValueDiastolic, mValueArterialPressure;
    private String mUnitType = "";
    private boolean isBpMeasurementFound = false;

    public BpmDeviceManager(
            BleBaseDeviceManagerUiCallback bleBaseDeviceManagerUiCallback,
            Activity activity) {
        super(activity, bleBaseDeviceManagerUiCallback);
        mBpmDeviceManagerUiCallback = (BpmDeviceManagerUiCallback) bleBaseDeviceManagerUiCallback;
    }

    public float getSystolicValue() {
        return mValueSystolic;
    }

    public float getDiastolicValue() {
        return mValueDiastolic;
    }

    public float getArterialPressureValue() {
        return mValueArterialPressure;
    }

    public String getFormattedSystolicValue() {
        return mValueSystolic + "" + getUnitType();
    }

    public String getFormattedDiastolicValue() {
        return mValueDiastolic + "" + getUnitType();
    }

    public String getFormattedArterialPressureValue() {
        return mValueArterialPressure + "" + getUnitType();
    }

    public String getUnitType() {
        return mUnitType;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                        int newState) {
        super.onConnectionStateChange(gatt, status, newState);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:

                    mValueSystolic = 0;
                    mValueDiastolic = 0;
                    mValueArterialPressure = 0;

                    readRssiPeriodicaly(true, RSSI_UPDATE_INTERVAL);

                    break;

                case BluetoothProfile.STATE_DISCONNECTED:
                    isBpMeasurementFound = false;
                    break;
            }
        } else {
            isBpMeasurementFound = false;
        }
    }

    @Override
    protected void onCharFound(BluetoothGattCharacteristic characteristic) {
        if (BleUUIDs.Service.BLOOD_PRESSURE.equals(characteristic
                .getService().getUuid())) {
            if (BleUUIDs.Characteristic.BLOOD_PRESSURE_MEASUREMENT
                    .equals(characteristic.getUuid())) {
                addCharToQueue(characteristic);
                isBpMeasurementFound = true;
            }
        } else {
            super.onCharFound(characteristic);
        }
    }

    @Override
    protected void onCharsFoundCompleted() {
        if (!isBpMeasurementFound) {
            disconnect();
        } else {
            // call execute after all services added to queue
            executeCharacteristicsQueue();
        }

        mBpmDeviceManagerUiCallback.onUiBpmFound(isBpMeasurementFound);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic ch) {
        super.onCharacteristicChanged(gatt, ch);

        UUID currentCharUUID = ch.getUuid();
        if (BleUUIDs.Characteristic.BLOOD_PRESSURE_MEASUREMENT
                .equals(currentCharUUID)) {
            mValueSystolic = ch.getFloatValue(
                    BluetoothGattCharacteristic.FORMAT_SFLOAT, 1);
            mValueDiastolic = ch.getFloatValue(
                    BluetoothGattCharacteristic.FORMAT_SFLOAT, 3);
            mValueArterialPressure = ch.getFloatValue(
                    BluetoothGattCharacteristic.FORMAT_SFLOAT, 5);

            identifyAndSetUnitType(ch);

            mBpmDeviceManagerUiCallback.onUIBloodPressureRead(
                    mValueSystolic, mValueDiastolic, mValueArterialPressure);
        }
    }

    private void identifyAndSetUnitType(
            BluetoothGattCharacteristic bloodPressureChar) {
        /*
         * first byte is the flag bloodPressureChar.getValue()[0] and first bit
		 * of that byte is the unit type (bloodPressureChar.getValue()[0] & 1)
		 * That is why we do a bitwise operation with 1.
		 * 
		 * https://developer.bluetooth.org/gatt/characteristics/Pages/
		 * CharacteristicViewer
		 * .aspx?u=org.bluetooth.characteristic.blood_pressure_measurement.xml
		 */
        byte[] unitType = bloodPressureChar.getValue();

        boolean kpa = ((unitType[0] & 1) == 1 ? true : false);

        Log.i(TAG, "is it in kpa: " + kpa);

        if (kpa) {
            mUnitType = "kpa";
        } else {
            mUnitType = "mmHg";
        }
    }
}