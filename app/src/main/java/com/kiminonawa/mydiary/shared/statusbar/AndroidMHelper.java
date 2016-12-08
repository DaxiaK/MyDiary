package com.kiminonawa.mydiary.shared.statusbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * * Ref:https://github.com/zouzhenglu/zouzhenglu.github.io
 * Created by daxia on 2016/12/7.
 */
public class AndroidMHelper implements IStatusBarFontHelper {
    /**
     * @return if version is lager than M
     */
    @Override
    @TargetApi(23)
    public boolean setStatusBarLightMode(Activity activity, boolean isFontColorDark) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, android.R.color.transparent));
        if (isFontColorDark) {
            //Status bar is Translucent
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //Status bar not Translucent
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        return true;
    }
}