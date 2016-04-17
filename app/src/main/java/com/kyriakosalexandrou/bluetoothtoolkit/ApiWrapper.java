package com.kyriakosalexandrou.bluetoothtoolkit;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;

public class ApiWrapper {
    private final static String TAG = ApiWrapper.class.getSimpleName();

    public static int getColor(Context context, @ColorRes int id, @Nullable Resources.Theme theme) {
        int color;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            color = context.getResources().getColor(id, theme);
        } else {
            color = context.getResources().getColor(id);
        }
        return color;
    }
}