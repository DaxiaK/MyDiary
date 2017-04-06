package com.kiminonawa.mydiary.backup;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.backup.obj.BUContactsEntries;
import com.kiminonawa.mydiary.backup.obj.BUDiaryEntries;
import com.kiminonawa.mydiary.backup.obj.BUDiaryItem;
import com.kiminonawa.mydiary.backup.obj.BUMemoEntries;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.shared.FileManager;

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
        void onExportCompiled(String backupZipFilePath);
    }

    private final static String TAG = "ExportAsyncTask";


    /*
     * Backup
     */
    private BackupManager backupManager;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private String backupJsonFilePath;
    private String backupZipRootPath;
    private String backupZipFileName;
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

    public ExportAsyncTask(Context context, ExportCallBack callBack, String backupZipRootPath) {

        this.mContext = context;
        this.backupManager = new BackupManager();
        this.backupManager.initBackupManagerExportInfo();

        this.dbManager = new DBManager(context);
        FileManager backupFM = new FileManager(context, FileManager.BACKUP_DIR);
        this.backupJsonFilePath = backupFM.getDirAbsolutePath() + "/"
                + BackupManager.BACKUP_JSON_FILE_NAME;
        this.backupZipRootPath = backupZipRootPath;
        this.backupZipFileName = BACKUP_ZIP_FILE_HEADER + sdf.format(new Date()) + BACKUP_ZIP_FILE_SUB_FILE_NAME;
        this.callBack = callBack;
        this.progressDialog = new ProgressDialog(context);

        //Init progressDialog
        progressDialog.setMessage(context.getString(R.string.process_dialog_loading));
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
            Toast.makeText(mContext,
                    String.format(mContext.getResources().getString(R.string.toast_export_successful), backupZipFileName)
                    , Toast.LENGTH_LONG).show();
            callBack.onExportCompiled(backupZipRootPath + "/" + backupZipFileName);
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_export_fail), Toast.LENGTH_LONG).show();
        }
    }


    private void deleteBackupJsonFile() {
        new FileManager(mContext, FileManager.BACKUP_DIR).clearDir();
    }

    private void outputBackupJson() throws IOException {
        Writer writer = new FileWriter(backupJsonFilePath);
        Gson gson = new GsonBuilder().create();
        gson.toJson(backupManager, writer);
        writer.close();
    }

    private boolean zipBackupFile() throws IOException {
        ZipManager zipManager = new ZipManager(mContext);
        return zipManager.zipFileAtPath(backupJsonFilePath, backupZipRootPath + "/" + backupZipFileName);
    }


    /**
     * Select the all data from DB.
     * the backupManager will be written into a json file.
     */
    private void exportDataIntoBackupManager() throws Exception {
        dbManager.opeDB();
        Cursor topicCursor = dbManager.selectTopic();
        for (int i = 0; i < topicCursor.getCount(); i++) {
            BackupManager.BackupTopicListBean exportTopic = loadTopicDataFormDB(topicCursor);
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


    private BackupManager.BackupTopicListBean loadTopicDataFormDB(Cursor topicCursor) {
        BackupManager.BackupTopicListBean topic = null;
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
                topic = new BackupManager.BackupTopicListBean(topicCursor.getLong(0), topicCursor.getString(1),
                        topicCursor.getInt(7), topicCursor.getInt(5));
                topic.setTopic_type(ITopic.TYPE_MEMO);
                topic.setMemo_topic_entries_list(memoEntriesItemList);
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
                            new BUDiaryEntries(diaryEntriesCursor.getLong(0),
                                    diaryEntriesCursor.getLong(1), diaryEntriesCursor.getString(2),
                                    diaryEntriesCursor.getInt(3), diaryEntriesCursor.getInt(4),
                                    diaryEntriesCursor.getInt(5) > 0 ? true : false,
                                    diaryEntriesCursor.getString(7), diaryItemItemList));
                    diaryEntriesCursor.moveToNext();
                }
                diaryEntriesCursor.close();

                //Create the BUDiary
                topic = new BackupManager.BackupTopicListBean(topicCursor.getLong(0), topicCursor.getString(1),
                        topicCursor.getInt(7), topicCursor.getInt(5));
                topic.setTopic_type(ITopic.TYPE_DIARY);
                topic.setDiary_topic_entries_list(diaryEntriesItemList);
                break;
            case ITopic.TYPE_CONTACTS:
                //Select contacts entries first
                Cursor contactsEntriesCursor = dbManager.selectContacts(topicCursor.getLong(0));
                List<BUContactsEntries> contactsEntriesItemList = new ArrayList<>();
                for (int j = 0; j < contactsEntriesCursor.getCount(); j++) {
                    contactsEntriesItemList.add(
                            new BUContactsEntries(contactsEntriesCursor.getLong(0),
                                    contactsEntriesCursor.getString(1),
                                    contactsEntriesCursor.getString(2)));
                    contactsEntriesCursor.moveToNext();
                }
                contactsEntriesCursor.close();
                //Create the BUmemo
                topic = new BackupManager.BackupTopicListBean(topicCursor.getLong(0), topicCursor.getString(1),
                        topicCursor.getInt(7), topicCursor.getInt(5));
                topic.setTopic_type(ITopic.TYPE_CONTACTS);
                topic.setContacts_topic_entries_list(contactsEntriesItemList);
                break;
        }
        return topic;

    }

}
