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