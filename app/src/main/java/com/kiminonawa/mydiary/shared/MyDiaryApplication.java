package com.kiminonawa.mydiary.shared;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

/**
 * Created by daxia on 2017/1/10.
 */

public class MyDiaryApplication extends Application {

    boolean hasPassword = false;

    @Override
    public void onCreate() {
        super.onCreate();
        //To fix bug : spinner bg is dark when mode is night.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Log.e("test","SPFManager.getPassword(this)=" +SPFManager.getPassword(this));

        if (SPFManager.getPassword(this).equals("")) {
            hasPassword = false;
        } else {
            hasPassword = true;
        }
    }


    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }
}
