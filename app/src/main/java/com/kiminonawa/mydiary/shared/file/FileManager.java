package com.kiminonawa.mydiary.shared.file;

import android.content.Context;
import android.util.Log;

import com.kiminonawa.mydiary.main.topic.ITopic;

import org.apache.commons.io.FileUtils;

import java.io.File;

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
    public FileManager(Context context, @ITopic.TopicType int topicType, long topicId) {
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
        try {
            File[] fList = fileDir.listFiles();
            if (fList != null && fileDir.isDirectory()) {
                FileUtils.cleanDirectory(fileDir);
            }
        } catch (Exception e) {
            Log.e(TAG, "ClearDir file", e);
        }
    }






}
