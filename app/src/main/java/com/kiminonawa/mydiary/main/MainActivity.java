package com.kiminonawa.mydiary.main;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.main.topic.Contacts;
import com.kiminonawa.mydiary.main.topic.Diary;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.main.topic.Memo;
import com.kiminonawa.mydiary.oobe.CustomViewTarget;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;
import com.kiminonawa.mydiary.shared.statusbar.ChinaPhoneHelper;
import com.kiminonawa.mydiary.shared.statusbar.OOBE;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        TopicDetailDialogFragment.TopicCreatedCallback, YourNameDialogFragment.YourNameCallback,
        TopicDeleteDialogFragment.DeleteCallback, TextWatcher {

    private static final String TAG = "MainActivity";

    /*
     * RecyclerView
     */
    private RecyclerView RecyclerView_topic;
    private MainTopicAdapter mainTopicAdapter;
    private List<ITopic> topicList;

    //swipe
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;

    //drag
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    /*
     * DB
     */
    private DBManager dbManager;
    /*
     * Back button event
     */

    private static Boolean isExit = false;
    private Timer backTimer = new Timer();
    /*
     * OOBE
     */
    private int oobeCount = 0;
    private ShowcaseView sv;

    /*
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
        ChinaPhoneHelper.setStatusBar(this, true);

        themeManager = ThemeManager.getInstance();

        LL_main_profile = (LinearLayout) findViewById(R.id.LL_main_profile);
        LL_main_profile.setOnClickListener(this);

        IV_main_profile_picture = (ImageView) findViewById(R.id.IV_main_profile_picture);
        TV_main_profile_username = (TextView) findViewById(R.id.TV_main_profile_username);

        EDT_main_topic_search = (EditText) findViewById(R.id.EDT_main_topic_search);
        EDT_main_topic_search.addTextChangedListener(this);

        IV_main_setting = (ImageView) findViewById(R.id.IV_main_setting);
        IV_main_setting.setOnClickListener(this);

        RecyclerView_topic = (RecyclerView) findViewById(R.id.RecyclerView_topic);

        topicList = new ArrayList<>();
        dbManager = new DBManager(MainActivity.this);

        initProfile();
        initBottomBar();
        initTopicAdapter();
        loadProfilePicture();

        //Init topic adapter
        dbManager.opeDB();
        loadTopic();
        dbManager.closeDB();
        mainTopicAdapter.notifyDataSetChanged(true);

        initOOBE();
        //Check show Release note dialog.
        if (getIntent().getBooleanExtra("showReleaseNote", false)) {
            ReleaseNoteDialogFragment releaseNoteDialogFragment = new ReleaseNoteDialogFragment();
            releaseNoteDialogFragment.show(getSupportFragmentManager(), "releaseNoteDialogFragment");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //It should be reload
        countTopicContent();
    }

    @Override
    protected void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroy() {

        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }
        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager.release();
            mRecyclerViewSwipeManager = null;
        }

        if (mRecyclerViewTouchActionGuardManager != null) {
            mRecyclerViewTouchActionGuardManager.release();
            mRecyclerViewTouchActionGuardManager = null;
        }

        if (RecyclerView_topic != null) {
            RecyclerView_topic.setItemAnimator(null);
            RecyclerView_topic.setAdapter(null);
            RecyclerView_topic = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mainTopicAdapter = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, getString(R.string.main_activity_exit_app), Toast.LENGTH_SHORT).show();
            TimerTask task;
            task = new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            };
            backTimer.schedule(task, 2000);
        } else {
            super.onBackPressed();

        }
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
        Cursor topicCursor = dbManager.selectTopic();
        for (int i = 0; i < topicCursor.getCount(); i++) {
            switch (topicCursor.getInt(2)) {
                case ITopic.TYPE_CONTACTS:
                    topicList.add(
                            new Contacts(topicCursor.getLong(0),
                                    topicCursor.getString(1),topicCursor.getInt(5)));
                    break;
                case ITopic.TYPE_DIARY:
                    topicList.add(
                            new Diary(topicCursor.getLong(0),
                                    topicCursor.getString(1),
                                    topicCursor.getInt(5)));
                    break;
                case ITopic.TYPE_MEMO:
                    topicList.add(
                            new Memo(topicCursor.getLong(0),
                                    topicCursor.getString(1),
                                    topicCursor.getInt(5)));
                    break;
            }
            topicCursor.moveToNext();
        }
        topicCursor.close();
    }

    private void loadProfilePicture() {
        IV_main_profile_picture.setImageDrawable(themeManager.getProfilePictureDrawable(this));
    }

    private void countTopicContent() {
        dbManager.opeDB();
        for (int i = 0; i < topicList.size(); i++) {
            switch (topicList.get(i).getType()) {
                case ITopic.TYPE_CONTACTS:
                    topicList.get(i).setCount(
                            dbManager.getContactsCountByTopicId(topicList.get(i).getId()));
                    break;
                case ITopic.TYPE_DIARY:
                    topicList.get(i).setCount(
                            dbManager.getDiaryCountByTopicId(topicList.get(i).getId()));
                    break;
                case ITopic.TYPE_MEMO:
                    topicList.get(i).setCount(
                            dbManager.getMemoCountByTopicId(topicList.get(i).getId()));
                    break;
            }
        }
        dbManager.closeDB();
        mWrappedAdapter.notifyDataSetChanged();
    }


    private void initTopicAdapter() {

        // For swipe

        // swipe manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        //Init topic adapter
        LinearLayoutManager lmr = new LinearLayoutManager(this);
        RecyclerView_topic.setLayoutManager(lmr);
        RecyclerView_topic.setHasFixedSize(true);
        mainTopicAdapter = new MainTopicAdapter(this, topicList, dbManager);
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mainTopicAdapter);


        final GeneralItemAnimator animator = new DraggableItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.setSupportsChangeAnimations(false);


        //For Drag

        // Setup D&D feature and RecyclerView
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();

        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mWrappedAdapter);


        RecyclerView_topic.setAdapter(mWrappedAdapter);// requires *wrapped* adapter
        RecyclerView_topic.setItemAnimator(animator);

        //For Attach the all manager

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(RecyclerView_topic);
        mRecyclerViewSwipeManager.attachRecyclerView(RecyclerView_topic);

        mRecyclerViewDragDropManager.attachRecyclerView(RecyclerView_topic);
    }

    private void initOOBE() {

        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();

        final RelativeLayout.LayoutParams centerParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        centerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        centerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        centerParams.setMargins(0, 0, 0, margin);

        final RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        leftParams.setMargins(margin, margin, margin, margin);

        View.OnClickListener showcaseViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (oobeCount) {
                    case 0:
                        sv.setButtonPosition(centerParams);
                        sv.setShowcase(new CustomViewTarget(RecyclerView_topic, 4, 4), true);
                        sv.setContentTitle(getString(R.string.oobe_main_topic_list_title));
                        sv.setContentText(getString(R.string.oobe_main_topic_list_content));
                        break;
                    case 1:
                        sv.setButtonPosition(leftParams);
                        sv.setShowcase(new ViewTarget(EDT_main_topic_search), true);
                        sv.setContentTitle(getString(R.string.oobe_main_search_title));
                        sv.setContentText(getString(R.string.oobe_main_search_content));
                        break;
                    case 2:
                        sv.setButtonPosition(centerParams);
                        sv.setShowcase(new ViewTarget(IV_main_setting), true);
                        sv.setContentTitle(getString(R.string.oobe_main_adv_setting_title));
                        sv.setContentText(getString(R.string.oobe_main_adv_setting_content));
                        break;
                    case 3:
                        sv.setButtonPosition(centerParams);
                        sv.setTarget(Target.NONE);
                        sv.setContentTitle(getString(R.string.oobe_main_mydiary_title));
                        sv.setContentText(getString(R.string.oobe_main_mydiary_content));
                        sv.setButtonText(getString(R.string.dialog_button_ok));
                        break;
                    case 4:
                        sv.hide();
                        break;
                }
                oobeCount++;
            }
        };


        Target viewTarget = new ViewTarget(IV_main_profile_picture);
        sv = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(viewTarget)
                .setContentTitle(getString(R.string.oobe_main_your_name_title))
                .setContentText(getString(R.string.oobe_main_your_name_content))
                .setStyle(R.style.OOBEShowcaseTheme)
                .singleShot(OOBE.MAIN_PAGE)
                .replaceEndButton(new MyDiaryButton(this))
                .setOnClickListener(showcaseViewOnClickListener)
                .build();
        sv.setButtonText(getString(R.string.oobe_next_button));
        sv.setButtonPosition(leftParams);
    }

    private void updateTopicBg(int position, int topicBgStatus, String newTopicBgFileName) {
        switch (topicBgStatus) {
            case TopicDetailDialogFragment.TOPIC_BG_ADD_PHOTO:
                File outputFile = themeManager.getTopicBgSavePathFile(
                        this, mainTopicAdapter.getList().get(position).getId(),
                        mainTopicAdapter.getList().get(position).getType());
                //Copy file into topic dir
                try {
                    if (outputFile.exists()) {
                        outputFile.delete();
                    }
                    FileManager tempFM = new FileManager(this, FileManager.TEMP_DIR);
                    FileUtils.moveFile(new File(
                                    tempFM.getDirAbsolutePath()
                                            + "/" + newTopicBgFileName),
                            outputFile);
                    //Enter the topic
                    mainTopicAdapter.gotoTopic(mainTopicAdapter.getList().get(position).getType(), position);
                } catch (IOException e) {
                    Toast.makeText(this, getString(R.string.topic_topic_bg_fail), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
            case TopicDetailDialogFragment.TOPIC_BG_REVERT_DEFAULT:
                File topicBgFile = themeManager.getTopicBgSavePathFile(
                        this, mainTopicAdapter.getList().get(position).getId(),
                        mainTopicAdapter.getList().get(position).getType());
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
                yourNameDialogFragment.show(getSupportFragmentManager(), "yourNameDialogFragment");
                break;
            case R.id.IV_main_setting:
                MainSettingDialogFragment mainSettingDialogFragment = new MainSettingDialogFragment();
                mainSettingDialogFragment.show(getSupportFragmentManager(), "mainSettingDialogFragment");
                break;
        }
    }

    @Override
    public void TopicCreated(final String topicTitle, final int type, final int color) {
        dbManager.opeDB();
        //Create newTopic into List first
        final long newTopicId = dbManager.insertTopic(topicTitle, type, color);
        //This ITopic is temp object to order
        topicList.add(0,
                new ITopic() {
                    @Override
                    public String getTitle() {
                        return topicTitle;
                    }

                    @Override
                    public void setTitle(String title) {
                        //do nothing
                    }

                    @Override
                    public int getType() {
                        return type;
                    }

                    @Override
                    public long getId() {
                        return newTopicId;
                    }

                    @Override
                    public int getIcon() {
                        return 0;
                    }

                    @Override
                    public void setCount(long count) {

                    }
                    @Override
                    public long getCount() {
                        return 0;
                    }

                    @Override
                    public int getColor() {
                        return color;
                    }

                    @Override
                    public void setColor(int color) {
                        //do nothing
                    }

                    @Override
                    public void setPinned(boolean pinned) {
                        //do nothing
                    }

                    @Override
                    public boolean isPinned() {
                        return false;
                    }
                });
        //Get size
        int orderNumber = topicList.size();
        //Remove this topic order
        dbManager.deleteAllCurrentTopicOrder();
        //sort the topic order
        for (ITopic topic : topicList) {
            dbManager.insertTopicOrder(topic.getId(), --orderNumber);
        }
        loadTopic();
        dbManager.closeDB();
        mainTopicAdapter.notifyDataSetChanged(true);
        //Clear the filter
        EDT_main_topic_search.setText("");
    }


    @Override
    public void TopicUpdated(int position, String newTopicTitle, int color, int topicBgStatus, String newTopicBgFileName) {
        DBManager dbManager = new DBManager(this);
        dbManager.opeDB();
        dbManager.updateTopic(mainTopicAdapter.getList().get(position).getId(), newTopicTitle, color);
        dbManager.closeDB();
        //Update filter list
        mainTopicAdapter.getList().get(position).setTitle(newTopicTitle);
        mainTopicAdapter.getList().get(position).setColor(color);
        mainTopicAdapter.notifyDataSetChanged(false);

        updateTopicBg(position, topicBgStatus, newTopicBgFileName);
        //Clear the filter
        EDT_main_topic_search.setText("");
    }

    @Override
    public void onTopicDelete(final int position) {
        DBManager dbManager = new DBManager(MainActivity.this);
        dbManager.opeDB();
        switch (mainTopicAdapter.getList().get(position).getType()) {
            case ITopic.TYPE_CONTACTS:
                dbManager.delAllContactsInTopic(mainTopicAdapter.getList().get(position).getId());
                break;
            case ITopic.TYPE_MEMO:
                dbManager.delAllMemoInTopic(mainTopicAdapter.getList().get(position).getId());
                dbManager.deleteAllCurrentMemoOrder(mainTopicAdapter.getList().get(position).getId());
                break;
            case ITopic.TYPE_DIARY:
                //Clear the auto save content
                SPFManager.clearDiaryAutoSave(this, mainTopicAdapter.getList().get(position).getId());
                //Because FOREIGN key is not work in this version,
                //so delete diary item first , then delete diary
                Cursor diaryCursor = dbManager.selectDiaryList(mainTopicAdapter.getList().get(position).getId());
                for (int i = 0; i < diaryCursor.getCount(); i++) {
                    dbManager.delAllDiaryItemByDiaryId(diaryCursor.getLong(0));
                    diaryCursor.moveToNext();
                }
                diaryCursor.close();
                dbManager.delAllDiaryInTopic(mainTopicAdapter.getList().get(position).getId());
                break;
        }
        //Delete the dir if it exist.
        try {
            FileUtils.deleteDirectory(new FileManager(MainActivity.this,
                    mainTopicAdapter.getList().get(position).getType(),
                    mainTopicAdapter.getList().get(position).getId()).getDir());
        } catch (IOException e) {
            //Do nothing if delete fail
            e.printStackTrace();
        }
        dbManager.delTopic(mainTopicAdapter.getList().get(position).getId());
        //Don't delete the topic order, it will be refreshed next moving time.
        dbManager.closeDB();
        //Search for remove the topiclist
        for (int i = 0; i < topicList.size(); i++) {
            if (topicList.get(i).getId() == mainTopicAdapter.getList().get(position).getId()) {
                topicList.remove(i);
                break;
            }
        }
        //remove the filter list
        mainTopicAdapter.getList().remove(position);
        //Notify recycle view
        mainTopicAdapter.notifyItemRemoved(position);
        mainTopicAdapter.notifyItemRangeChanged(position, mainTopicAdapter.getItemCount());
        //Clear the filter
        EDT_main_topic_search.setText("");
    }


    @Override
    public void updateName() {
        initProfile();
        loadProfilePicture();
    }

    /*
     * For search
     */

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mainTopicAdapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
