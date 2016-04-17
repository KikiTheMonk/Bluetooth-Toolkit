package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.healththermometermeasurement;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseMultipleDevicesAdapter;

public class HtmMultipleDevicesAdapter extends
        BleBaseMultipleDevicesAdapter {
    private HtmDeviceManager mHtmDeviceManager;

    public HtmMultipleDevicesAdapter(Activity par) {
        super(par);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        super.getView(position, convertView, parent);

        FieldReferences fields;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.item_multiple_htm_devices,
                    parent, false);

            fields = new FieldReferences();
            bindViews(fields, convertView);
            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }

//        if (mDevices.size() > position) {
//            mHtmDeviceManager = (HtmDeviceManager) mDevices.get(position);
//        }
        mHtmDeviceManager = (HtmDeviceManager) mDevices.get(position);

        if (mHtmDeviceManager != null) {
            // set proper values into the view
            setGenericViews(fields, position);
            setThermometerViews(fields);
        }
        return convertView;
    }

    private class FieldReferences extends CommonFieldReferences {
        TextView tempMeasurementValue;
    }

    private void bindViews(FieldReferences fields, View convertView) {
        bindCommonViews(fields, convertView);

        fields.tempMeasurementValue = (TextView) convertView
                .findViewById(R.id.tempMeasurementValue);
    }

    private void setThermometerViews(final FieldReferences fields) {
//        if (mHtmDeviceManager.getTempMeasurementValue() != null) {
            fields.tempMeasurementValue.setText(mHtmDeviceManager.getFormattedHtmValue() + mContext.getString(R.string.celsius_unit));
//        } else {
//            fields.tempMeasurementValue.setText(mContext.getString(R.string.dash));
//        }
    }
}