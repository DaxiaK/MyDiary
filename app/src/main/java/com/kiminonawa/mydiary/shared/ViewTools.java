package com.kiminonawa.mydiary.shared;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.TypedValue;

/**
 * Created by daxia on 2016/10/28.
 */

public class ViewTools {

    public static int dpToPixel(final Resources r, final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static Drawable getDrawable(Context context, @DrawableRes int drawRes) {

        Drawable returnDrawable;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            returnDrawable = context.getResources().getDrawable(drawRes, null);
        } else {
            returnDrawable = context.getResources().getDrawable(drawRes);

        }
        return returnDrawable;
    }
}
