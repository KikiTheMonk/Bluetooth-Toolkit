package com.kyriakosalexandrou.bluetoothtoolkit.bt;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.adapters.ListFoundDevicesAdapter;
import com.kyriakosalexandrou.bluetoothtoolkit.ui.components.HeaderComponent;

/**
 * shows the found BT devices
 */
public class DialogFoundBtDevices extends Dialog {
    private Button mBtnCancel;
    private HeaderComponent mHeaderComponent;
    private ListView mLvFoundDevices;

    private ListFoundDevicesAdapter mListFoundDevicesAdapter;
    private DialogFoundDevicesCallbacks mDialogFoundDevicesCallbacks;

    public DialogFoundBtDevices(Context context, DialogFoundDevicesCallbacks dialogFoundDevicesCallbacks, String headerTitle, Drawable headerIcon) {
        super(context);
        mDialogFoundDevicesCallbacks = dialogFoundDevicesCallbacks;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_found_bt_devices);
        init(headerTitle, headerIcon);
    }

    public ListFoundDevicesAdapter getListFoundDevicesHandler() {
        return mListFoundDevicesAdapter;
    }

    private void init(String headerTitle, Drawable headerIcon) {
        bindViews();
        setAdapters();
        setListeners();
        configureDialog(headerTitle, headerIcon);
    }

    private void bindViews() {
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mHeaderComponent = (HeaderComponent) findViewById(R.id.header);
        mLvFoundDevices = (ListView) findViewById(R.id.list);
    }

    private void setAdapters() {
        mListFoundDevicesAdapter = new ListFoundDevicesAdapter(getContext());
        mLvFoundDevices.setAdapter(mListFoundDevicesAdapter);
    }

    private void setListeners() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        mLvFoundDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long id) {
                mDialogFoundDevicesCallbacks.onDialogFoundDevicesItemClick(arg0, view, position, id);
            }
        });

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                mDialogFoundDevicesCallbacks.onDialogFoundDevicesCancel(arg0);
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                mListFoundDevicesAdapter.clearList();
            }
        });
    }

    public void shouldShowProgressBar(BtAdapterHelper btAdapterHelper) {
        mHeaderComponent.shouldShowProgressBar(btAdapterHelper);
    }


    private void configureDialog(String headerTitle, Drawable headerIcon) {
        mHeaderComponent.setTitle("Select a " + headerTitle + " device");
        mHeaderComponent.setIcon(headerIcon);
        setCanceledOnTouchOutside(false);
    }

    public interface DialogFoundDevicesCallbacks {
        /**
         * Callback for what to do when an item is clicked on the found devices
         * dialog
         *
         * @param arg0     The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this will be
         *                 a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        void onDialogFoundDevicesItemClick(AdapterView<?> arg0, View view, int position, long id);

        /**
         * Called when the dialog of found bluetooth devices is cancelled
         *
         * @param dialogInterface
         */
        void onDialogFoundDevicesCancel(DialogInterface dialogInterface);
    }
}