package com.kiminonawa.mydiary.entries.diary;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.entries.diary.item.IDairyRow;
import com.kiminonawa.mydiary.shared.file.DirFactory;
import com.kiminonawa.mydiary.shared.file.IDir;
import com.kiminonawa.mydiary.shared.file.MyDiaryFileUtils;

import java.io.File;

/**
 * Created by daxia on 2016/11/21.
 */

public class SaveDiaryTask extends AsyncTask<Long, Void, Integer> {

    public interface SaveDiaryCallBack {
        void onDiarySaved();
    }

    public final static String TAG = "SaveDiaryTask";
    public final static int RESULT_INSERT_SUCCESSFUL = 1;
    public final static int RESULT_INSERT_ERROR = 2;


    private Context mContext;
    private DBManager dbManager;
    private long time;
    private String title;
    private int moodPosition, weatherPosition;
    private boolean attachment;
    private String locationName;
    private DiaryItemHelper diaryItemHelper;
    private IDir tempLocalDir, diaryLocalDir;
    private ProgressDialog progressDialog;

    private SaveDiaryCallBack callBack;


    public SaveDiaryTask(Context context, long time, String title,
                         int moodPosition, int weatherPosition,
                         boolean attachment, String locationName,
                         DiaryItemHelper diaryItemHelper, long topicId, SaveDiaryCallBack callBack) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.process_dialog_saving));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);

        this.dbManager = new DBManager(context);
        this.mContext = context;
        this.time = time;
        this.title = title;
        this.moodPosition = moodPosition;
        this.weatherPosition = weatherPosition;
        this.attachment = attachment;
        this.locationName = locationName;
        this.diaryItemHelper = diaryItemHelper;
        this.tempLocalDir = DirFactory.CreateDiaryAutoSaveDir(context, topicId);
        this.callBack = callBack;

        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Long... params) {

        int saveResult = RESULT_INSERT_SUCCESSFUL;
        long topicId = params[0];
        dbManager.opeDB();
        dbManager.beginTransaction();
        try {
            //Save info
            long diaryId = dbManager.insertDiaryInfo(time,
                    title, moodPosition, weatherPosition,
                    attachment, topicId, locationName);
            //Save content
            diaryLocalDir = DirFactory.CreateDiaryDir(mContext, topicId, diaryId);
            //Check no any garbage in this diary.
            diaryLocalDir.clearDir();
            if (diaryId != -1) {
                for (int i = 0; i < diaryItemHelper.getItemSize(); i++) {
                    //Copy photo from temp to diary dir
                    if (diaryItemHelper.get(i).getType() == IDairyRow.TYPE_PHOTO) {
                        savePhoto(diaryItemHelper.get(i).getContent());
                    }
                    //Save data
                    dbManager.insertDiaryContent(diaryItemHelper.get(i).getType(), i
                            , diaryItemHelper.get(i).getContent(), diaryId);
                }
                dbManager.setTransactionSuccessful();
            } else {
                saveResult = RESULT_INSERT_ERROR;
            }
        } catch (Exception e) {
            Log.e(TAG, "save diary fail", e);
            //Revert the Data
            if (diaryLocalDir != null) {
                diaryLocalDir.clearDir();
            }
            saveResult = RESULT_INSERT_ERROR;
        } finally {
            dbManager.endTransaction();
            dbManager.closeDB();
        }
        return saveResult;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        if (result == SaveDiaryTask.RESULT_INSERT_SUCCESSFUL) {
            Toast.makeText(mContext, mContext.getString(R.string.toast_diary_insert_successful), Toast.LENGTH_LONG).show();
            callBack.onDiarySaved();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_diary_insert_fail), Toast.LENGTH_LONG).show();
        }
    }

    private void savePhoto(String filename) throws Exception {
        MyDiaryFileUtils.copy(new File(tempLocalDir.getDirAbsolutePath() + "/" + filename),
                new File(diaryLocalDir.getDirAbsolutePath() + "/" + filename));
    }
}
