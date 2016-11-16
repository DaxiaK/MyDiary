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

    /**
     * System
     */
    private static final String SPF_SYSTEM = "SYSTEM";
    private static final String FIRST_RUN = "FIRST_RUN";
    private static final String SYSTEM_VERSIONCODE = "VERSIONCODE";
    public static final int DEFAULT_VERSIONCODE = -1;
    private static final String DESCRIPTION_CLOSE = "DESCRIPTION_CLOSE";


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

    public static boolean getDescriptionClose(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        return settings.getBoolean(DESCRIPTION_CLOSE, false);
    }

    public static void setDescriptionClose(Context context, boolean close) {
        SharedPreferences settings = context.getSharedPreferences(SPF_SYSTEM, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putBoolean(DESCRIPTION_CLOSE, close);
        PE.commit();
    }


}
