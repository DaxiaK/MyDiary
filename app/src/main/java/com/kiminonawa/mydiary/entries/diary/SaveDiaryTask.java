package com.kiminonawa.mydiary.entries.diary;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.entries.diary.item.DiaryPhoto;
import com.kiminonawa.mydiary.entries.diary.item.IDairyRow;
import com.kiminonawa.mydiary.shared.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by daxia on 2016/11/21.
 */

public class SaveDiaryTask extends AsyncTask<String, Void, Void> {

    public interface TaskCallBack {
        void onDiarySaved();
    }

    private DBManager dbManager;
    private long time;
    private String title;
    private int moodPosition, weatherPosition;
    private boolean attachment;
    private long topicId;
    private String locationName;
    private DiaryItemHelper diaryItemHelper;
    private FileManager fileManager;
    private ProgressDialog progressDialog;

    private TaskCallBack callBack;


    public SaveDiaryTask(Context context, long time, String title,
                         int moodPosition, int weatherPosition,
                         boolean attachment, long topicId, String locationName,
                         DiaryItemHelper diaryItemHelper, FileManager fileManager, TaskCallBack callBack) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);

        dbManager = new DBManager(context);
        this.time = time;
        this.title = title;
        this.moodPosition = moodPosition;
        this.weatherPosition = weatherPosition;
        this.attachment = attachment;
        this.topicId = topicId;
        this.locationName = locationName;
        this.diaryItemHelper = diaryItemHelper;
        this.fileManager = fileManager;
        this.callBack = callBack;

        //Create bitmap
        for (int i = 0; i < diaryItemHelper.getItemSize(); i++) {
            if (diaryItemHelper.get(i).getType() == IDairyRow.TYPE_PHOTO) {
                DiaryPhoto diaryPhoto = ((DiaryPhoto) diaryItemHelper.get(i));
                ImageView imageView = diaryPhoto.getPhoto();
                imageView.buildDrawingCache();
                diaryPhoto.setTempBitmap(imageView.getDrawingCache());
            }
        }
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        dbManager.opeDB();
        //Save info
        long diaryInfoId = dbManager.insertDiaryInfo(time,
                title, moodPosition, weatherPosition,
                attachment, topicId, locationName);
        //Save content
        if (diaryInfoId != -1) {
            for (int i = 0; i < diaryItemHelper.getItemSize(); i++) {
                //Save data
                dbManager.insertDiaryContent(diaryItemHelper.get(i).getType(), i
                        , diaryItemHelper.get(i).getContent(), diaryInfoId);
                //Copy photo to sdcard
                if (diaryItemHelper.get(i).getType() == IDairyRow.TYPE_PHOTO) {
                    DiaryPhoto diaryPhoto = ((DiaryPhoto) diaryItemHelper.get(i));
                    savePhoto(diaryInfoId, diaryItemHelper.get(i).getContent(), diaryPhoto.getTempBitmap());
                    diaryPhoto.getTempBitmap().recycle();
                }
            }
        }
        dbManager.closeDB();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        callBack.onDiarySaved();
    }

    private void savePhoto(long diaryId, String filename, Bitmap bitmap) {
        FileOutputStream out = null;
        String savePath = fileManager.getFileRootDir() + "/" + String.valueOf(topicId) +
                "/" + String.valueOf(diaryId);
        File saveFile = new File(savePath);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        try {
            out = new FileOutputStream(savePath + "/" + filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
