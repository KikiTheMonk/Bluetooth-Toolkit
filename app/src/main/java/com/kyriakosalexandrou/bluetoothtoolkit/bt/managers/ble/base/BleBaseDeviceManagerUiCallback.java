package com.kyriakosalexandrou.bluetoothtoolkit.bt.managers.ble.base;

public interface BleBaseDeviceManagerUiCallback {

    void onUiDeviceManagerInitialised(BleBaseDeviceManager bleBaseDeviceManager);

    void onUiConnected();

    void onUiConnecting();

    void onUiDisconnected(final int status);

    void onUiDisconnecting();

    void onUiBatteryRead(final int valueBattery);

    void onUiReadRemoteRssi(final int valueRSSI);
}