package com.kiminonawa.mydiary.entries.photo;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.statusbar.ChinaPhoneHelper;

/**
 * Created by daxia on 2017/4/12.
 */

public class PhotoOverviewActivity extends AppCompatActivity {


    public final static String PHOTO_OVERVIEW_TOPIC_ID = "PHOTOOVERVIEW_TOPIC_ID";
    private long topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_phoho_overview);
        //For set status bar
        ChinaPhoneHelper.setStatusBar(this, true);
        //get topic id
        topicId = getIntent().getLongExtra(PHOTO_OVERVIEW_TOPIC_ID, -1);
        //get topic fail , close this activity
        if (topicId == -1) {
            Toast.makeText(this, "取得日記資料錯誤", Toast.LENGTH_LONG).show();
            finish();
        }
        showFragment();
    }

    private void showFragment() {
        PhotoOverviewFragment photoOverviewFragment = PhotoOverviewFragment.newInstance(topicId);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.RL_diary_photo_overview_content, photoOverviewFragment);
        fragmentTransaction.commit();
    }

}
