package com.kiminonawa.mydiary.backup;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.backup.entries.BUMemoEntries;
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
                Cursor diaryCursor = dbManager.selectDiaryList(topicCursor.getLong(0));
                List<BUDiary> diaryItemItemList = new ArrayList<>();
                dbManager.showCursor(diaryCursor);
                for (int j = 0; j < diaryCursor.getCount(); j++) {
//                    diaryItemItemList.add(
//                            new BUMemoEntries(memoCursor.getString(2), memoCursor.getInt(7),
//                                    memoCursor.getInt(3) > 0 ? true : false));
//                    memoCursor.moveToNext();
                }
                //Create the BUmemo
//                backupManager.addTopic(
//                        new BUMemo(topicCursor.getString(1), topicCursor.getInt(7), topicCursor.getInt(5), memoItemItemList));
                diaryCursor.close();
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
                Cursor memoCursor = dbManager.selectMemoAndMemoOrder(topicCursor.getLong(0));
                List<BUMemoEntries> memoItemItemList = new ArrayList<>();
                for (int j = 0; j < memoCursor.getCount(); j++) {
                    memoItemItemList.add(
                            new BUMemoEntries(memoCursor.getString(2), memoCursor.getInt(7),
                                    memoCursor.getInt(3) > 0 ? true : false));
                    memoCursor.moveToNext();
                }
                //Create the BUmemo
                backupManager.addTopic(
                        new BUMemo(topicCursor.getString(1), topicCursor.getInt(7), topicCursor.getInt(5), memoItemItemList));
                memoCursor.close();
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
