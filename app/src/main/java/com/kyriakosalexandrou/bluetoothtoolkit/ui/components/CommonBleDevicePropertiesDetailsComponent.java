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

package com.kyriakosalexandrou.bluetoothtoolkit.ui.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.UtilHelper;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseDeviceManager;

/**
 * component used to show the generic BLE properties such us device name, address, battery, rssi etc.
 */
public class CommonBleDevicePropertiesDetailsComponent extends LinearLayout {
    protected TextView mNameValueTv, mAddressValueTv, mRssiValueTv, mBatteryValueTv;
    protected ImageView mRssiIcon, mBatteryIcon;
    private BleBaseDeviceManager mBleBaseDeviceManager;

    public CommonBleDevicePropertiesDetailsComponent(Context context) {
        this(context, null);
    }

    public CommonBleDevicePropertiesDetailsComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonBleDevicePropertiesDetailsComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CommonBleDevicePropertiesDetailsComponent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = UtilHelper.getLayoutInflater(getContext());
        View view = inflater.inflate(R.layout.common_ble_device_properties_details_component, this, true);
        bindViews(view);
    }

    private void bindViews(View view) {
        mNameValueTv = (TextView) findViewById(R.id.device_name_value);
        mAddressValueTv = (TextView) findViewById(R.id.device_address_value);
        mRssiValueTv = (TextView) findViewById(R.id.rssi_value);
        mBatteryValueTv = (TextView) findViewById(R.id.battery_value);

        mBatteryIcon = (ImageView) findViewById(R.id.battery_icon);
        mRssiIcon = (ImageView) findViewById(R.id.rssi_icon);
    }

    public String getFormattedBatteryValue() {
        return mBleBaseDeviceManager.getBatteryValue() + getContext().getString(R.string.battery_unit);
    }

    public String getFormattedRssiValue() {
        return mBleBaseDeviceManager.getRssiValue() + getContext().getString(R.string.rssi_unit);
    }

    public void setBleDeviceManager(BleBaseDeviceManager bleBaseDeviceManager) {
        mBleBaseDeviceManager = bleBaseDeviceManager;
    }

    public void updateCommonViews() {
        if(mBleBaseDeviceManager == null) return;

        String name = mBleBaseDeviceManager.getBluetoothDevice().getName();
        String address = mBleBaseDeviceManager.getBluetoothDevice().getAddress();
        if (name == null || name.length() <= 0)
            name = getContext().getString(R.string.unknown_device);

        mNameValueTv.setText(name);
        mAddressValueTv.setText(address);

        setRssiIconAndValue(mBleBaseDeviceManager.getRssiValue());
        setBatteryIconAndValue(mBleBaseDeviceManager.getBatteryValue());
    }

    public void setCommonViewsToNonApplicable() {
        mBatteryValueTv.setText(getResources().getString(R.string.non_applicable));
        mNameValueTv.setText(getResources().getString(R.string.non_applicable));
        mRssiValueTv.setText(getResources().getString(R.string.non_applicable));
        mAddressValueTv.setText(getResources().getString(
                R.string.non_applicable));
        mBatteryIcon.setImageResource(R.drawable.ic_battery_unknown);
        mRssiIcon.setImageResource(R.drawable.ic_rssi_unknown);
    }

    private void setBatteryIconAndValue(int batteryPercentage) {
        mBatteryValueTv.setText(getFormattedBatteryValue());

        if (batteryPercentage >= 95) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_full);
        } else if (batteryPercentage >= 90) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_90);
        } else if (batteryPercentage >= 80) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_80);
        } else if (batteryPercentage >= 70) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_70);
        } else if (batteryPercentage >= 60) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_60);
        } else if (batteryPercentage >= 50) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_50);
        } else if (batteryPercentage >= 40) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_40);
        } else if (batteryPercentage >= 30) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_30);
        } else if (batteryPercentage >= 20) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_20);
        } else if (batteryPercentage >= 10) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_10);
        } else if (batteryPercentage >= 0) {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_lees_than_10);
        } else {
            mBatteryIcon.setImageResource(R.drawable.ic_battery_unknown);
        }
    }

    private void setRssiIconAndValue(int rssiValue) {
        mRssiValueTv.setText(getFormattedRssiValue());

        if (rssiValue >= -26) {
            mRssiIcon.setImageResource(R.drawable.ic_rssi_4);
        } else if (rssiValue >= -40) {
            mRssiIcon.setImageResource(R.drawable.ic_rssi_3);
        } else if (rssiValue >= -60) {
            mRssiIcon.setImageResource(R.drawable.ic_rssi_2);
        } else if (rssiValue >= -80) {
            mRssiIcon.setImageResource(R.drawable.ic_rssi_1);
        } else if (rssiValue >= -100) {
            mRssiIcon.setImageResource(R.drawable.ic_rssi_0);
        }
    }
}