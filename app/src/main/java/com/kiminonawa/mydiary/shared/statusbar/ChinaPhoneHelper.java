package com.kiminonawa.mydiary.shared.statusbar;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by daxia on 2016/12/7.
 * Ref:https://github.com/zouzhenglu/zouzhenglu.github.io
 * This class does not test in MIUI & FLYME.
 */

public class ChinaPhoneHelper {

    @IntDef({
            OTHER,
            MIUI,
            FLYME,
            ANDROID_M
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SystemType {

    }

    private static int deviceStatusBarType = 0;

    public static final int OTHER = -1;
    public static final int MIUI = 1;
    public static final int FLYME = 2;
    public static final int ANDROID_M = 3;

    /**
     * Make status icon is dark，
     * Run on Android 6.0+ , for MIUI,FLYME,ANDROID_M
     *
     * @return 1:MIUI 2:Flyme 3:android6.0
     */
    public static void setStatusBar(Activity activity, boolean lightMode) {
        @SystemType int result = OTHER;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (new MIUIHelper().setStatusBarLightMode(activity, lightMode)) {
                result = MIUI;
            } else if (new FlymeHelper().setStatusBarLightMode(activity, lightMode)) {
                result = FLYME;
            } else if (new AndroidMHelper().setStatusBarLightMode(activity, lightMode)) {
                result = ANDROID_M;
            }
        }
        if (deviceStatusBarType == 0) {
            deviceStatusBarType = result;
        }
    }

    public static int getDeviceStatusBarType() {
        return deviceStatusBarType;
    }
}

