package com.kiminonawa.mydiary.memo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.ViewTools;
import com.kiminonawa.mydiary.shared.statusbar.ChinaPhoneHelper;

import java.util.ArrayList;
import java.util.List;

public class MemoActivity extends FragmentActivity implements View.OnClickListener, EditMemoDialogFragment.MemoCallback {


    /**
     * getId
     */
    private long topicId;

    /**
     * UI
     */
    private RelativeLayout RL_memo_topbar_content;
    private TextView TV_memo_topbar_title;
    private ImageView IV_memo_edit;

    /**
     * DB
     */
    private DBManager dbManager;
    /**
     * RecyclerView
     */
    private RelativeLayout RL_memo_content_bg;
    private RecyclerView RecyclerView_memo;
    private MemoAdapter memoAdapter;
    private List<MemoEntity> memoList;

    @Override
    public void onBackPressed() {
        if (memoAdapter.isEditMode()) {
            setEditModeUI(memoAdapter.isEditMode());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        //For set status bar
        ChinaPhoneHelper.setStatusBar(this,true);

        topicId = getIntent().getLongExtra("topicId", -1);
        if (topicId == -1) {
            finish();
        }
        /**
         * init UI
         */
        RL_memo_topbar_content = (RelativeLayout) findViewById(R.id.RL_memo_topbar_content);
        RL_memo_topbar_content.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(this));

        RL_memo_content_bg = (RelativeLayout) findViewById(R.id.RL_memo_content_bg);
        RL_memo_content_bg.setBackground(ThemeManager.getInstance().getMemoBgDrawable(this, topicId));

        TV_memo_topbar_title = (TextView) findViewById(R.id.TV_memo_topbar_title);
        IV_memo_edit = (ImageView) findViewById(R.id.IV_memo_edit);
        IV_memo_edit.setOnClickListener(this);
        String diaryTitle = getIntent().getStringExtra("diaryTitle");
        if (diaryTitle == null) {
            diaryTitle = "Memo";
        }
        TV_memo_topbar_title.setText(diaryTitle);

        RecyclerView_memo = (RecyclerView) findViewById(R.id.RecyclerView_memo);
        memoList = new ArrayList<>();
        dbManager = new DBManager(MemoActivity.this);

        loadMemo();
        initTopicAdapter();
    }

    private void loadMemo() {
        memoList.clear();
        dbManager.opeDB();
        Cursor memoCursor = dbManager.selectMemo(topicId);
        for (int i = 0; i < memoCursor.getCount(); i++) {
            memoList.add(
                    new MemoEntity(memoCursor.getLong(0), memoCursor.getString(2), memoCursor.getInt(3) > 0 ? true : false));
            memoCursor.moveToNext();
        }
        memoCursor.close();
        dbManager.closeDB();
    }

    private void initTopicAdapter() {
        //Init topic adapter
        LinearLayoutManager lmr = new LinearLayoutManager(this);
        RecyclerView_memo.setLayoutManager(lmr);
        RecyclerView_memo.setHasFixedSize(true);
        memoAdapter = new MemoAdapter(MemoActivity.this, topicId, memoList, dbManager, this);
        RecyclerView_memo.setAdapter(memoAdapter);
    }

    public void setEditModeUI(boolean isEditMode) {
        if (isEditMode) {
            //Cancel edit
            IV_memo_edit.setImageDrawable(ViewTools.getDrawable(MemoActivity.this, R.drawable.ic_mode_edit_white_24dp));
        } else {
            //Start edit
            IV_memo_edit.setImageDrawable(ViewTools.getDrawable(MemoActivity.this, R.drawable.ic_mode_edit_cancel_white_24dp));
        }
        memoAdapter.setEditMode(!isEditMode);
        memoAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_memo_edit:
                setEditModeUI(memoAdapter.isEditMode());
                break;
        }
    }

    @Override
    public void addMemo() {
        loadMemo();
        memoAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateMemo() {
        loadMemo();
        memoAdapter.notifyDataSetChanged();
    }
}
