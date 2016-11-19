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
    private File tempDiaryDir;
    private Context mContext;
    private final static String tempDiaryDirStr = "diary/temp/";
    private String topicId;

    public FileManager(Context context, long topicId) {
        this.mContext = context;
        this.tempDiaryDir = mContext.getExternalFilesDir(tempDiaryDirStr);
        this.topicId = String.valueOf(topicId);
    }

    public File getTempDiaryDir() {
        return tempDiaryDir;
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

}
