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

package com.kyriakosalexandrou.bluetoothtoolkit.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.adapters.HomeIconsAdapter;
import com.kyriakosalexandrou.bluetoothtoolkit.ui.components.SupportedBtTechnologiesComponent;
import com.kyriakosalexandrou.bluetoothtoolkit.models.BtHomeScreenFunctionality;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BtBaseActivity {
    private GridView mIconsGrid;
    private List<BtHomeScreenFunctionality> mBtFunctionalities = new ArrayList<>();
    private SupportedBtTechnologiesComponent mSupportedBtTechnologiesComponent;
    private HomeIconsAdapter mHomeIconsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_home);
        initialiseDialogAbout(getResources()
                .getString(R.string.disclaimer_text));
    }

    @Override
    protected void onEnableBtRequestCallback(boolean isEnabled) {
        if (isEnabled) {
            mSupportedBtTechnologiesComponent.checkBtSupportedFunctionalities(getBtAdapterHelper().getBtChecker());

            disableUnsupportedBtFunctionalities(
                    getBtAdapterHelper().getBtChecker().checkBtClassicSupport(),
                    getBtAdapterHelper().getBtChecker().checkBleCentralSupport(),
                    getBtAdapterHelper().getBtChecker().checkBlePeripheralSupport());
        }
    }

    @Override
    protected void onEnableGpsRequestCallback(boolean isEnabled) {
    }

    private void disableUnsupportedBtFunctionalities(
            boolean isBtClassicSupported, boolean isBleCentralSupported,
            boolean isBlePeripheralSupported) {

        for (BtHomeScreenFunctionality btHomeScreenFunctionality : mHomeIconsAdapter.getItems()) {
            switch (btHomeScreenFunctionality.getBtType()) {
                case BT_CLASSIC:
                    if (!isBtClassicSupported) {
                        btHomeScreenFunctionality.setIsSupported(false);
                    }
                    break;
                case BLE_CENTRAL:
                    if (!isBleCentralSupported) {
                        btHomeScreenFunctionality.setIsSupported(false);
                    }
                    break;
                case BLE_PERIPHERAL:
                    if (!isBlePeripheralSupported) {
                        btHomeScreenFunctionality.setIsSupported(false);
                    }
                    break;
            }
        }

        mHomeIconsAdapter.notifyDataSetChanged();
    }

    @Override
    public void bindViews() {
        super.bindViews();
        mSupportedBtTechnologiesComponent = (SupportedBtTechnologiesComponent) findViewById(R.id.bt_supported_functionalities);
        mIconsGrid = (GridView) findViewById(R.id.grid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSupportedBtTechnologiesComponent.checkBtSupportedFunctionalities(getBtAdapterHelper().getBtChecker());
    }

    @Override
    public void setAdapters() {
        super.setAdapters();
        mBtFunctionalities.add(createBpmFunctionalityItem());
        mBtFunctionalities.add(createHrmFunctionalityItem());
        mBtFunctionalities.add(createHtmFunctionalityItem());
        mBtFunctionalities.add(createSppFunctionalityItem());
        mHomeIconsAdapter = new HomeIconsAdapter(this, mBtFunctionalities);

        mIconsGrid.setAdapter(mHomeIconsAdapter);

        mIconsGrid.setClickable(false);
        mIconsGrid.setLongClickable(false);
    }

    private BtHomeScreenFunctionality createBpmFunctionalityItem() {
        int[] bpmImages = new int[2];
        bpmImages[0] = R.drawable.ic_home_screen_bpm;
        bpmImages[1] = R.drawable.ic_home_screen_multiple_bpm;
        return new BtHomeScreenFunctionality(
                BtHomeScreenFunctionality.BtFunctionalityType.BPM,
                BtHomeScreenFunctionality.BtType.BLE_CENTRAL,
                getResources().getString(R.string.bpm_abbreviation),
                getResources().getString(R.string.title_activity_bpm),
                bpmImages);
    }

    private BtHomeScreenFunctionality createHrmFunctionalityItem() {
        int[] hrImages = new int[2];
        hrImages[0] = R.drawable.ic_home_screen_hrm;
        hrImages[1] = R.drawable.ic_home_screen_multiple_hrm;
        return new BtHomeScreenFunctionality(
                BtHomeScreenFunctionality.BtFunctionalityType.HRM,
                BtHomeScreenFunctionality.BtType.BLE_CENTRAL,
                getResources().getString(R.string.label_hrm),
                getResources().getString(R.string.title_activity_hrm),
                hrImages);
    }

    private BtHomeScreenFunctionality createHtmFunctionalityItem() {
        int[] thermometerImages = new int[2];
        thermometerImages[0] = R.drawable.ic_home_screen_htm;
        thermometerImages[1] = R.drawable.ic_home_screen_multiple_htm;
        return new BtHomeScreenFunctionality(
                BtHomeScreenFunctionality.BtFunctionalityType.HTM,
                BtHomeScreenFunctionality.BtType.BLE_CENTRAL,
                getResources().getString(R.string.label_htm),
                getResources().getString(R.string.title_activity_htm),
                thermometerImages);
    }

    private BtHomeScreenFunctionality createSppFunctionalityItem() {
        int[] sppImages = new int[1];
        sppImages[0] = R.drawable.ic_home_screen_spp;
        return new BtHomeScreenFunctionality(
                BtHomeScreenFunctionality.BtFunctionalityType.SPP,
                BtHomeScreenFunctionality.BtType.BT_CLASSIC,
                getResources().getString(R.string.label_spp),
                getResources().getString(R.string.title_activity_spp),
                sppImages);
    }

    @Override
    public void setListeners() {
        super.setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
