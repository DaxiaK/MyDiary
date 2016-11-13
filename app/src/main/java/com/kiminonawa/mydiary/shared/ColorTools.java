package com.kiminonawa.mydiary.shared;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.ColorRes;

/**
 * Created by daxia on 2016/10/24.
 */

public class ColorTools {

    public static int getColor(Context context, @ColorRes int color) {

        int returnColor;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            returnColor = context.getResources().getColor(color, null);
        } else {
            returnColor = context.getResources().getColor(color);

        }
        return returnColor;
    }

    public static ColorStateList getColorStateList(Context context, @ColorRes int resId) {

        ColorStateList colorStateList;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colorStateList = context.getResources().getColorStateList(resId, null);
        } else {
            colorStateList = context.getResources().getColorStateList(resId);

        }
        return colorStateList;
    }
}
