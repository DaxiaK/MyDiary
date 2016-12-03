package com.kiminonawa.mydiary.init;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.kiminonawa.mydiary.BuildConfig;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.main.MainActivity;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ScreenHelper;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.Locale;


public class InitActivity extends Activity implements InitTask.InitCallBack {

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
        //Init Object
        int imageHeight = ScreenHelper.getScreenHeight(InitActivity.this) -
                //diary activity top bar + diary info + diary botton bar + padding
                ScreenHelper.dpToPixel(getResources(), 80 + 120 + 40 + (2 * 5));
        int imageWeight = ScreenHelper.getScreenWidth(InitActivity.this) -
                ScreenHelper.dpToPixel(getResources(), 2 * 5);
        DiaryItemHelper.setVisibleArea(imageWeight, imageHeight);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //This apk is first install or was updated
        if (SPFManager.getVersionCode(InitActivity.this) < BuildConfig.VERSION_CODE) {
            Toast.makeText(InitActivity.this, "正在更新資料，請勿關閉MyDiary以免資料遺失", Toast.LENGTH_LONG).show();
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
