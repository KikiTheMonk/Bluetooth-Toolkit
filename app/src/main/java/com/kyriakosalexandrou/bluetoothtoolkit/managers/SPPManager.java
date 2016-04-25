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

package com.kyriakosalexandrou.bluetoothtoolkit.managers;

import android.content.Context;
import android.util.Log;

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
