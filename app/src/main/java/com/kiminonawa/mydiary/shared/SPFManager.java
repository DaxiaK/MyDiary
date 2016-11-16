package com.kiminonawa.mydiary.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.kiminonawa.mydiary.BuildConfig;


/**
 * Created by daxia on 2016/7/31.
 */
public class SPFManager {

    /**
     * config
     */
    private static final String SPF_CONFIG_NEME = "CONFIG";
    //Location
    private static final String CONFIG_OPEN_DIARY_LOCATION = "OPEN_DIARY_LOCATION";
    //Theme
    private static final String CONFIG_THEME = "CONFIG_THEME";

    /**
     * profile
     */
    private static final String SPF_PROFILE = "PROFILE";
    private static final String PROFILE_YOUR_NAME_IS = "YOUR_NAME_IS";
    private static final String PROFILE_LOGIN_TYPE = "LOGIN_TYPE";

    public static final int LOGIN_TYPE_NONE = 0;
    public static final int LOGIN_TYPE_LOCAL = 1;
    public static final int LOGIN_TYPE_GOOGLE = 2;


    /**
     * System
     */
    private static final String SPF_SYSTEM = "SYSTEM";
    private static final String FIRST_RUN = "FIRST_RUN";
    private static final String SYSTEM_VERSIONCODE = "VERSIONCODE";
    public static final int DEFAULT_VERSIONCODE = -1;

    /**
     * Config method
     */
    public static boolean getDiaryLocation(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        //default is close
        return settings.getBoolean(CONFIG_OPEN_DIARY_LOCATION, false);
    }

    public static void setDiaryLocation(Context context, boolean open) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putBoolean(CONFIG_OPEN_DIARY_LOCATION, open);
        PE.commit();
    }

    public static int getTheme(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        //default is close
        return settings.getInt(CONFIG_THEME, ThemeManager.TAKI);
    }

    public static void setTheme(Context context, int theme) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(CONFIG_THEME, theme);
        PE.commit();
    }

    /**
     * Profile method
     */

    public static String getYourName(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_PROFILE, 0);
        //default is space
        return settings.getString(PROFILE_YOUR_NAME_IS, "");
    }

    public static void setLoginType(Context context, int loginType) {
        SharedPreferences settings = context.getSharedPreferences(SPF_PROFILE, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(PROFILE_LOGIN_TYPE, loginType);
        PE.commit();
    }


    public static int getLoginType(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_PROFILE, 0);
        return settings.getInt(PROFILE_LOGIN_TYPE, LOGIN_TYPE_NONE);
    }

    public static void setYourName(Context context, String yourNameIs) {
        SharedPreferences settings = context.getSharedPreferences(SPF_PROFILE, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString(PROFILE_YOUR_NAME_IS, yourNameIs);
        PE.commit();
    }


    /**
     * System method
     */
    public static void setFirstRun(Context context, boolean firstRun) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putBoolean(FIRST_RUN, firstRun);
        PE.commit();
    }


    public static boolean getFirstRun(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        return settings.getBoolean(FIRST_RUN, true);
    }

    public static void setVersionCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putInt(SYSTEM_VERSIONCODE, BuildConfig.VERSION_CODE);
        PE.commit();
    }


    public static int getVersionCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        return settings.getInt(SYSTEM_VERSIONCODE, DEFAULT_VERSIONCODE);
    }


}
