package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.healththermometermeasurement;

public interface HtmDeviceManagerUICallback {

    void onUiHtmFound(boolean isFound);

    void onUiTemperatureChange(final float result);
}