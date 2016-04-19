package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.heartratemeasurement;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.BleBaseMultipleDevicesAdapter;

public class HrmMultipleDevicesAdapter extends
        BleBaseMultipleDevicesAdapter {
    private HrmDeviceManager mHrmDeviceManager;

    public HrmMultipleDevicesAdapter(Activity par) {
        super(par);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        super.getView(position, convertView, parent);

        FieldReferences fields;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.item_multiple_hrm_devices,
                    parent, false);

            fields = new FieldReferences();
            bindViews(fields, convertView);
            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }

        mHrmDeviceManager = (HrmDeviceManager) mDevices.get(position);

        if (mHrmDeviceManager != null) {
            setGenericViews(fields, position);
            setHeartRateViews(fields);
        }
        return convertView;
    }

    private class FieldReferences extends CommonFieldReferences {
        TextView bodySensorValue;
        TextView hrmValue;
    }

    private void bindViews(FieldReferences fields, View convertView) {
        bindCommonViews(fields, convertView);

        fields.bodySensorValue = (TextView) convertView
                .findViewById(R.id.valueBodySensor);
        fields.hrmValue = (TextView) convertView.findViewById(R.id.valueHRM);
    }

    private void setHeartRateViews(final FieldReferences fields) {
        fields.hrmValue.setText(mHrmDeviceManager.getFormattedHtmValue());
        fields.bodySensorValue.setText(mHrmDeviceManager.getFormattedBodySensorValue());
    }
}