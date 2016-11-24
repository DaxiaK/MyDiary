package com.kiminonawa.mydiary.entries.entries;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.entries.diary.item.IDairyRow;
import com.kiminonawa.mydiary.shared.FileManager;

import java.io.File;

/**
 * Created by daxia on 2016/11/21.
 */

public class UpdateDiaryTask extends AsyncTask<Long, Void, Integer> {

    public interface UpdateDiaryCallBack {
        void onDiaryUpdated();
    }

    public final static int RESULT_UPDATE_SUCCESSFUL = 1;
    public final static int RESULT_UPDATE_ERROR = 2;


    private Context mContext;
    private DBManager dbManager;
    private String title;
    private int moodPosition, weatherPosition;
    private boolean attachment;
    private DiaryItemHelper diaryItemHelper;
    private FileManager tempFileManager, diaryFileManager;
    private ProgressDialog progressDialog;

    private UpdateDiaryCallBack callBack;


    public UpdateDiaryTask(Context context, String title,
                           int moodPosition, int weatherPosition,
                           boolean attachment, DiaryItemHelper diaryItemHelper,
                           FileManager fileManager, UpdateDiaryCallBack callBack) {

        this.dbManager = new DBManager(context);
        this.mContext = context;
        this.title = title;
        this.moodPosition = moodPosition;
        this.weatherPosition = weatherPosition;
        this.attachment = attachment;
        this.diaryItemHelper = diaryItemHelper;
        this.tempFileManager = fileManager;
        this.callBack = callBack;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.process_dialog_saving));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Long... params) {

        int updateResult = RESULT_UPDATE_SUCCESSFUL;
        long topicId = params[0];
        long diaryId = params[1];
        try {
            dbManager.opeDB();
            //Delete all item first
            dbManager.delAllDiaryItemByDiaryId(diaryId);
            //Delete old photo
            diaryFileManager = new FileManager(mContext, topicId, diaryId);
            diaryFileManager.clearDiaryDir();
            //Update Diary
            dbManager.updateDiary(diaryId, title, moodPosition, weatherPosition, attachment);
            for (int i = 0; i < diaryItemHelper.getItemSize(); i++) {
                //Copy photo from temp to diary dir
                if (diaryItemHelper.get(i).getType() == IDairyRow.TYPE_PHOTO) {
                    savePhoto(diaryItemHelper.get(i).getContent());
                }
                //Save new data item
                dbManager.insertDiaryContent(diaryItemHelper.get(i).getType(), i
                        , diaryItemHelper.get(i).getContent(), diaryId);
            }
        } catch (Exception e) {
            updateResult = RESULT_UPDATE_ERROR;
        } finally {
            dbManager.closeDB();
        }
        return updateResult;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        if (result == UpdateDiaryTask.RESULT_UPDATE_SUCCESSFUL) {
            Toast.makeText(mContext, mContext.getString(R.string.toast_diary_update_successful), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.toast_diary_update_fail), Toast.LENGTH_LONG).show();
        }
        callBack.onDiaryUpdated();
    }

    private void savePhoto(String filename) throws Exception {
        FileManager.copy(new File(tempFileManager.getDiaryDir().getAbsoluteFile() + "/" + filename),
                new File(diaryFileManager.getDiaryDir().getAbsoluteFile() + "/" + filename));
    }
}
