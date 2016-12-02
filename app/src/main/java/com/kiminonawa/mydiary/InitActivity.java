package com.kiminonawa.mydiary;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.diary.DiaryInfoHelper;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.entries.diary.item.IDairyRow;
import com.kiminonawa.mydiary.main.MainActivity;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ScreenHelper;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.Locale;


/**
 * Version History
 * 20161120
 * Implement diaryDB v2 , update sample data
 * ----
 * 20161109
 * Add contacts function in version 10
 * ----
 * 20161108
 * Add memo function & show memo sample data in versionCode 6
 * ----
 */
public class InitActivity extends Activity {

    private int initTime = 3000; // 3S
    private Handler initHandler;
    private boolean showReleaseNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLocaleLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        ThemeManager themeManager = ThemeManager.getInstance();
        themeManager.setCurrentTheme(SPFManager.getTheme(InitActivity.this));
        initHandler = new Handler();
        //Show release note
        if (!SPFManager.getDescriptionClose(InitActivity.this)) {
            showReleaseNote = true;
        }
        loadSampleData();
        //Init Object
        int imageHeight = ScreenHelper.getScreenHeight(InitActivity.this) -
                //diary activity topbar + diary info + diary botton bar + padding
                ScreenHelper.dpToPixel(getResources(), 80 + 120 + 40 + (2 * 5));
        int imageWeight = ScreenHelper.getScreenWidth(InitActivity.this) -
                ScreenHelper.dpToPixel(getResources(), 2 * 5);
        DiaryItemHelper.setVisibleArea(imageWeight, imageHeight);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent goMainPageIntent = new Intent(InitActivity.this, MainActivity.class);
                goMainPageIntent.putExtra("showReleaseNote", showReleaseNote);
                finish();
                InitActivity.this.startActivity(goMainPageIntent);
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


    private void loadSampleData() {

        DBManager dbManager = new DBManager(InitActivity.this);
        dbManager.opeDB();
        //Because memo function is run in version 6 ,
        //So , if version < 6 , show the sample memo data
        if (SPFManager.getVersionCode(InitActivity.this) < 6) {
            //Insert sample topic
            long mitsuhaMemoId = dbManager.insertTopic("ゼッタイ禁止", ITopic.TYPE_MEMO);
            long takiMemoId = dbManager.insertTopic("禁止事項 Ver.5", ITopic.TYPE_MEMO);
            //Insert sample memo
            if (mitsuhaMemoId != -1) {
                dbManager.insertMemo("女子にも触るな！", false, mitsuhaMemoId);
                dbManager.insertMemo("男子に触るな！", false, mitsuhaMemoId);
                dbManager.insertMemo("脚をひらくな！", true, mitsuhaMemoId);
                dbManager.insertMemo("体は見ない/触らない！！", false, mitsuhaMemoId);
                dbManager.insertMemo("お風呂ぜっっったい禁止！！！！！！！", true, mitsuhaMemoId);
            }
            if (takiMemoId != -1) {
                dbManager.insertMemo("司とベタベタする.....", true, takiMemoId);
                dbManager.insertMemo("奧寺先輩と馴れ馴れしくするな.....", true, takiMemoId);
                dbManager.insertMemo("女言葉NG！", false, takiMemoId);
                dbManager.insertMemo("遅刻するな！", true, takiMemoId);
                dbManager.insertMemo("訛り禁止！", false, takiMemoId);
                dbManager.insertMemo("無駄つかい禁止！", true, takiMemoId);
            }
        }

        if (SPFManager.getVersionCode(InitActivity.this) < 10) {
            //Insert sample topic
            long topicOnDiarySampleId = dbManager.insertTopic("DIARY", ITopic.TYPE_DIARY);
            if (topicOnDiarySampleId != -1) {
                //Insert sample diary
                long diarySampleId = dbManager.insertDiaryInfo(1475665800000L, "東京生活3❤",
                        DiaryInfoHelper.MOOD_HAPPY, DiaryInfoHelper.WEATHER_RAINY, true, topicOnDiarySampleId, "Tokyo");
                dbManager.insertDiaryContent(IDairyRow.TYPE_TEXT, 0, "There are many coffee shop in Tokyo!", diarySampleId);
            }
        }

        //Contacts function work in version 10
        if (SPFManager.getVersionCode(InitActivity.this) < 10) {
            //Insert sample cntacts
            long sampleContactsId = dbManager.insertTopic("緊急狀況以外不要聯絡", ITopic.TYPE_CONTACTS);

            //Insert sample memo
            if (sampleContactsId != -1) {
                dbManager.insertContacts(getString(R.string.profile_username_mitsuha), "090000000", "", sampleContactsId);
            }
        }

        dbManager.closeDB();

        //Save currentVersion
        if (SPFManager.getVersionCode(InitActivity.this) < BuildConfig.VERSION_CODE) {
            SPFManager.setDescriptionClose(InitActivity.this, false);
            showReleaseNote = true;
            SPFManager.setVersionCode(InitActivity.this);
        }

    }
}
