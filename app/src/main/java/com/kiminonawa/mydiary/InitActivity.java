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

public class InitActivity extends Activity {


    private int initTime = 3000; // 3S

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        loadSampleData();
        ThemeManager themeManager = ThemeManager.getInstance();
        themeManager.setCurrentTheme(SPFManager.getTheme(InitActivity.this));

        Handler initHandler = new Handler();
        initHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent goMainPageIntent = new Intent(InitActivity.this, MainActivity.class);
                finish();
                InitActivity.this.startActivity(goMainPageIntent);
            }
        }, initTime);
    }

    private void loadSampleData() {
        if (SPFManager.getFirstRun(InitActivity.this)) {

            DBManager dbManager = new DBManager(InitActivity.this);
            dbManager.opeDB();

            //Insert sample topic
            dbManager.insertTopic("ゼッタイ禁止", ITopic.TYPE_MEMO);
            dbManager.insertTopic("禁止事項 Ver.5", ITopic.TYPE_MEMO);
            dbManager.insertTopic("DIARY", ITopic.TYPE_DIARY);
            dbManager.insertTopic("電話番号", ITopic.TYPE_CONTACTS);


            //Insert sample diary
            dbManager.insetDiary(1475665800000L, "東京生活3❤",
                    "There are many coffee shop in Tokyo!",
                    DiaryInfo.MOOD_HAPPY, DiaryInfo.WEATHER_RAINY, true, 3, "Tokyo");
            dbManager.insetDiary(1475241600000L, "No Title",
                    "My name is TAKI , I am a man!",
                    DiaryInfo.MOOD_SOSO, DiaryInfo.WEATHER_SUNNY, true, 3, "Itomori");
            dbManager.insetDiary(1475144400000L, "東京生活2",
                    "Today is second day , I like Tokyo!",
                    DiaryInfo.MOOD_UNHAPPY, DiaryInfo.WEATHER_CLOUD, false, 3, "Tokyo");

            //Insert sample memo
            dbManager.insetMemo("お風呂ぜっっったい禁止！！！！！！！", false, 1);
            dbManager.insetMemo("体は見ない/触らない！！", false, 1);
            dbManager.insetMemo("脚をひらくな！", false, 1);
            dbManager.insetMemo("男子に触るな！", false, 1);
            dbManager.insetMemo("女子にも触るな！", false, 1);


            dbManager.closeDB();
            SPFManager.setFirstRun(InitActivity.this, false);
        }
    }
}
