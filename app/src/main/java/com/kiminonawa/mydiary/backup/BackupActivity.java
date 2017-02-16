package com.kiminonawa.mydiary.backup;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.backup.obj.BUDiary;
import com.kiminonawa.mydiary.backup.obj.BUDiaryEntries;
import com.kiminonawa.mydiary.backup.obj.BUDiaryItem;
import com.kiminonawa.mydiary.backup.obj.BUMemo;
import com.kiminonawa.mydiary.backup.obj.BUMemoEntries;
import com.kiminonawa.mydiary.backup.obj.BUTopic;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public class BackupActivity extends AppCompatActivity implements View.OnClickListener {


    private final static String TAG = "BackupActivity";

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
    }


    private void outputBackupJson() throws IOException {
            Writer writer = new FileWriter("/sdcard/Output.json");
            Gson gson = new GsonBuilder().create();
            gson.toJson(backupManager, writer);
            writer.close();
    }

    private void zipBackupFile() throws IOException {
        ZipManager zipManager = new ZipManager(this);
        zipManager.zipFileAtPath("/sdcard/123.zip");
    }


    /**
     * Select the all data from DB.
     * the backupManager will be written into a json file.
     */
    private void exportDataIntoBackupManager() throws Exception {
        dbManager.opeDB();
        Cursor topicCursor = dbManager.selectTopic();
        for (int i = 0; i < topicCursor.getCount(); i++) {
            BUTopic exportTopic = loadTopicDataFormDB(topicCursor);
            if (exportTopic != null) {
                backupManager.addTopic(exportTopic);
                topicCursor.moveToNext();
            } else {
                throw new Exception("backup type Exception");
            }
        }
        topicCursor.close();
        dbManager.closeDB();
    }


    private BUTopic loadTopicDataFormDB(Cursor topicCursor) {
        BUTopic topic = null;
        switch (topicCursor.getInt(2)) {
            //This memo have 2 part , 1st is topic , 2nd is memo entries
            case ITopic.TYPE_MEMO:
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
                topic = new BUMemo(topicCursor.getLong(0), topicCursor.getString(1),
                        topicCursor.getInt(7), topicCursor.getInt(5), memoEntriesItemList);
                break;
            //   This diary have 3 part , 1st is topic , 2nd is diary entries , 3rd is diary item (content)
            case ITopic.TYPE_DIARY:
                //Select diary entries first
                Cursor diaryEntriesCursor = dbManager.selectDiaryList(topicCursor.getLong(0));
                List<BUDiaryEntries> diaryEntriesItemList = new ArrayList<>();

                for (int j = 0; j < diaryEntriesCursor.getCount(); j++) {

                    Cursor diaryItemCursor = dbManager.selectDiaryContentByDiaryId(diaryEntriesCursor.getLong(0));
                    List<BUDiaryItem> diaryItemItemList = new ArrayList<>();
                    for (int k = 0; k < diaryItemCursor.getCount(); k++) {
                        diaryItemItemList.add(
                                new BUDiaryItem(diaryItemCursor.getInt(1), diaryItemCursor.getInt(2),
                                        diaryItemCursor.getString(3)));
                        diaryItemCursor.moveToNext();
                    }
                    diaryItemCursor.close();
                    diaryEntriesItemList.add(
                            new BUDiaryEntries(diaryEntriesCursor.getLong(0), diaryEntriesCursor.getLong(1),
                                    diaryEntriesCursor.getInt(3), diaryEntriesCursor.getInt(4),
                                    diaryEntriesCursor.getInt(5) > 0 ? true : false,
                                    diaryEntriesCursor.getString(7), diaryItemItemList));
                    diaryEntriesCursor.moveToNext();
                }
                diaryEntriesCursor.close();

                //Create the BUDiary
                topic = new BUDiary(topicCursor.getLong(0), topicCursor.getString(1),
                        topicCursor.getInt(7), topicCursor.getInt(5), diaryEntriesItemList);
                break;
            case ITopic.TYPE_CONTACTS:
                break;
        }
        return topic;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_backup_export:
                //TODO crate a AsyncTask for doingBackground
                try {
                    exportDataIntoBackupManager();
                    outputBackupJson();
                    zipBackupFile();
                } catch (Exception e) {
                    Log.e(TAG, "export fail", e);
                }
                break;
            case R.id.But_backup_import:
                break;
        }
    }
}
