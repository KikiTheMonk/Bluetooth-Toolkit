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