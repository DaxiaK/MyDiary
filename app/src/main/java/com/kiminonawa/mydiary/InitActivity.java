package com.kiminonawa.mydiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.diary.DiaryInfo;
import com.kiminonawa.mydiary.main.MainActivity;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ThemeManager;


/**
 * Version History
 * 20161109
 * Add contacts function in version 9
 * ----
 * 20161108
 * Add memo function & show memo sample data in versionCode 6
 * ----
 */
public class InitActivity extends Activity {

    private int initTime = 3000; // 3S
    private Handler initHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        loadSampleData();
        ThemeManager themeManager = ThemeManager.getInstance();
        themeManager.setCurrentTheme(SPFManager.getTheme(InitActivity.this));

        initHandler = new Handler();
        initHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent goMainPageIntent = new Intent(InitActivity.this, MainActivity.class);
                finish();
                InitActivity.this.startActivity(goMainPageIntent);
            }
        }, initTime);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                dbManager.insetMemo("女子にも触るな！", false, mitsuhaMemoId);
                dbManager.insetMemo("男子に触るな！", false, mitsuhaMemoId);
                dbManager.insetMemo("脚をひらくな！", true, mitsuhaMemoId);
                dbManager.insetMemo("体は見ない/触らない！！", false, mitsuhaMemoId);
                dbManager.insetMemo("お風呂ぜっっったい禁止！！！！！！！", true, mitsuhaMemoId);
            }
            if (takiMemoId != -1) {
                dbManager.insetMemo("司とベタベタする.....", true, takiMemoId);
                dbManager.insetMemo("奧寺先輩と馴れ馴れしくするな.....", true, takiMemoId);
                dbManager.insetMemo("女言葉NG！", false, takiMemoId);
                dbManager.insetMemo("遅刻するな！", true, takiMemoId);
                dbManager.insetMemo("訛り禁止！", false, takiMemoId);
                dbManager.insetMemo("無駄つかい禁止！", true, takiMemoId);
            }
        }

        if (SPFManager.getFirstRun(InitActivity.this)) {
            //Insert sample topic
            long diaryId = dbManager.insertTopic("DIARY", ITopic.TYPE_DIARY);
            if (diaryId != -1) {
                dbManager.insertTopic("電話番号", ITopic.TYPE_CONTACTS);
                //Insert sample diary
                dbManager.insetDiary(1475665800000L, "東京生活3❤",
                        "There are many coffee shop in Tokyo!",
                        DiaryInfo.MOOD_HAPPY, DiaryInfo.WEATHER_RAINY, true, diaryId, "Tokyo");
                dbManager.insetDiary(1475241600000L, "No Title",
                        "My name is TAKI , I am a man!",
                        DiaryInfo.MOOD_SOSO, DiaryInfo.WEATHER_SUNNY, true, diaryId, "Itomori");
                dbManager.insetDiary(1475144400000L, "東京生活2",
                        "Today is second day , I like Tokyo!",
                        DiaryInfo.MOOD_UNHAPPY, DiaryInfo.WEATHER_CLOUD, false, diaryId, "Tokyo");
                SPFManager.setFirstRun(InitActivity.this, false);
            }
        }
        dbManager.closeDB();

        //Save currentVersion
        if (SPFManager.getVersionCode(InitActivity.this) < BuildConfig.VERSION_CODE) {
            SPFManager.setVersionCode(InitActivity.this);
        }

    }
}
