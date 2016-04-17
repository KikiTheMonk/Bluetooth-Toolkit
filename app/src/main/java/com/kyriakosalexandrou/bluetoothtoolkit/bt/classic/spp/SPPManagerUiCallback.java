package com.kyriakosalexandrou.bluetoothtoolkit.bt.classic.spp;

public interface SPPManagerUiCallback {
    /**
     * called when data is received from the remote BT classic device
     */
    void onUiRemoteDeviceRead(String result);
}
