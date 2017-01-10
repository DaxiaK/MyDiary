package com.kiminonawa.mydiary.shared;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by daxia on 2017/1/10.
 */

public class MyDiaryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //To fix bug : spinner bg is dark when mode is night.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

}
