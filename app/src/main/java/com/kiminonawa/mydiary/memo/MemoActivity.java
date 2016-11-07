package com.kiminonawa.mydiary.memo;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class MemoActivity extends AppCompatActivity {


    /**
     * getId
     */
    private long topicId;

    /**
     * UI
     */
    private RelativeLayout RL_memo_topbar_content;
    private TextView TV_memo_topbar_title;
    private FloatingActionButton FAB_memo_edit;

    /**
     * DB
     */
    private DBManager dbManager;
    /**
     * RecyclerView
     */
    private RecyclerView RecyclerView_memo;
    private MemoAdapter memoAdapter;
    private List<MemoEntity> memoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        topicId = getIntent().getLongExtra("topicId", -1);
        if (topicId == -1) {
            //TODO close this activity and show toast
        }
        /**
         * init UI
         */
        RL_memo_topbar_content = (RelativeLayout) findViewById(R.id.RL_memo_topbar_content);
        RL_memo_topbar_content.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(this));

        TV_memo_topbar_title = (TextView) findViewById(R.id.TV_memo_topbar_title);
        FAB_memo_edit = (FloatingActionButton) findViewById(R.id.FAB_memo_edit);
        FAB_memo_edit.setBackgroundTintList(ColorStateList.valueOf(ThemeManager.getInstance().getThemeMainColor(this)));

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
        memoAdapter = new MemoAdapter(MemoActivity.this, memoList, dbManager);
        RecyclerView_memo.setAdapter(memoAdapter);
    }


}
