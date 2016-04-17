package com.kyriakosalexandrou.bluetoothtoolkit.bt.classic;

public interface BtcBaseDeviceManagerUiCallback {
    /**
     * called when the device is initialised
     */
    void onUiDeviceManagerInitialised(BtcBaseDeviceManager btcBaseDeviceManager);

    /**
     * called when the BT classic device gets connected
     */
    void onUiBtcRemoteDeviceConnected();

    /**
     * called when the BT classic device gets disconnected
     */
    void onUiBtcRemoteDeviceDisconnected();

    /**
     * Callback indicating that connection to the remote BT Classic device has
     * failed
     */
    void onUiBtcConnectFailed();
}
