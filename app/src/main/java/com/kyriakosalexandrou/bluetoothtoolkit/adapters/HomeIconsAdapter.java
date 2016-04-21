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
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.UtilHelper;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.bloodpressuremeasurement.BpmDeviceActivity;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.bloodpressuremeasurement.BpmDeviceMultipleDevicesActivity;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.healththermometermeasurement.HtmDeviceActivity;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.healththermometermeasurement.HtmDeviceMultipleDevicesActivity;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.heartratemeasurement.HrmDeviceActivity;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.heartratemeasurement.HrmDeviceMultipleDevicesActivity;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.classic.spp.SppActivity;
import com.kyriakosalexandrou.bluetoothtoolkit.models.BtHomeScreenFunctionality;

import java.util.List;

/**
 * Responsible for handling the icons in the home screen
 *
 * @author Kyriakos.Alexandrou
 */
public class HomeIconsAdapter extends BaseAdapter {
    private Context mContext;
    private final List<BtHomeScreenFunctionality> mBtHomeScreenFunctionalities;

    public HomeIconsAdapter(Context context, List<BtHomeScreenFunctionality> btFunctionalities) {
        mContext = context;
        this.mBtHomeScreenFunctionalities = btFunctionalities;
    }

    @Override
    public int getCount() {
        return mBtHomeScreenFunctionalities.size();
    }

    @Override
    public BtHomeScreenFunctionality getItem(int position) {
        return mBtHomeScreenFunctionalities.get(position);
    }

    public List<BtHomeScreenFunctionality> getItems() {
        return mBtHomeScreenFunctionalities;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = UtilHelper.getLayoutInflater(mContext);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_home, null);
            viewHolder = new ViewHolder();
            bindViews(convertView, viewHolder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setViewsValues(viewHolder, mBtHomeScreenFunctionalities.get(position));

        return convertView;
    }

    private void bindViews(View view, ViewHolder viewHolder) {
        viewHolder.mShortName = (TextView) view.findViewById(R.id.short_name_label);
        viewHolder.mFullName = (TextView) view.findViewById(R.id.full_name_label);
        viewHolder.mBtType = (TextView) view.findViewById(R.id.bt_type);
        viewHolder.mLeftIcon = (ImageView) view.findViewById(R.id.left_icon);
        viewHolder.mRightIcon = (ImageView) view.findViewById(R.id.right_icon);
    }

    private void setViewsValues(final ViewHolder viewHolder, BtHomeScreenFunctionality btHomeScreenFunctionality) {
        viewHolder.mShortName.setText(btHomeScreenFunctionality.getShortName());
        viewHolder.mFullName.setText(btHomeScreenFunctionality.getFullName());
        viewHolder.mBtType.setText(mContext.getString(R.string.bt_type, btHomeScreenFunctionality.getBtTypeInPrettyFormat()));
        setIcons(viewHolder, btHomeScreenFunctionality);
    }

    private void setIcons(final ViewHolder viewHolder, final BtHomeScreenFunctionality btHomeScreenFunctionality) {
        viewHolder.mLeftIcon.setImageResource(btHomeScreenFunctionality.getImages()[0]);

        viewHolder.mLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftIconClick(btHomeScreenFunctionality);
            }
        });

        if (btHomeScreenFunctionality.getImages().length >= 2) {
            viewHolder.mRightIcon.setVisibility(View.VISIBLE);
            viewHolder.mRightIcon.setImageResource(btHomeScreenFunctionality.getImages()[1]);

            viewHolder.mRightIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightIconClick(btHomeScreenFunctionality);
                }
            });
        } else {
            viewHolder.mRightIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void onRightIconClick(final BtHomeScreenFunctionality btHomeScreenFunctionality) {
        Intent intent;

        if (btHomeScreenFunctionality.isSupported()) {

            switch (btHomeScreenFunctionality.getBtFunctionalityType()) {
                case BPM:
                    intent = new Intent(mContext, BpmDeviceMultipleDevicesActivity.class);
                    mContext.startActivity(intent);
                    break;

                case HRM:
                    intent = new Intent(mContext, HrmDeviceMultipleDevicesActivity.class);
                    mContext.startActivity(intent);
                    break;
                case HTM:
                    intent = new Intent(mContext, HtmDeviceMultipleDevicesActivity.class);
                    mContext.startActivity(intent);
                    break;
                case SPP:
                    intent = new Intent(mContext, SppActivity.class);
                    mContext.startActivity(intent);
                    break;
            }
        } else {
            Toast.makeText(mContext, R.string.not_supported, Toast.LENGTH_LONG).show();
        }
    }

    private void onLeftIconClick(final BtHomeScreenFunctionality btHomeScreenFunctionality) {
        Intent intent;

        if (btHomeScreenFunctionality.isSupported()) {
            switch (btHomeScreenFunctionality.getBtFunctionalityType()) {
                case BPM:
                    intent = new Intent(mContext, BpmDeviceActivity.class);
                    mContext.startActivity(intent);
                    break;
                case HRM:
                    intent = new Intent(mContext, HrmDeviceActivity.class);
                    mContext.startActivity(intent);
                    break;
                case HTM:
                    intent = new Intent(mContext, HtmDeviceActivity.class);
                    mContext.startActivity(intent);
                    break;
                case SPP:
                    intent = new Intent(mContext, SppActivity.class);
                    mContext.startActivity(intent);
                    break;
            }
        } else {
            Toast.makeText(mContext, R.string.not_supported, Toast.LENGTH_LONG).show();
        }
    }

    private class ViewHolder {
        private TextView mShortName;
        private ImageView mLeftIcon;
        private ImageView mRightIcon;
        private TextView mFullName;
        private TextView mBtType;
    }
}