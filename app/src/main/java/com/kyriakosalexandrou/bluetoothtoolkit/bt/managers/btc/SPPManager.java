package com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.btc;

import android.content.Context;
import android.util.Log;

import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.btc.base.BtcBaseDeviceManager;
import com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.btc.base.BtcBaseDeviceManagerUiCallback;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.UUID;

public class SPPManager extends BtcBaseDeviceManager {
    private static final String TAG = "SPPManager";
    private static final int TOTAL_BYTES_TO_READ_FROM_REMOTE_DEVICE = 100;
    public static final UUID UUID_SPP = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private SPPManagerUiCallback mSPPManagerUiCallback;

    public SPPManager(Context context,
                      BtcBaseDeviceManagerUiCallback btcBaseDeviceManagerUiCallback) {
        super(context, btcBaseDeviceManagerUiCallback, UUID_SPP);
        mSPPManagerUiCallback = (SPPManagerUiCallback) btcBaseDeviceManagerUiCallback;
    }

    @Override
    public void onBtcConnected() {
        super.onBtcConnected();
        startDataListeningFromRemoteDevice(TOTAL_BYTES_TO_READ_FROM_REMOTE_DEVICE);
    }

    @Override
    public void onBtcDisconnected() {
        super.onBtcDisconnected();
    }

    @Override
    public void onBtcConnectFailed() {
        super.onBtcConnectFailed();
    }

    @Override
    public void onBtcDataRead(byte[] buffer) {
        /*
         * The buffer might have extra data, if the data that was sent from the
		 * remote device is less than the size of our buffer then the buffer
		 * will have extra unneeded data.
		 * 
		 * As we only want the data that was actually sent from the remote
		 * device, we discard the extra data.
		 */
        String result = new String(buffer);

        Log.i(TAG,
                "Received data from remote device: "
                        + StringEscapeUtils.escapeJava(result));

        mSPPManagerUiCallback.onUiRemoteDeviceRead(result);
    }
}
