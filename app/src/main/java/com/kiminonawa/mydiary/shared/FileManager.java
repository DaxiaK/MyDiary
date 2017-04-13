package com.kiminonawa.mydiary.shared;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.kiminonawa.mydiary.main.topic.ITopic;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by daxia on 2016/11/18.
 */

//
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永無Bug

public class FileManager {

    private static final String TAG = "FileManager";
    //Min free space is 50 MB
    public static final int MIN_FREE_SPACE = 50;
    public static final String FILE_HEADER = "file://";

    public final static int ROOT_DIR = 0;
    public final static int TEMP_DIR = 1;
    public final static int DIARY_EDIT_CACHE_DIR = 2;
    public final static int DIARY_ROOT_DIR = 3;
    public final static int MEMO_ROOT_DIR = 4;
    public final static int CONTACTS_ROOT_DIR = 5;
    public final static int SETTING_DIR = 6;
    public final static int BACKUP_DIR = 7;

    /**
     * The path is :
     * 1.setting , topic bg & profile photo  temp
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/temp
     * 2.diary edit temp
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/diary/editCache
     * 3.diary saved
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/diary/TOPIC_ID/DIARY_ID/
     * 4.memo path
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/memo/TOPIC_ID/
     * 5.contacts path
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/contacts/TOPIC_ID/
     * 6.Setting path
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/setting/
     * 7.Backup temp path
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/backup/
     */
    private File fileDir;
    private Context mContext;
    private final static String TEMP_DIR_STR = "temp/";
    private final static String DIARY_ROOT_DIR_STR = "diary/";
    private final static String MEMO_ROOT_DIR_STR = "memo/";
    private final static String CONTACTS_ROOT_DIR_STR = "contacts/";
    private final static String EDIT_CACHE_DIARY_DIR_STR = "diary/editCache/";
    private final static String SETTING_DIR_STR = "setting/";
    private final static String BACKUP_DIR_STR = "backup/";

    /**
     * Create trem dir file manager
     *
     * @param context
     */
    public FileManager(Context context, int dir) {
        this.mContext = context;
        switch (dir) {
            case ROOT_DIR:
                this.fileDir = mContext.getExternalFilesDir("");
                break;
            case TEMP_DIR:
                this.fileDir = mContext.getExternalFilesDir(TEMP_DIR_STR);
                break;
            case DIARY_ROOT_DIR:
                this.fileDir = mContext.getExternalFilesDir(DIARY_ROOT_DIR_STR);
                break;
            case MEMO_ROOT_DIR:
                this.fileDir = mContext.getExternalFilesDir(MEMO_ROOT_DIR_STR);
                break;
            case CONTACTS_ROOT_DIR:
                this.fileDir = mContext.getExternalFilesDir(CONTACTS_ROOT_DIR_STR);
                break;
            case DIARY_EDIT_CACHE_DIR:
                this.fileDir = mContext.getExternalFilesDir(EDIT_CACHE_DIARY_DIR_STR);
                break;
            case SETTING_DIR:
                this.fileDir = mContext.getExternalFilesDir(SETTING_DIR_STR);
                break;
            case BACKUP_DIR:
                this.fileDir = mContext.getExternalFilesDir(BACKUP_DIR_STR);
                break;
        }
    }

    /**
     * Create diary  dir file manager
     */
    public FileManager(Context context, long topicId, long diaryId) {
        this.mContext = context;
        this.fileDir = mContext.getExternalFilesDir(DIARY_ROOT_DIR_STR + "/" + topicId + "/" + diaryId + "/");
    }

    /**
     * Create diary temp file manager for auto save
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/diary/TOPIC_ID/temp
     */
    public FileManager(Context context, long diaryTopicId) {
        this.mContext = context;
        this.fileDir = mContext.getExternalFilesDir(DIARY_ROOT_DIR_STR + "/" + diaryTopicId + "/temp/");
    }

    /**
     * Create topic dir file manager for delete
     */
    public FileManager(Context context, int topicType, long topicId) {
        this.mContext = context;
        switch (topicType) {
            case ITopic.TYPE_MEMO:
                this.fileDir = mContext.getExternalFilesDir(MEMO_ROOT_DIR_STR + "/" + topicId + "/");

                break;
            case ITopic.TYPE_CONTACTS:
                this.fileDir = mContext.getExternalFilesDir(CONTACTS_ROOT_DIR_STR + "/" + topicId + "/");

                break;
            case ITopic.TYPE_DIARY:
                this.fileDir = mContext.getExternalFilesDir(DIARY_ROOT_DIR_STR + "/" + topicId + "/");

                break;
        }
    }


    public File getDir() {
        return fileDir;
    }

    public String getDirAbsolutePath() {
        return fileDir.getAbsolutePath();
    }

    public void clearDir() {
        File[] fList = fileDir.listFiles();
        if (fList != null && fileDir.isDirectory()) {
            try {
                FileUtils.cleanDirectory(fileDir);
            } catch (IOException e) {
                Log.e(TAG, "ClearDir file", e);
            }
        }
    }

    public static String getFileNameByUri(Context context, Uri uri) {
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
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
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
                e.printStackTrace();
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

    public static void startBrowseImageFile(Activity activity, int requestCode) {
        try {
            Intent intentImage = new Intent();
            intentImage.setType("image/*");
            intentImage.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(Intent.createChooser(intentImage, "Select Picture"), requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    public static String createRandomFileName() {
        return UUID.randomUUID().toString();
    }


    public static boolean isImage(String fileName) {
        return fileName.toLowerCase().endsWith(".jpeg") || fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png");
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }


    /**
     * Gets the real path from file
     *
     * @param context
     * @param uri
     * @return path
     * REF:http://stackoverflow.com/questions/26195731/android-get-real-path-from-uri
     */
    @TargetApi(19)
    public static String getRealPathFromURI(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * @return MB
     */
    public static long getSDCardFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize, freeBlocks;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = sf.getBlockSize();
            freeBlocks = sf.getAvailableBlocks();
        } else {
            blockSize = sf.getBlockSizeLong();
            freeBlocks = sf.getAvailableBlocksLong();
        }
        return (freeBlocks * blockSize) / 1024 / 1024;
    }


}
