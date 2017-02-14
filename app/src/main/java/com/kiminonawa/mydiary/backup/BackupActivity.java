package com.kiminonawa.mydiary.backup;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.backup.entries.BUDiaryEntries;
import com.kiminonawa.mydiary.backup.entries.BUMemoEntries;
import com.kiminonawa.mydiary.backup.item.BUDiaryItem;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public class BackupActivity extends AppCompatActivity implements View.OnClickListener {


    /*
     * Backup
     */
    private BackupManager backupManager;

    /*
     * DB
     */
    private DBManager dbManager;
    private FileManager backupFileManager;

    /*
     * UI
     */
    private MyDiaryButton But_backup_export;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        //For backup
        backupManager = new BackupManager();

        //DB
        dbManager = new DBManager(this);

        //UI
        But_backup_export = (MyDiaryButton) findViewById(R.id.But_backup_export);
        But_backup_export.setOnClickListener(this);

        //TODO crate a AsyncTask for doingBackground
        dbManager.opeDB();
        exportDiary();
        exportMemo();
        backupManager.getTopic(0);
        dbManager.closeDB();
    }


    /**
     * Select the all diary from DB.
     * This diary have 2 part , 1st is topic , 2nd is diary entries , 3rd is diary item (content)
     * the backupManager will be written into a json file.
     */

    private void exportDiary() {
        Cursor topicCursor = dbManager.selectTopic();
        dbManager.showCursor(topicCursor);
        for (int i = 0; i < topicCursor.getCount(); i++) {
            if (topicCursor.getInt(2) == ITopic.TYPE_DIARY) {
                //Select memo first
                Cursor diaryEntriesCursor = dbManager.selectDiaryList(topicCursor.getLong(0));
                List<BUDiaryEntries> diaryEntriesItemList = new ArrayList<>();

                for (int j = 0; j < diaryEntriesCursor.getCount(); j++) {

                    Cursor diaryItemCursor = dbManager.selectDiaryContentByDiaryId(diaryEntriesCursor.getLong(0));
                    List<BUDiaryItem> diaryItemItemList = new ArrayList<>();
                    for (int k = 0; k < diaryItemCursor.getCount(); k++) {
                        diaryItemItemList.add(
                                new BUDiaryItem(diaryItemCursor.getInt(1), diaryItemCursor.getInt(2),
                                        diaryItemCursor.getString(3)));
                    }
                    diaryItemCursor.close();
                    diaryEntriesItemList.add(
                            new BUDiaryEntries(diaryEntriesCursor.getLong(1), diaryEntriesCursor.getInt(3),
                                    diaryEntriesCursor.getInt(4), diaryEntriesCursor.getInt(5) > 0 ? true : false,
                                    diaryEntriesCursor.getString(7), diaryItemItemList));
                    diaryEntriesCursor.moveToNext();
                }
                diaryEntriesCursor.close();

                //Create the BUDiary
                backupManager.addTopic(
                        new BUDiary(topicCursor.getString(1), topicCursor.getInt(7), topicCursor.getInt(5), null));
            }
            topicCursor.moveToNext();
        }
        topicCursor.close();

    }


    /**
     * Select the all memo from DB.
     * This memo have 2 part , 1st is topic , 2nd is memo entries
     * the backupManager will be written into a json file.
     */
    private void exportMemo() {
        Cursor topicCursor = dbManager.selectTopic();
        for (int i = 0; i < topicCursor.getCount(); i++) {
            if (topicCursor.getInt(2) == ITopic.TYPE_MEMO) {
                //Select memo first
                Cursor memoEntriesCursor = dbManager.selectMemoAndMemoOrder(topicCursor.getLong(0));
                List<BUMemoEntries> memoEntriesItemList = new ArrayList<>();
                for (int j = 0; j < memoEntriesCursor.getCount(); j++) {
                    memoEntriesItemList.add(
                            new BUMemoEntries(memoEntriesCursor.getString(2), memoEntriesCursor.getInt(7),
                                    memoEntriesCursor.getInt(3) > 0 ? true : false));
                    memoEntriesCursor.moveToNext();
                }
                memoEntriesCursor.close();
                //Create the BUmemo
                backupManager.addTopic(
                        new BUMemo(topicCursor.getString(1), topicCursor.getInt(7), topicCursor.getInt(5), memoEntriesItemList));
            }
            topicCursor.moveToNext();
        }
        topicCursor.close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_backup_export:
                break;
            case R.id.But_backup_import:
                break;
        }
    }
}
