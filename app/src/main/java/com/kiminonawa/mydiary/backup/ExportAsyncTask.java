package com.kiminonawa.mydiary.backup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.backup.obj.BUContacts;
import com.kiminonawa.mydiary.backup.obj.BUDiary;
import com.kiminonawa.mydiary.backup.obj.BUDiaryEntries;
import com.kiminonawa.mydiary.backup.obj.BUDiaryItem;
import com.kiminonawa.mydiary.backup.obj.BUMemo;
import com.kiminonawa.mydiary.backup.obj.BUMemoEntries;
import com.kiminonawa.mydiary.backup.obj.BUTopic;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.shared.FileManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kiminonawa.mydiary.backup.BackupManager.BACKUP_ZIP_FILE_HEADER;
import static com.kiminonawa.mydiary.backup.BackupManager.BACKUP_ZIP_FILE_SUB_FILE_NAME;

/**
 * Created by daxia on 2017/2/16.
 */

public class ExportAsyncTask extends AsyncTask<Void, Void, Boolean> {

    public interface ExportCallBack {
        void onExportCompiled(boolean exportSuccessful);
    }

    private final static String TAG = "ExportAsyncTask";


    /*
     * Backup
     */
    private BackupManager backupManager;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private String backupJsonFilePath;
    private String backupZieFilePath;
    /*
     * DB
     */
    private DBManager dbManager;

    /*
     * UI
     */
    //From MyDiaryApplication Context
    private Context mContext;
    private ProgressDialog progressDialog;
    private ExportCallBack callBack;

    public ExportAsyncTask(Activity activity, ExportCallBack callBack, String backupZieFilePath) {

        this.mContext = activity.getApplicationContext();
        this.backupManager = new BackupManager();
        this.dbManager = new DBManager(activity.getApplicationContext());
        this.backupJsonFilePath = new FileManager(activity.getApplicationContext(), FileManager.BACKUP_DIR).getDiaryDirAbsolutePath() + "/"
                + BackupManager.BACKUP_JSON_FILE_NAME;
        this.backupZieFilePath = backupZieFilePath;
        this.callBack = callBack;
        this.progressDialog = new ProgressDialog(activity);

        //Init progressDialog
        progressDialog.setMessage(activity.getString(R.string.process_dialog_loading));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean exportSuccessful = true;
        try {
            //Load the data
            exportDataIntoBackupManager();
            //Create backup.json
            outputBackupJson();
            //Zip the json file and photo
            zipBackupFile();
            //Delete the json file
            deleteBackupJsonFile();
        } catch (Exception e) {
            Log.e(TAG, "export fail", e);
            exportSuccessful = false;
        }

        return exportSuccessful;
    }

    @Override
    protected void onPostExecute(Boolean exportSuccessful) {
        super.onPostExecute(exportSuccessful);
        progressDialog.dismiss();
        if (exportSuccessful) {
            Toast.makeText(mContext, "匯出成功", Toast.LENGTH_LONG);
        } else {
            Toast.makeText(mContext, "糟糕，匯出失敗了...請檢查權限或洽詢作者", Toast.LENGTH_LONG);
        }
        callBack.onExportCompiled(exportSuccessful);
    }


    private void deleteBackupJsonFile() {
        new File(backupJsonFilePath).delete();
    }

    private void outputBackupJson() throws IOException {
        Writer writer = new FileWriter(backupJsonFilePath);
        Gson gson = new GsonBuilder().create();
        gson.toJson(backupManager, writer);
        writer.close();
    }

    private boolean zipBackupFile() throws IOException {
        ZipManager zipManager = new ZipManager(mContext, backupJsonFilePath);
        //TODO This file path should be selected by user
        return zipManager.zipFileAtPath(backupZieFilePath + "/" +
                BACKUP_ZIP_FILE_HEADER + sdf.format(new Date()) + BACKUP_ZIP_FILE_SUB_FILE_NAME);
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
                topic = new BUContacts(topicCursor.getLong(0), topicCursor.getString(1),
                        topicCursor.getInt(7), topicCursor.getInt(5), null);
                break;
        }
        return topic;

    }

}
