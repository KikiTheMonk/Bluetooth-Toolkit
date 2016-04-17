package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.bloodpressuremeasurement;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseMultipleDevicesAdapter;

public class BpmMultipleDevicesAdapter extends
        BleBaseMultipleDevicesAdapter {
    private BpmDeviceManager mBpmDeviceManager = null;

    public BpmMultipleDevicesAdapter(Activity par) {
        super(par);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        super.getView(position, convertView, parent);

        FieldReferences fields;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.item_multiple_bpm_devices,
                    parent, false);
            fields = new FieldReferences();
            bindViews(fields, convertView);
            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }

        mBpmDeviceManager = (BpmDeviceManager) mBleBaseDeviceManager;

        if (mBpmDeviceManager != null) {
            setGenericViews(fields, position);
            setBloodPressureViews(fields);
        }

        return convertView;
    }

    private class FieldReferences extends CommonFieldReferences {
        TextView systolicValue;
        TextView diastolicValue;
        TextView arterialPressureValue;
    }

    private void bindViews(FieldReferences fields, View convertView) {
        bindCommonViews(fields, convertView);

        fields.systolicValue = (TextView) convertView
                .findViewById(R.id.systolic_value);
        fields.diastolicValue = (TextView) convertView
                .findViewById(R.id.diastolic_value);
        fields.arterialPressureValue = (TextView) convertView
                .findViewById(R.id.arterial_pressure_value);
    }


    private void setBloodPressureViews(final FieldReferences fields) {
        if (mBpmDeviceManager.getUnitType() != null) {
            fields.systolicValue.setText(mBpmDeviceManager.getFormattedSystolicValue());
            fields.diastolicValue.setText(mBpmDeviceManager.getFormattedDiastolicValue());
            fields.arterialPressureValue.setText(mBpmDeviceManager.getFormattedArterialPressureValue());
        } else {
            fields.systolicValue.setText(mContext.getString(R.string.non_applicable));
            fields.diastolicValue.setText(mContext.getString(R.string.non_applicable));
            fields.arterialPressureValue.setText(mContext.getString(R.string.non_applicable));
        }
    }
}