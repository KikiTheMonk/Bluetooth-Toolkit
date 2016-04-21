package com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble;

public interface HrmDeviceManagerUiCallback {

    void onUiHrmFound(boolean isFound);

    /**
     * gets called whenever the heart rate measurement changes from the remote
     * BLE device
     *
     * @param hrmValue the new heart rate measurement value
     */
    void onUiHRM(final int hrmValue);

    /**
     * called when the body sensor location characteristic is read with its
     * current body sensor location value
     *
     * @param bodySensorLocationValue the current body sensor location value
     */
    void onUiBodySensorLocation(final int bodySensorLocationValue);
}
