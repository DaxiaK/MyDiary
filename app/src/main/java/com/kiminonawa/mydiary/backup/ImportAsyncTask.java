package com.kiminonawa.mydiary.backup;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.backup.obj.BUContactsEntries;
import com.kiminonawa.mydiary.backup.obj.BUDiaryEntries;
import com.kiminonawa.mydiary.backup.obj.BUDiaryItem;
import com.kiminonawa.mydiary.backup.obj.BUMemoEntries;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.shared.FileManager;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by daxia on 2017/2/16.
 */

public class ImportAsyncTask extends AsyncTask<Void, Void, Boolean> {

    public interface ImportCallBack {
        void onImportCompiled(boolean importSuccessful);
    }

    private final static String TAG = "ImportAsyncTask";


    /*
     * Backup
     */
    private BackupManager backupManager;
    private FileManager backupFileManager, diartFileManager;
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
    private ImportCallBack callBack;

    public ImportAsyncTask(Context context, ImportCallBack callBack, String backupZieFilePath) {
        this.mContext = context;
        this.dbManager = new DBManager(context);
        FileManager backFM = new FileManager(context, FileManager.BACKUP_DIR);
        this.backupJsonFilePath = backFM.getDirAbsolutePath() + "/"
                + BackupManager.BACKUP_JSON_FILE_NAME;
        this.backupZieFilePath = backupZieFilePath;

        this.backupFileManager = new FileManager(mContext, FileManager.BACKUP_DIR);
        this.diartFileManager = new FileManager(mContext, FileManager.DIARY_ROOT_DIR);

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
        boolean importSuccessful = true;
        try {
            ZipManager zipManager = new ZipManager(mContext);

            FileManager zipBackupFM = new FileManager(mContext, FileManager.BACKUP_DIR);
            zipManager.unzip(backupZieFilePath,
                    zipBackupFM.getDirAbsolutePath() + "/");
            loadBackupJsonFileIntoManager();
            importSuccessful = importTopic();
        } catch (Exception e) {
            Log.e(TAG, "import flow fail", e);
            importSuccessful = false;
        } finally {
            backupFileManager.clearDir();
        }
        return importSuccessful;
    }

    @Override
    protected void onPostExecute(Boolean importSuccessful) {
        super.onPostExecute(importSuccessful);
        progressDialog.dismiss();
        if (importSuccessful) {
            Toast.makeText(mContext, mContext.getString(R.string.toast_import_successful), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_import_fail), Toast.LENGTH_LONG).show();
        }
        callBack.onImportCompiled(importSuccessful);
    }

    private void loadBackupJsonFileIntoManager() throws Exception {
        FileInputStream fis = new FileInputStream(backupJsonFilePath);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString();
        Gson gson = new Gson();
        backupManager = gson.fromJson(json, BackupManager.class);
        if (backupManager.getHeader() == null ||
                !backupManager.getHeader().equals(BackupManager.header)) {
            throw new Exception("This is not mydiary backup file");
        }
    }


    private boolean importTopic() {
        boolean importSuccessful = true;
        try {
            dbManager.opeDB();
            //Start a transaction
            dbManager.beginTransaction();
            for (int i = 0; i < backupManager.getBackup_topic_list().size(); i++) {
                saveTopicIntoDB(backupManager.getBackup_topic_list().get(i));
            }
            //Re-sort the topic

            //Check update success
            dbManager.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "importTopic fail", e);
            importSuccessful = false;
        } finally {
            dbManager.endTransaction();
            dbManager.closeDB();
        }
        return importSuccessful;
    }

    private void saveTopicIntoDB(BackupManager.BackupTopicListBean backupTopic) throws IOException {

        long newTopicId = dbManager.insertTopic(
                backupTopic.getTopic_title(),
                backupTopic.getTopic_type(),
                backupTopic.getTopic_color());


        switch (backupTopic.getTopic_type()) {
            case ITopic.TYPE_MEMO:
                if (backupTopic.getMemo_topic_entries_list() != null) {
                    for (int x = 0; x < backupTopic.getMemo_topic_entries_list().size(); x++) {
                        BUMemoEntries memo = backupTopic.getMemo_topic_entries_list().get(x);
                        long newMemoId = dbManager.insertMemo(memo.getMemoEntriesContent(), memo.isChecked(), newTopicId);
                        dbManager.insertMemoOrder(newTopicId, newMemoId, memo.getMemoEntriesOrder());
                    }
                }
                break;
            case ITopic.TYPE_DIARY:
                if (backupTopic.getDiary_topic_entries_list() != null) {
                    for (int y = 0; y < backupTopic.getDiary_topic_entries_list().size(); y++) {
                        BUDiaryEntries diary = backupTopic.getDiary_topic_entries_list().get(y);
                        //Write the diary entries
                        long newDiaryId =
                                dbManager.insertDiaryInfo(diary.getDiaryEntriesTime(), diary.getDiaryEntriesTitle(),
                                        diary.getDiaryEntriesMood(), diary.getDiaryEntriesWeather(),
                                        diary.isDiaryEntriesAttachment(), newTopicId, diary.getDiaryEntriesLocation());
                        //Write the diary item
                        for (int yi = 0; yi < diary.getDiaryItemList().size(); yi++) {
                            BUDiaryItem diaryItem = diary.getDiaryItemList().get(yi);
                            dbManager.insertDiaryContent(diaryItem.getDiaryItemType(),
                                    diaryItem.getDiaryItemPosition(),
                                    diaryItem.getDiaryItemContent(), newDiaryId);
                        }
                        //Copy the diary photo
                        copyDiaryPhoto(backupTopic.getTopic_id(), newTopicId,
                                diary.getDiaryEntriesId(), newDiaryId);
                    }
                }

                break;
            case ITopic.TYPE_CONTACTS:
                if (backupTopic.getContacts_topic_entries_list() != null) {
                    for (int z = 0; z < backupTopic.getContacts_topic_entries_list().size(); z++) {
                        BUContactsEntries contact = backupTopic.getContacts_topic_entries_list().get(z);
                        dbManager.insertContacts(contact.getContactsEntriesName(),
                                contact.getContactsEntriesPhonenumber(), "", newTopicId);
                    }
                }
                break;
        }
    }

    private void copyDiaryPhoto(long oldTopicId, long newTopicId,
                                long oldDiaryId, long newDiaryId) throws IOException {
        File backupDiaryDir = new File(backupFileManager.getDirAbsolutePath() + "/diary/" +
                oldTopicId + "/" + oldDiaryId + "/");
        if (backupDiaryDir.exists() || backupDiaryDir.isDirectory()) {
            File newDiaryDir = new File(diartFileManager.getDirAbsolutePath() + "/" +
                    newTopicId + "/" + newDiaryId + "/");
            FileUtils.moveDirectory(backupDiaryDir, newDiaryDir);
        }
    }

}
