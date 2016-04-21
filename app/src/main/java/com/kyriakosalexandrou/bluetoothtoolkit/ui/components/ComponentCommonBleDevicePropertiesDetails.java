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
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.base.BleBaseDeviceManager;

/**
 * component used to show the generic BLE properties such us device name, address, battery, rssi etc.
 */
public class ComponentCommonBleDevicePropertiesDetails extends LinearLayout {
    protected TextView mNameValueTv, mAddressValueTv, mRssiValueTv, mBatteryValueTv;
    protected ImageView mRssiIcon, mBatteryIcon;
    private BleBaseDeviceManager mBleBaseDeviceManager;

    public ComponentCommonBleDevicePropertiesDetails(Context context) {
        this(context, null);
    }

    public ComponentCommonBleDevicePropertiesDetails(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComponentCommonBleDevicePropertiesDetails(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ComponentCommonBleDevicePropertiesDetails(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = UtilHelper.getLayoutInflater(getContext());
        View view = inflater.inflate(R.layout.component_common_ble_device_properties_details, this, true);
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