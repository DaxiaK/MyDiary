package com.kiminonawa.mydiary.main;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.main.topic.Contacts;
import com.kiminonawa.mydiary.main.topic.Diary;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.main.topic.Memo;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.statusbar.ChinaPhoneHelper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        TopicDetailDialogFragment.TopicCreatedCallback, YourNameDialogFragment.YourNameCallback {


    /**
     * RecyclerView
     */
    private RecyclerView RecyclerView_topic;
    private MainTopicAdapter mainTopicAdapter;
    private List<ITopic> topicList;
    /**
     * DB
     */
    private DBManager dbManager;

    /**
     * UI
     */
    private ThemeManager themeManager;
    private ImageView IV_main_profile_picture;

    private LinearLayout LL_main_profile;
    private TextView TV_main_profile_username;
    private EditText EDT_main_topic_search;
    private ImageView IV_main_setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set layout
        setContentView(R.layout.activity_main);
        //For set status bar
        ChinaPhoneHelper.setStatusBar(this,true);

        themeManager = ThemeManager.getInstance();

        LL_main_profile = (LinearLayout) findViewById(R.id.LL_main_profile);
        LL_main_profile.setOnClickListener(this);

        IV_main_profile_picture = (ImageView) findViewById(R.id.IV_main_profile_picture);
        TV_main_profile_username = (TextView) findViewById(R.id.TV_main_profile_username);

        EDT_main_topic_search = (EditText) findViewById(R.id.EDT_main_topic_search);
        IV_main_setting = (ImageView) findViewById(R.id.IV_main_setting);
        IV_main_setting.setOnClickListener(this);

        RecyclerView_topic = (RecyclerView) findViewById(R.id.RecyclerView_topic);

        topicList = new ArrayList<>();
        dbManager = new DBManager(MainActivity.this);

        initProfile();
        initBottomBar();
        initTopicAdapter();
        loadProfilePicture();

        //Release note dialog
        if (getIntent().getBooleanExtra("showReleaseNote", true)) {
            ReleaseNoteDialogFragment releaseNoteDialogFragment = new ReleaseNoteDialogFragment();
            releaseNoteDialogFragment.show(getSupportFragmentManager(), "releaseNoteDialogFragment");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Init topic adapter
        loadTopic();
        mainTopicAdapter.notifyDataSetChanged();
    }

    private void initProfile() {
        String YourNameIs = SPFManager.getYourName(MainActivity.this);
        if (YourNameIs == null || "".equals(YourNameIs)) {
            YourNameIs = themeManager.getThemeUserName(MainActivity.this);
        }
        TV_main_profile_username.setText(YourNameIs);
        LL_main_profile.setBackground(themeManager.getProfileBgDrawable(this));
    }

    private void initBottomBar() {
        EDT_main_topic_search.getBackground().setColorFilter(themeManager.getThemeMainColor(this),
                PorterDuff.Mode.SRC_ATOP);
        IV_main_setting.setColorFilter(themeManager.getThemeMainColor(this));
    }

    private void loadTopic() {
        topicList.clear();
        dbManager.opeDB();
        Cursor topicCursor = dbManager.selectTopic();
        for (int i = 0; i < topicCursor.getCount(); i++) {
            switch (topicCursor.getInt(2)) {
                case ITopic.TYPE_CONTACTS:
                    topicList.add(
                            new Contacts(topicCursor.getLong(0), topicCursor.getString(1),
                                    dbManager.getContactsCountByTopicId(topicCursor.getLong(0)),
                                    topicCursor.getInt(5)));
                    break;
                case ITopic.TYPE_DIARY:
                    topicList.add(
                            new Diary(topicCursor.getLong(0), topicCursor.getString(1),
                                    dbManager.getDiaryCountByTopicId(topicCursor.getLong(0)),
                                    topicCursor.getInt(5)));
                    break;
                case ITopic.TYPE_MEMO:
                    topicList.add(
                            new Memo(topicCursor.getLong(0), topicCursor.getString(1),
                                    dbManager.getMemoCountByTopicId(topicCursor.getLong(0)),
                                    topicCursor.getInt(5)));
                    break;
            }
            topicCursor.moveToNext();
        }
        topicCursor.close();
        dbManager.closeDB();
    }

    private void loadProfilePicture() {
        IV_main_profile_picture.setImageDrawable(themeManager.getProfilePictureDrawable(this));
    }


    private void initTopicAdapter() {
        //Init topic adapter
        LinearLayoutManager lmr = new LinearLayoutManager(this);
        RecyclerView_topic.setLayoutManager(lmr);
        RecyclerView_topic.setHasFixedSize(true);
        mainTopicAdapter = new MainTopicAdapter(this, topicList);
        RecyclerView_topic.setAdapter(mainTopicAdapter);
    }

    private void updateTopicBg(int position, int topicBgStatus, String newTopicBgFileName) {
        switch (topicBgStatus) {
            case TopicDetailDialogFragment.TOPIC_BG_ADD_PHOTO:
                File outputFile = themeManager.getTopicBgSavePathFile(
                        this, topicList.get(position).getId(), topicList.get(position).getType());
                //Copy file into topic dir
                try {
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    FileUtils.moveFile(new File(
                                    new FileManager(this, FileManager.TEMP_DIR).getDiaryDirAbsolutePath()
                                            + "/" + newTopicBgFileName),
                            outputFile);
                    //Enter the topic
                    mainTopicAdapter.gotoTopic(topicList.get(position).getType(), position);
                } catch (IOException e) {
                    Toast.makeText(this, getString(R.string.topic_topic_bg_fail), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
            case TopicDetailDialogFragment.TOPIC_BG_REVERT_DEFAULT:
                File topicBgFile = themeManager.getTopicBgSavePathFile(
                        this, topicList.get(position).getId(), topicList.get(position).getType());
                //Just delete the file  , the topic's activity will check file for changing the bg
                if (topicBgFile.exists()) {
                    topicBgFile.delete();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LL_main_profile:
                YourNameDialogFragment yourNameDialogFragment = new YourNameDialogFragment();
                yourNameDialogFragment.setCallBack(this);
                yourNameDialogFragment.show(getSupportFragmentManager(), "yourNameDialogFragment");
                break;
            case R.id.IV_main_setting:
                MainSettingDialogFragment mainSettingDialogFragment = new MainSettingDialogFragment();
                mainSettingDialogFragment.show(getSupportFragmentManager(), "mainSettingDialogFragment");
                break;
        }
    }

    @Override
    public void TopicCreated() {
        loadTopic();
        mainTopicAdapter.notifyDataSetChanged();
    }

    @Override
    public void TopicDeleted(final int position) {
        DBManager dbManager = new DBManager(MainActivity.this);
        dbManager.opeDB();
        switch (topicList.get(position).getType()) {
            case ITopic.TYPE_CONTACTS:
                dbManager.delAllContactsInTopic(topicList.get(position).getId());
                break;
            case ITopic.TYPE_MEMO:
                dbManager.delAllMemoInTopic(topicList.get(position).getId());
                break;
            case ITopic.TYPE_DIARY:
                //Because FOREIGN key is not work in this version,
                //so delete diary item first , then delete diary
                Cursor diaryCursor = dbManager.selectDiaryList(topicList.get(position).getId());
                for (int i = 0; i < diaryCursor.getCount(); i++) {
                    dbManager.delAllDiaryItemByDiaryId(diaryCursor.getLong(0));
                    diaryCursor.moveToNext();
                }
                diaryCursor.close();
                dbManager.delAllDiaryInTopic(topicList.get(position).getId());
                break;
        }
        //Delete the dir if it exist.
        try {
            FileUtils.deleteDirectory(new FileManager(MainActivity.this, topicList.get(position).getType(),
                    topicList.get(position).getId()).getDiaryDir());
        } catch (IOException e) {
            //Do nothing if delete fail
            e.printStackTrace();
        }
        dbManager.delTopic(topicList.get(position).getId());
        dbManager.closeDB();
        topicList.remove(position);
        mainTopicAdapter.notifyItemRemoved(position);
        mainTopicAdapter.notifyItemRangeChanged(position, mainTopicAdapter.getItemCount());
    }

    @Override
    public void TopicUpdated(int position, String newTopicTitle, int color, int topicBgStatus, String newTopicBgFileName) {
        DBManager dbManager = new DBManager(this);
        dbManager.opeDB();
        dbManager.updateTopic(topicList.get(position).getId(), newTopicTitle, color);
        dbManager.closeDB();
        loadTopic();
        mainTopicAdapter.notifyDataSetChanged();
        updateTopicBg(position, topicBgStatus, newTopicBgFileName);
    }

    @Override
    public void updateName() {
        initProfile();
        loadProfilePicture();
    }
}
