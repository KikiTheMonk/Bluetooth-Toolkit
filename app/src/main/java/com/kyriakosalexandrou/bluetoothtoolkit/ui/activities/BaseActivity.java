package com.kyriakosalexandrou.bluetoothtoolkit.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.ui.components.HeaderComponent;

/**
 * Base class for all activities
 *
 * @author Kyriakos.Alexandrou
 */
public abstract class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";
    protected static final int ENABLE_BT_REQUEST_ID = 1;
    private static final int Enable_GPS_REQUEST = 2;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 3;

    protected Activity mActivity;

    private Dialog mDialogAbout;
    private View mViewAbout;

    protected SharedPreferences mSharedPreferences;
    protected boolean isPrefRunInBackground = true;

    protected Dialog getDialogAbout() {
        return mDialogAbout;
    }


    protected void onCreate(Bundle savedInstanceState, int layoutResID) {
        setContentView(layoutResID);
        super.onCreate(savedInstanceState);

        mActivity = this;

        bindViews();
        setAdapters();
        setListeners();

        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
    }

    protected void bindViews() {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewAbout = li.inflate(R.layout.dialog_about, null, false);
    }

    protected void setAdapters() {
    }

    protected void setListeners() {
    }

    protected void uiInvalidateBtnState() {
    }

    /**
     * About popup dialog initialisation
     *
     * @param text the content message to display in the dialog
     */
    protected void initialiseDialogAbout(String text) {
        mDialogAbout = new Dialog(this);

        mDialogAbout.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialogAbout.setContentView(mViewAbout);

        HeaderComponent header = (HeaderComponent) mDialogAbout.findViewById(R.id.header);
        header.setTitle(getResources().getString(R.string.about));
        mDialogAbout.setCanceledOnTouchOutside(true);

		/*
         * set content to display in the about message
		 */
        TextView valueAbout = (TextView) mDialogAbout
                .findViewById(R.id.valueAbout);
        valueAbout.setMovementMethod(LinkMovementMethod.getInstance());
        valueAbout.setText(Html.fromHtml(text));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPref();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_about:
                mDialogAbout.show();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(BaseActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean isEnabled = false;

        switch (requestCode) {
            case Enable_GPS_REQUEST:
                if (resultCode == Activity.RESULT_CANCELED) {
                    isEnabled = false;
                } else if (resultCode == Activity.RESULT_OK) {
                    isEnabled = true;
                }
                onEnableGpsRequestCallback(isEnabled);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onEnableGpsRequestCallback(boolean isEnabled) {
    }

    protected void uiInvalidateViewsState() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uiInvalidateBtnState();
            }
        });
    }

    protected void loadPref() {
        isPrefRunInBackground = mSharedPreferences.getBoolean(
                "pref_run_in_background", true);
    }


    public boolean isGpsEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void askToEnableGps() {
        if (!isGpsEnabled()) {
            showMessageOKCancel(getString(R.string.gps_needs_to_be_enabled), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent enableGpsRequestIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(enableGpsRequestIntent, Enable_GPS_REQUEST);
                }
            });
        }
    }

    protected boolean isPermissionRequestGranted(final String permission, String informativeMsg) {
        boolean isPermissionRequestGranted;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permitted = checkSelfPermission(permission);
            if (permitted == PackageManager.PERMISSION_DENIED) {
                showMessageOKCancel(informativeMsg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(
                                new String[]{permission},
                                REQUEST_CODE_ASK_PERMISSIONS);
                    }
                });
                isPermissionRequestGranted = false;
            } else {
                //permission is already granted
                isPermissionRequestGranted = true;
            }
        } else {
            //in pre Marshmallow there is no need to request for permission in runtime
            isPermissionRequestGranted = true;
        }

        return isPermissionRequestGranted;
    }

    protected void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BaseActivity.this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
//                    Toast.makeText(this, "ACCESS_COARSE_LOCATION GRANTED", Toast.LENGTH_SHORT)
//                            .show();
                } else {
                    // Permission Denied
//                    Toast.makeText(this, "ACCESS_COARSE_LOCATION Denied", Toast.LENGTH_SHORT)
//                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}