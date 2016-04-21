package com.kyriakosalexandrou.bluetoothtoolkit.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.UtilHelper;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.base.BleBaseDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.ui.components.ComponentCommonBleDevicePropertiesDetails;

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
        fields.componentCommonBleDevicePropertiesDetails =
                (ComponentCommonBleDevicePropertiesDetails)
                        convertView.findViewById(R.id.common_ble_properties_component);
    }

    protected void setGenericViews(final CommonFieldReferences fields, int position) {
        fields.componentCommonBleDevicePropertiesDetails.
                setBleDeviceManager(mDevices.get(position));

        fields.componentCommonBleDevicePropertiesDetails.updateCommonViews();
    }

    protected class CommonFieldReferences {
        ComponentCommonBleDevicePropertiesDetails componentCommonBleDevicePropertiesDetails;
    }
}