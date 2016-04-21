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