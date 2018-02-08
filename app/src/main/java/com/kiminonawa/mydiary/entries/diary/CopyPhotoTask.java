package com.kiminonawa.mydiary.entries.diary;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.diary.item.DiaryTextTag;
import com.kiminonawa.mydiary.shared.file.IDir;
import com.kiminonawa.mydiary.shared.file.MyDiaryFileUtils;
import com.kiminonawa.mydiary.shared.photo.BitmapHelper;
import com.kiminonawa.mydiary.shared.photo.ExifUtil;

import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by daxia on 2016/11/21.
 */

public class CopyPhotoTask extends AsyncTask<Void, Void, String> {

    public interface CopyPhotoCallBack {
        void onCopyCompiled(String fileName, DiaryTextTag tag);
    }

    private Uri uri;
    private String srcFileName;
    private ProgressDialog progressDialog;
    private CopyPhotoTask.CopyPhotoCallBack callBack;
    private Context mContext;
    private int reqWidth, reqHeight;
    private IDir localDir;
    private boolean isAddPicture = false;
    private DiaryTextTag tag;

    /**
     * From select image
     */
    public CopyPhotoTask(Context context, Uri uri,
                         int reqWidth, int reqHeight,
                         IDir localDir, CopyPhotoCallBack callBack, DiaryTextTag tag) {
        this.uri = uri;
        this.isAddPicture = false;
        this.tag = tag;
        initTask(context, reqWidth, reqHeight, localDir, callBack);

    }


    /**
     * From take a picture
     */
    public CopyPhotoTask(Context context, String srcFileName,
                         int reqWidth, int reqHeight,
                         IDir localDir, CopyPhotoCallBack callBack, DiaryTextTag tag) {
        this.srcFileName = localDir.getDirAbsolutePath() + "/" + srcFileName;
        this.tag = tag;
        isAddPicture = true;
        initTask(context, reqWidth, reqHeight, localDir, callBack);
    }

    public void initTask(Context context,
                         int reqWidth, int reqHeight,
                         IDir localDir, CopyPhotoCallBack callBack) {
        this.mContext = context;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
        this.localDir = localDir;
        this.callBack = callBack;
        this.progressDialog = new ProgressDialog(context);

        progressDialog.setMessage(context.getString(R.string.process_dialog_loading));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        String returnFileName = null;
        try {
            //1.Create bitmap
            //2.Get uri exif
            if (isAddPicture) {
                returnFileName = savePhotoToTemp(
                        ExifUtil.rotateBitmap(srcFileName,
                                BitmapHelper.getBitmapFromTempFileSrc(srcFileName, reqWidth, reqHeight)));
            } else {
                //rotateBitmap && resize
                returnFileName = savePhotoToTemp(
                        ExifUtil.rotateBitmap(mContext, uri,
                                BitmapHelper.getBitmapFromReturnedImage(mContext, uri, reqWidth, reqHeight)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CopyPhotoTask", e.toString());
        }
        return returnFileName;
    }

    @Override
    protected void onPostExecute(String fileName) {
        super.onPostExecute(fileName);
        progressDialog.dismiss();
        callBack.onCopyCompiled(fileName,tag);
    }


    private String savePhotoToTemp(Bitmap bitmap) throws Exception {

        FileOutputStream out = null;
        String fileName = MyDiaryFileUtils.createRandomFileName();
        try {
            out = new FileOutputStream(localDir.getDirAbsolutePath() + "/" + fileName);
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
