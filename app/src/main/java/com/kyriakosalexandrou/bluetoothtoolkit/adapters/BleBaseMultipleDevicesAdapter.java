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

package com.kyriakosalexandrou.bluetoothtoolkit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.helpers.UtilHelper;
import com.kyriakosalexandrou.bluetoothtoolkit.managers.BleBaseDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.ui.components.CommonBleDevicePropertiesDetailsComponent;

import java.util.ArrayList;

/**
 * Base adapter for devices that can connect to multiple BLE devices
 *
 * @author Kyriakos.Alexandrou
 */
public class BleBaseMultipleDevicesAdapter extends BaseAdapter {
    protected final ArrayList<BleBaseDeviceManager> mDevices;
    protected final LayoutInflater mInflater;
    protected final Context mContext;
    protected BleBaseDeviceManager mBleBaseDeviceManager;

    public BleBaseMultipleDevicesAdapter(Context context) {
        super();
        mDevices = new ArrayList<>();
        mContext = context;
        mInflater = UtilHelper.getLayoutInflater(mContext);
    }

    /**
     * Adds a BLE device into the list
     *
     * @param device the device to add to the list
     * @return true if device was added to the list, otherwise returns false
     */
    public boolean addDevice(BleBaseDeviceManager device) {
        if (!mDevices.contains(device)) {
            mDevices.add(device);
            return true;
        } else {
            return false;
        }
    }

    public BleBaseDeviceManager getDevice(int index) {
        return mDevices.get(index);
    }

    public ArrayList<BleBaseDeviceManager> getDevices() {
        return mDevices;
    }

    public void clearList() {
        mDevices.clear();
    }

    public void disconnectFromDevices() {
        for (int i = 0; i < getCount(); i++) {
            mDevices.get(i).disconnect();
        }
    }

    public void remove(int position) {
        /*
         * making sure we are not going to try and get a device that was already
		 * removed from the list
		 */
        if (mDevices.size() > position) {
            mDevices.remove(position);
        }
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return getDevice(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * making sure we are not going to try and get a device that was removed
		 * from the list
		 */
        if (mDevices.size() > position) {
            mBleBaseDeviceManager = mDevices.get(position);
        }
        return convertView;
    }

    protected void bindCommonViews(CommonFieldReferences fields, View convertView) {
        fields.commonBleDevicePropertiesDetailsComponent =
                (CommonBleDevicePropertiesDetailsComponent)
                        convertView.findViewById(R.id.common_ble_properties_component);
    }

    protected void setGenericViews(final CommonFieldReferences fields, int position) {
        fields.commonBleDevicePropertiesDetailsComponent.
                setBleDeviceManager(mDevices.get(position));

        fields.commonBleDevicePropertiesDetailsComponent.updateCommonViews();
    }

    protected class CommonFieldReferences {
        CommonBleDevicePropertiesDetailsComponent commonBleDevicePropertiesDetailsComponent;
    }
}