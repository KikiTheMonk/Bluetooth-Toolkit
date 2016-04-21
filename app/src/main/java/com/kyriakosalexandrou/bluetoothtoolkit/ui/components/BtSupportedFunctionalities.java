package com.kyriakosalexandrou.bluetoothtoolkit.ui.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.UtilHelper;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.BtCheckerHelper;


public class BtSupportedFunctionalities extends LinearLayout {

    private BtCheckerHelper mBtCheckerHelper;
    private TextView mIsBtClassicSupportedLabel;
    private TextView mIsBleCentralSupportedLabel;
    private TextView mIsBlePeripheralSupportedLabel;

    public BtSupportedFunctionalities(Context context) {
        this(context, null);
    }

    public BtSupportedFunctionalities(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BtSupportedFunctionalities(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BtSupportedFunctionalities(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = UtilHelper.getLayoutInflater(getContext());
        View view = inflater.inflate(R.layout.component_bt_supported_functionalities_banner, this, true);
        bindViews(view);
    }

    private void bindViews(View view) {
        mIsBtClassicSupportedLabel = (TextView) view.findViewById(R.id.is_bt_classic_supported_label);
        mIsBleCentralSupportedLabel = (TextView) view.findViewById(R.id.is_central_supported_label);
        mIsBlePeripheralSupportedLabel = (TextView) view.findViewById(R.id.is_peripheral_supported_label);
    }

    public void checkBtSupportedFunctionalities(BtCheckerHelper btCheckerHelper) {
        mBtCheckerHelper = btCheckerHelper;

        checkBtClassicSupport();
        checkBleCentralSupport();
        checkBlePeripheralSupport();
    }

    private void checkBtClassicSupport() {
        if (mBtCheckerHelper.checkBtClassicSupport()) {
            mIsBtClassicSupportedLabel.setText(getResources().getString(R.string.label_bt_classic_mode_supported));
            mIsBtClassicSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            mIsBtClassicSupportedLabel.setText(getResources().getString(R.string.label_bt_classic_mode_not_supported));
            mIsBtClassicSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void checkBleCentralSupport() {
        if (mBtCheckerHelper.checkBleCentralSupport()) {
            mIsBleCentralSupportedLabel.setText(getResources().getString(R.string.label_central_mode_supported));
            mIsBleCentralSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            mIsBleCentralSupportedLabel.setText(getResources().getString(R.string.label_central_mode_not_supported));
            mIsBleCentralSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void checkBlePeripheralSupport() {
        if (mBtCheckerHelper.checkBlePeripheralSupport()) {
            mIsBlePeripheralSupportedLabel.setText(getResources().getString(R.string.label_peripheral_mode_supported));
            mIsBlePeripheralSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            mIsBlePeripheralSupportedLabel.setText(getResources().getString(R.string.label_peripheral_mode_not_supported));
            mIsBlePeripheralSupportedLabel.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
}
