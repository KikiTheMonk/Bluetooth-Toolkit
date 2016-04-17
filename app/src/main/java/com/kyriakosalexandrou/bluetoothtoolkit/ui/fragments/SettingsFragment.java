package com.kyriakosalexandrou.bluetoothtoolkit.ui.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.kyriakosalexandrou.bluetoothtoolkit.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.layout.activity_base_preferences);
    }
}