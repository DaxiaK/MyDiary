package com.kiminonawa.mydiary.shared;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by daxia on 2016/11/18.
 */

public class FileManager {

    private static final String TAG = "FileManager";

    /**
     * The path is :
     * 1.diary temp
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/diary/temp
     * 2.diary saved
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/typeId/diaryId/
     */
    private File photoFileDir;
    private Context mContext;
    private final static String tempDiaryDirStr = "diary/temp/";

    /**
     * Create trem dir file manager
     *
     * @param context
     */
    public FileManager(Context context) {
        this.mContext = context;
        this.photoFileDir = mContext.getExternalFilesDir(tempDiaryDirStr);
    }

    /**
     * Create diary  dir file manager
     *
     * @return
     */
    public FileManager(Context context, long topicId, long diaryId) {
        this.mContext = context;
        this.photoFileDir = mContext.getExternalFilesDir("/" + topicId + "/" + diaryId + "/");
    }


    public File getTempDiaryDir() {
        return photoFileDir;
    }

    public File getDiaryDir() {
        return photoFileDir;
    }

    public void clearTempDiaryDir() {
        File tempDirFile = mContext.getExternalFilesDir(tempDiaryDirStr);
        if (tempDirFile.isDirectory()) {
            String[] children = tempDirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(tempDirFile, children[i]).delete();
            }
        }
    }

    public String getFileRootDir() {
        return mContext.getExternalFilesDir("").getPath();
    }


    public String getFileNameByUri(Context context, Uri uri) {
        String displayName = "";
        if (uri.getScheme().toString().startsWith("content")) {
            Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    cursor.close();
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        } else if (uri.getScheme().toString().startsWith("file")) {
            try {
                File file = new File(new URI(uri.toString()));
                if (file.exists()) {
                    displayName = file.getName();
                }
            } catch (URISyntaxException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            File file = new File(uri.getPath());
            if (file.exists()) {
                displayName = file.getName();
            }
        }
        return displayName;
    }

    public static void startBrowseImageFile(Fragment fragment, int requestCode) {
        try {
            Intent intentImage = new Intent();
            intentImage.setType("image/*");
            intentImage.setAction(Intent.ACTION_GET_CONTENT);
            fragment.startActivityForResult(Intent.createChooser(intentImage, "Select Picture"), requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    public static String createRandomFileName() {
        return UUID.randomUUID().toString();
    }

}
