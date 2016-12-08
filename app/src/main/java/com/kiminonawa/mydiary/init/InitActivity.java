package com.kiminonawa.mydiary.init;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.kiminonawa.mydiary.BuildConfig;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.main.MainActivity;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ScreenHelper;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.Locale;


public class InitActivity extends Activity implements InitTask.InitCallBack {

    private TextView TV_init_message;

    private int initTime = 3000; // 3S
    private Handler initHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLocaleLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        ThemeManager themeManager = ThemeManager.getInstance();
        themeManager.setCurrentTheme(SPFManager.getTheme(InitActivity.this));
        initHandler = new Handler();
        //init UI
        TV_init_message = (TextView) findViewById(R.id.TV_init_message);

        //Init photo value
        //topbar height
        int topbarHeight = getResources().getDimensionPixelOffset(R.dimen.top_bar_height);
        //topic bg
        int bgWeight = ScreenHelper.getScreenWidth(InitActivity.this);
        int bgHeight = ScreenHelper.getScreenHeight(InitActivity.this) -
                //diary activity top bar  + edit bottom bar
                ScreenHelper.dpToPixel(getResources(), 40) - topbarHeight;
        int withoutEditBarHeight = ScreenHelper.getScreenHeight(InitActivity.this) -
                //diary activity top bar
                topbarHeight;
        ThemeManager.getInstance().setBgSize(bgWeight, bgHeight, withoutEditBarHeight);

        //Diary photo size
        int imageHeight = ScreenHelper.getScreenHeight(InitActivity.this)
                //diary activity top bar  -( diary info + diary button bar + padding)
                - topbarHeight - ScreenHelper.dpToPixel(getResources(), 120 + 40 + (2 * 5));
        int imageWeight = ScreenHelper.getScreenWidth(InitActivity.this) -
                ScreenHelper.dpToPixel(getResources(), 2 * 5);
        DiaryItemHelper.setVisibleArea(imageWeight, imageHeight);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //This apk is first install or was updated
        if (SPFManager.getVersionCode(InitActivity.this) < BuildConfig.VERSION_CODE) {
            TV_init_message.setVisibility(View.VISIBLE);
        }
        initHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new InitTask(InitActivity.this, InitActivity.this).execute();
            }
        }, initTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        initHandler.removeCallbacksAndMessages(null);
    }

    public void setLocaleLanguage() {
        Locale locale;
        switch (SPFManager.getLocalLanguageCode(this)) {
            case 1:
                locale = Locale.ENGLISH;
                break;
            case 2:
                locale = Locale.JAPANESE;
                break;
            case 3:
                locale = Locale.TRADITIONAL_CHINESE;
                break;
            case 4:
                locale = Locale.SIMPLIFIED_CHINESE;
                break;
            // 0 = default = language of system
            default:
                locale = Locale.getDefault();
                break;
        }
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        overwriteConfigurationLocale(config, locale);
    }

    private void overwriteConfigurationLocale(Configuration config, Locale locale) {
        config.locale = locale;
        getBaseContext().getResources()
                .updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }


    @Override
    public void onInitCompiled(boolean showReleaseNote) {
        Intent goMainPageIntent = new Intent(InitActivity.this, MainActivity.class);
        goMainPageIntent.putExtra("showReleaseNote", showReleaseNote);
        finish();
        InitActivity.this.startActivity(goMainPageIntent);
    }
}
