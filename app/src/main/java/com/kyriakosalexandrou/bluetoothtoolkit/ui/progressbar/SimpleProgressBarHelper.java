package com.kyriakosalexandrou.bluetoothtoolkit.ui.progressbar;

import android.content.Context;

/**
 * Created by Kyriakos on 14/12/2015.
 * <p/>
 * Preferring composition over inheritance.
 * Simply create an instance of this class to the class
 * that requires a progressBar and the logic would be handled from here.
 */
public class SimpleProgressBarHelper extends BaseProgressBarHelper {
    private static final String TAG = SimpleProgressBarHelper.class.getSimpleName();

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
    public SimpleProgressBarHelper(Context context, ProgressBarSize progressBarSize) {
        super(context, progressBarSize);
    }
}
