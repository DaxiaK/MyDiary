package com.kiminonawa.mydiary.shared;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;

/**
 * Created by daxia on 2016/10/28.
 */

public class ViewTools {

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
