package com.kyriakosalexandrou.bluetoothtoolkit;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public class UtilHelper {
    private final static String TAG = UtilHelper.class.getSimpleName();

    public static void dismissSoftKeyBoard(Activity activity) {
        View view = activity.getWindow().getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * gets the root view from the current context
     *
     * @param context the current context
     * @return the root view
     */
    public static ViewGroup getRootView(Context context) {
        ViewGroup layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();
        return layout;
    }

    public static LayoutInflater getLayoutInflater(Context context) {
        LayoutInflater LayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (LayoutInflater == null) {
            throw new AssertionError("LayoutInflater not found.");
        }
        return LayoutInflater;
    }

    public static float getResFloatValue(Context context, int dimenFloatResId) {
        TypedValue typedValue = new TypedValue();
        context.getResources().getValue(dimenFloatResId, typedValue, true);
        return typedValue.getFloat();
    }

    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Converts an array of bytes to a HEX string
     *
     * @param bytes the data to be converted to HEX
     * @return hexChars the converted bytes in a HEX string format
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null)
            return null;

        String result;

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        result = new String(hexChars);

        Log.i(TAG, "bytesToHex: " + result);
        return result;
    }
}