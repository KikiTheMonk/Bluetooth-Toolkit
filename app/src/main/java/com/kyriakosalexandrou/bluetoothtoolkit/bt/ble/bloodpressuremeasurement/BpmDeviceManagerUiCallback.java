package com.kyriakosalexandrou.bluetoothtoolkit.bt.ble.bloodpressuremeasurement;

public interface BpmDeviceManagerUiCallback {
    void onUiBpmFound(boolean isFound);

    void onUIBloodPressureRead(final float valueSystolic,
                                      final float valueDiastolic,
                                      final float valueArterialPressure
    );

}