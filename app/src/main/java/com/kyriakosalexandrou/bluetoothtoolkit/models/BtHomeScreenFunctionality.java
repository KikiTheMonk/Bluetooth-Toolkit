package com.kyriakosalexandrou.bluetoothtoolkit.models;

/**
 * Created by Kyriakos on 07/02/2016.
 * <p/>
 * home screen functionalities model
 */
public class BtHomeScreenFunctionality {
    public enum BtFunctionalityType {
        BPM, HRM, HTM, SPP
    }

    public enum BtType {
        BT_CLASSIC, BLE_CENTRAL, BLE_PERIPHERAL
    }

    private final BtFunctionalityType btFunctionalityType;
    private final BtType btType;
    private final String name;
    private final String fullName;
    private final int[] images;
    public boolean supported = true;

    public BtHomeScreenFunctionality(BtFunctionalityType btFunctionalityType, BtType btType, String name, String fullName, int[] images) {
        this.btFunctionalityType = btFunctionalityType;
        this.btType = btType;
        this.name = name;
        this.fullName = fullName;
        this.images = images;
    }

    public BtFunctionalityType getBtFunctionalityType() {
        return btFunctionalityType;
    }

    public BtType getBtType() {
        return btType;
    }

    public String getBtTypeInPrettyFormat() {
        switch (btType) {
            case BT_CLASSIC:
                return "BT Classic";
            case BLE_CENTRAL:
                return "BLE Central";
            case BLE_PERIPHERAL:
                return "BLE Peripheral";
            default:
                return "Unknown";
        }
    }

    public String getShortName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public int[] getImages() {
        return images;
    }

    public boolean isSupported() {
        return supported;
    }

    public void setIsSupported(boolean supported) {
        this.supported = supported;
    }
}