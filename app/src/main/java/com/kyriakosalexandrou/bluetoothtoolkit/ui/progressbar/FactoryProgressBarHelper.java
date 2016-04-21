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

package com.kyriakosalexandrou.bluetoothtoolkit.ui.progressbar;

import android.content.Context;

/**
 * Created by Kyriakos on 09/01/2016.
 * <p/>
 * Factory class to easily create a specific progress bar
 */
public class FactoryProgressBarHelper {

    public BaseProgressBarHelper getProgressBar(Context context, BaseProgressBarHelper.ProgressBarSize progressBarSize) {
        BaseProgressBarHelper progressBarHelper;

        switch (progressBarSize) {
            case SMALL:
                progressBarHelper = new SimpleProgressBarHelper(context, SimpleProgressBarHelper.ProgressBarSize.SMALL);
                break;

            case LARGE:
                progressBarHelper = new SimpleProgressBarHelper(context, SimpleProgressBarHelper.ProgressBarSize.LARGE);
                break;

            case FULL_SCREEN:
                progressBarHelper = new SimpleProgressBarHelper(context, SimpleProgressBarHelper.ProgressBarSize.FULL_SCREEN);
                break;

            default:
                progressBarHelper = new SimpleProgressBarHelper(context, SimpleProgressBarHelper.ProgressBarSize.SMALL);
        }
        return progressBarHelper;
    }
}