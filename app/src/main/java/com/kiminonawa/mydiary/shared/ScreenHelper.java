package com.kiminonawa.mydiary.shared;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.text.DecimalFormat;

/**
 * Created by daxia on 2016/9/23.
 */

public class ScreenHelper {


    public static float getScreenRatio(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        DecimalFormat mDecimalFormat = new DecimalFormat("#.##");
        float ScreenRatio = Float.valueOf(mDecimalFormat.format(metrics.heightPixels / metrics.widthPixels));
        return ScreenRatio;
    }

    public static int dpToPixel(final Resources r, final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static void hideSystemUI(View decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            openInmmersiveMode(decorView);
        } else {
            //Do nothing
        }
    }

    public static void showSystemUI(View decorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            closeInmmersiveMode(decorView);
        } else {
            //Do nothing
        }
    }

    /**
     * Open Immersive Full-Screen Mode
     * Only work on API 19+
     */
    @TargetApi(19)
    private static void openInmmersiveMode(View view) {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    /**
     * Close Immersive Full-Screen Mode
     * Only work on API 19+
     */
    @TargetApi(19)
    public static void closeInmmersiveMode(View view) {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}
