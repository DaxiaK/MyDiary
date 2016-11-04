package com.kiminonawa.mydiary.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.kiminonawa.mydiary.BuildConfig;


/**
 * Created by daxia on 2016/7/31.
 */
public class SPFManager {

    //Config
    private static final String SPF_CONFIG_NEME = "CONFIG";
    //Location
    private static final String CONFIG_OPEN_DIARY_LOACTION = "OPEN_DIARY_LOACTION";

    //System
    private static final String SPF_SYSTEM = "SYSTEM";
    private static final String FIRST_RUN = "FIRST_RUN";
    private static final String SYSTEM_VERSIONCODE = "VERSIONCODE";
    public static final int DEFAULT_VERSIONCODE = -1;

    /**
     * For config
     */

    public static boolean getDiaryLocation(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        //default is close
        return settings.getBoolean(CONFIG_OPEN_DIARY_LOACTION, false);
    }

    public static void setDiaryLocation(Context context, boolean open) {
        SharedPreferences settings = context.getSharedPreferences(SPF_CONFIG_NEME, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putBoolean(CONFIG_OPEN_DIARY_LOACTION, open);
        PE.commit();
    }


    /**
     * For System
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
