package com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble;

public interface HtmDeviceManagerUICallback {

    void onUiHtmFound(boolean isFound);

    void onUiTemperatureChange(final float result);
}