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

package com.kyriakosalexandrou.bluetoothtoolkit.ui.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kyriakosalexandrou.bluetoothtoolkit.ApiWrapper;
import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.UtilHelper;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.BtAdapterHelper;

/**
 * a custom header component
 */
public class HeaderComponent extends LinearLayout {
    private View mContentContainer;
    private ImageView mHeaderIcon;
    private TextView mHeaderTitle;
    private ProgressBar mProgressIndicator;
    private View mDivider;

    public void setBackgroundResource(int color) {
        mContentContainer.setBackgroundResource(color);
    }

    public void setTitle(String headerTitle) {
        mHeaderTitle.setText(headerTitle);
    }

    public void setTextColor(int color) {
        mHeaderTitle.setTextColor(color);
    }

    public void setIcon(Drawable headerIcon) {
        mHeaderIcon.setImageDrawable(headerIcon);
    }

    public void setDividerColor(int color) {
        mDivider.setBackgroundColor(color);
    }

    public HeaderComponent(Context context) {
        this(context, null);
    }

    public HeaderComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderComponent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater inflater = UtilHelper.getLayoutInflater(getContext());
        View view = inflater.inflate(R.layout.component_header, this, true);
        bindViews(view);
        setDefaultAttributeValues(attrs);
    }

    public void shouldShowProgressBar(final BtAdapterHelper btAdapterHelper) {
        if (btAdapterHelper.getBtcSearchHelper().isScanning()
                || btAdapterHelper.getBleSearchBaseHelper().isScanning()) {
            forceShowProgressBar();
        } else {
            forceHideProgressBar();
        }
    }

    public void forceShowProgressBar() {
        mProgressIndicator.setVisibility(View.VISIBLE);
    }

    public void forceHideProgressBar() {
        mProgressIndicator.setVisibility(View.INVISIBLE);
    }

    private void bindViews(View view) {
        this.mContentContainer = view.findViewById(R.id.content_container);
        this.mHeaderTitle = (TextView) view.findViewById(R.id.title);
        this.mHeaderIcon = (ImageView) view.findViewById(R.id.icon);
        this.mDivider = view.findViewById(R.id.divider);
        this.mProgressIndicator = (ProgressBar) view.findViewById(R.id.progress_indicator);
    }

    private void setDefaultAttributeValues(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Header);
        int bgColor = a.getColor(
                R.styleable.Header_backgroundColor,
                ApiWrapper.getColor(getContext(), R.color.header_background, null)
        );
        setBackgroundColor(bgColor);

        int textColor = a.getColor(
                R.styleable.Header_textColor,
                ApiWrapper.getColor(getContext(), R.color.header_text, null));
        setTextColor(textColor);

        int dividerColor = a.getColor(
                R.styleable.Header_dividerColor,
                ApiWrapper.getColor(getContext(), R.color.header_divider, null));
        setDividerColor(dividerColor);

        a.recycle();
    }
}