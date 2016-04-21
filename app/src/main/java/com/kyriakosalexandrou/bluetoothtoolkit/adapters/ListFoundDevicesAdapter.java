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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;

import java.util.ArrayList;

/**
 * Responsible for handling the BT classic and LE devices
 *
 * @author Kyriakos.Alexandrou
 */
public class ListFoundDevicesAdapter extends BaseAdapter {
    private String TAG = "ListFoundDevicesHandler";
    private ArrayList<BluetoothDevice> mDevices;
    private ArrayList<Integer> mRSSIs;
    private Context mContext;

    public ListFoundDevicesAdapter(Context context) {
        super();
        mDevices = new ArrayList<BluetoothDevice>();
        mRSSIs = new ArrayList<Integer>();
        mContext = context;
    }

    public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.i(TAG, "Device added in found devices list: " + device.getAddress());

        if (!mDevices.contains(device)) {
            mDevices.add(device);
            mRSSIs.add(rssi);
        } else {
            mRSSIs.set(mDevices.indexOf(device), rssi);
        }
        notifyDataSetChanged();
    }

    public void addDevice(BluetoothDevice device, int rssi) {
        Log.i(TAG, "Device added in found devices list: " + device.getAddress());

        if (!mDevices.contains(device)) {
            mDevices.add(device);
            mRSSIs.add(rssi);
        } else {
            mRSSIs.set(mDevices.indexOf(device), rssi);
        }
        notifyDataSetChanged();
    }

    public BluetoothDevice getDevice(int index) {
        return mDevices.get(index);
    }

    public int getRssi(int index) {
        return mRSSIs.get(index);
    }

    public void clearList() {
        mDevices.clear();
        mRSSIs.clear();
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get already available view or create new if necessary
        FieldReferences fields;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_found_device, null);

            fields = new FieldReferences();
            fields.valueDeviceName = (TextView) convertView
                    .findViewById(R.id.device_name_item_value);
            fields.valueDeviceAddress = (TextView) convertView
                    .findViewById(R.id.device_address_item_value);
            fields.valueDeviceRssi = (TextView) convertView
                    .findViewById(R.id.device_rssi_Item_value);
            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }

        // set proper values into the view
        BluetoothDevice device = mDevices.get(position);
        int rssi = mRSSIs.get(position);
        String rssiString = (rssi == 0) ? "N/A" : rssi + " db";
        String name = device.getName();
        String address = device.getAddress();
        if (name == null || name.length() <= 0)
            name = "Unknown Device";

        fields.valueDeviceName.setText(name);
        fields.valueDeviceAddress.setText(address);
        fields.valueDeviceRssi.setText(rssiString);

        return convertView;
    }

    private class FieldReferences {
        TextView valueDeviceName;
        TextView valueDeviceAddress;
        TextView valueDeviceRssi;
    }
}