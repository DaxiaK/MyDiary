package com.kiminonawa.mydiary.entries.diary;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.kiminonawa.mydiary.shared.BitmapHelper;
import com.kiminonawa.mydiary.shared.ExifUtil;
import com.kiminonawa.mydiary.shared.FileManager;

import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by daxia on 2016/11/21.
 */

public class CopyPhotoTask extends AsyncTask<Void, Void, String> {

    public interface TaskCallBack {
        void onCopyCompiled(String fileName);
    }


    private ProgressDialog progressDialog;
    private CopyPhotoTask.TaskCallBack callBack;
    private Context mContext;
    private Uri uri;
    private int reqWidth, reqHeight;
    private FileManager fileManager;

    public CopyPhotoTask(Context context, Uri uri,
                         int reqWidth, int reqHeight,
                         FileManager fileManager, TaskCallBack callBack) {
        this.mContext = context;
        this.uri = uri;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
        this.fileManager = fileManager;
        this.callBack = callBack;
        this.progressDialog = new ProgressDialog(context);

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        String fileName = null;
        try {
            //1.Create bitmap
            //2.Get uri exif
            fileName = savePhotoToTemp(
                    ExifUtil.rotateBitmap(mContext, uri,
                            BitmapHelper.getBitmapFromReturnedImage(mContext, uri, reqWidth, reqHeight))
            );
        } catch (Exception e) {
            Log.e("CopyPhotoTask", e.toString());
        }
        return fileName;
    }

    @Override
    protected void onPostExecute(String fileName) {
        super.onPostExecute(fileName);
        progressDialog.dismiss();
        callBack.onCopyCompiled(fileName);
    }


    private String savePhotoToTemp(Bitmap bitmap) throws Exception {
        FileOutputStream out = null;
        String fileName = FileManager.createRandomFileName();
        try {
            out = new FileOutputStream(fileManager.getDiaryDir() + "/" + fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

}
