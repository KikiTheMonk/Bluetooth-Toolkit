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
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kyriakosalexandrou.bluetoothtoolkit.R;
import com.kyriakosalexandrou.bluetoothtoolkit.UtilHelper;

/**
 * Created by Kyriakos on 18/12/2015.
 * <p/>
 * Abstract base class for common progress bar logic
 */
public abstract class BaseProgressBarHelper {
    private static final String TAG = BaseProgressBarHelper.class.getSimpleName();
    private ProgressBar mProgressBar;
    private View mProgressBarContainer;

    /**
     * prepares the progressBar object
     *
     * @param context         the context
     * @param progressBarSize Can be one of {@link ProgressBarSize#SMALL}
     *                        {@link ProgressBarSize#LARGE}
     *                        {@link ProgressBarSize#FULL_SCREEN}
     *                        <p/>
     *                        if null is passed then the {@link ProgressBarSize#SMALL} is used
     */
    public BaseProgressBarHelper(Context context, @Nullable ProgressBarSize progressBarSize) {
        if (context != null) {
            mProgressBarContainer = createProgressBarContainer(context, progressBarSize);
            mProgressBar = createProgressBar(mProgressBarContainer);
        } else {
            Log.v(TAG, "The context parameter was null");
        }
    }

    /**
     * this can be used to make changes to the actual progress bar during runtime such as change state, drawable etc.
     *
     * @return the progress bar
     */
    public final ProgressBar getProgressBar() {
        return mProgressBar;
    }

    private View createProgressBarContainer(Context context, @Nullable ProgressBarSize progressBarSize) {
        LayoutInflater layoutInflater = UtilHelper.getLayoutInflater(context);
        View progressBarContainer = inflateProgressBarView(layoutInflater, progressBarSize);

        ViewGroup layout = UtilHelper.getRootView(context);
        layout.addView(progressBarContainer);

        return progressBarContainer;
    }

    private View inflateProgressBarView(LayoutInflater layoutInflater, @Nullable ProgressBarSize progressBarSize) {
        View progressBarContainer;

        switch (progressBarSize) {
            case SMALL:
                progressBarContainer = layoutInflater.inflate(R.layout.progress_bar_small, null);
                break;

            case LARGE:
                progressBarContainer = layoutInflater.inflate(R.layout.progress_bar_large, null);
                break;

            case FULL_SCREEN:
                progressBarContainer = layoutInflater.inflate(R.layout.progress_bar_full_screen, null);
                break;

            default:
                progressBarContainer = layoutInflater.inflate(R.layout.progress_bar_small, null);
        }
        return progressBarContainer;
    }

    private ProgressBar createProgressBar(View progressBarContainer) {
        ProgressBar progressBar = (ProgressBar) progressBarContainer.findViewById(R.id.progress);
        return progressBar;
    }

    /**
     * shows the progressBar
     */
    public void showProgressBar() {
        if (mProgressBarContainer != null) {
            mProgressBarContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * hides the progressBar
     */
    public void hideProgressBar() {
        if (mProgressBarContainer != null) {
            mProgressBarContainer.setVisibility(View.GONE);
        }
    }

    /**
     * different progress bar sizes
     * <p/>
     * can be one of
     * {@link ProgressBarSize#SMALL}
     * {@link ProgressBarSize#LARGE}
     * {@link ProgressBarSize#FULL_SCREEN}
     */
    public enum ProgressBarSize {
        /**
         * a small sized progress bar
         */
        SMALL,
        /**
         * a large sized progress bar
         */
        LARGE,
        /**
         * the progress bar takes the full width and height of the screen
         */
        FULL_SCREEN
    }
}