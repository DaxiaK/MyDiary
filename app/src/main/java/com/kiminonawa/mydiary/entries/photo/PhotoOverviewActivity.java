package com.kiminonawa.mydiary.entries.photo;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;

/**
 * Created by daxia on 2017/4/12.
 */

public class PhotoOverviewActivity extends AppCompatActivity {


    public final static String PHOTO_OVERVIEW_TOPIC_ID = "PHOTOOVERVIEW_TOPIC_ID";
    public final static String PHOTO_OVERVIEW_DIARY_ID = "PHOTOOVERVIEW_DIARY_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_phoho_overview);
        //get topic id
        long topicId = getIntent().getLongExtra(PHOTO_OVERVIEW_TOPIC_ID, -1);
        //get topic fail , close this activity
        if (topicId == -1) {
            Toast.makeText(this, getString(R.string.photo_viewer_topic_fail)
                    , Toast.LENGTH_LONG).show();
            finish();
        }
        long diaryId = getIntent().getLongExtra(PHOTO_OVERVIEW_DIARY_ID, -1);
        showFragment(topicId, diaryId);
    }

    private void showFragment(long topicId, long diaryId) {
        PhotoOverviewFragment photoOverviewFragment = PhotoOverviewFragment.newInstance(topicId, diaryId);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.RL_diary_photo_overview_content, photoOverviewFragment);
        fragmentTransaction.commit();
    }

}
