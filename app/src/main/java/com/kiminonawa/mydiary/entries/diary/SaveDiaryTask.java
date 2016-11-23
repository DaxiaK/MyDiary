package com.kiminonawa.mydiary.entries.diary;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.entries.diary.item.IDairyRow;
import com.kiminonawa.mydiary.shared.FileManager;

import java.io.File;

/**
 * Created by daxia on 2016/11/21.
 */

public class SaveDiaryTask extends AsyncTask<Long, Void, Integer> {

    public interface TaskCallBack {
        void onDiarySaved();
    }

    public final static int RESULT_SUCCESSFUL = 1;
    public final static int RESULT_INSERT_ERROR = 2;


    private Context mContext;
    private DBManager dbManager;
    private long time;
    private String title;
    private int moodPosition, weatherPosition;
    private boolean attachment;
    private String locationName;
    private DiaryItemHelper diaryItemHelper;
    private FileManager tempfileManager, diaryFileManager;
    private ProgressDialog progressDialog;

    private TaskCallBack callBack;


    public SaveDiaryTask(Context context, long time, String title,
                         int moodPosition, int weatherPosition,
                         boolean attachment, String locationName,
                         DiaryItemHelper diaryItemHelper, FileManager fileManager, TaskCallBack callBack) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
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
        this.tempfileManager = fileManager;
        this.callBack = callBack;

        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Long... params) {

        int saveResult = RESULT_SUCCESSFUL;
        long topicId = params[0];
        dbManager.opeDB();
        //Save info
        long diaryInfoId = dbManager.insertDiaryInfo(time,
                title, moodPosition, weatherPosition,
                attachment, topicId, locationName);
        //Save content
        diaryFileManager = new FileManager(mContext, topicId, diaryInfoId);
        if (diaryInfoId != -1) {
            for (int i = 0; i < diaryItemHelper.getItemSize(); i++) {
                try {
                    //Copy photo from temp to diary dir
                    if (diaryItemHelper.get(i).getType() == IDairyRow.TYPE_PHOTO) {
                        savePhoto(diaryItemHelper.get(i).getContent());
                    }
                    //Save data
                    dbManager.insertDiaryContent(diaryItemHelper.get(i).getType(), i
                            , diaryItemHelper.get(i).getContent(), diaryInfoId);
                } catch (Exception e) {
                    Log.e("SaveDiaryTask", "Item:" + i + " insert fail");
                    saveResult = RESULT_INSERT_ERROR;
                }
            }
        }
        dbManager.closeDB();
        return saveResult;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        progressDialog.dismiss();
        callBack.onDiarySaved();
    }

    private void savePhoto(String filename) throws Exception {
        FileManager.copy(new File(tempfileManager.getDiaryDir().getAbsoluteFile() + "/" + filename),
                new File(diaryFileManager.getDiaryDir().getAbsoluteFile() + "/" + filename));
    }
}
