package com.kiminonawa.mydiary.shared;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ScrollView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

    public static void setScrollBarColor(Context context, ScrollView scrollView) {
        try {
            Field mScrollCacheField = View.class.getDeclaredField("mScrollCache");
            mScrollCacheField.setAccessible(true);
            Object mScrollCache = mScrollCacheField.get(scrollView);
            Field scrollBarField = mScrollCache.getClass().getDeclaredField("scrollBar");
            scrollBarField.setAccessible(true);
            Object scrollBar = scrollBarField.get(mScrollCache);
            Method method = scrollBar.getClass().getDeclaredMethod("setVerticalThumbDrawable", Drawable.class);
            method.setAccessible(true);
            method.invoke(scrollBar, new ColorDrawable(ThemeManager.getInstance().getThemeDarkColor(context)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
