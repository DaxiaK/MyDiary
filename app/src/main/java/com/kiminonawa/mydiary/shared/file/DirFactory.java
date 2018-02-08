package com.kiminonawa.mydiary.shared.file;

import android.content.Context;

import com.kiminonawa.mydiary.main.topic.ITopic;

/**
 * Created by daxia on 2018/2/8.
 */

public class DirFactory {

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
    private final static String TEMP_DIR_STR = "temp/";
    private final static String DIARY_ROOT_DIR_STR = "diary/";
    private final static String MEMO_ROOT_DIR_STR = "memo/";
    private final static String CONTACTS_ROOT_DIR_STR = "contacts/";
    private final static String EDIT_CACHE_DIARY_DIR_STR = "diary/editCache/";
    private final static String SETTING_DIR_STR = "setting/";
    private final static String BACKUP_DIR_STR = "backup/";

    public static IDir CreateDirByType(Context context, int dirType) {
        IDir dir = null;
        switch (dirType) {
            case IDir.ROOT_DIR:
                dir = new LocalDir(context.getExternalFilesDir(""));
                break;
            case IDir.TEMP_DIR:
                dir = new LocalDir(context.getExternalFilesDir(TEMP_DIR_STR));
                break;
            case IDir.DIARY_ROOT_DIR:
                dir = new LocalDir(context.getExternalFilesDir(DIARY_ROOT_DIR_STR));
                break;
            case IDir.MEMO_ROOT_DIR:
                dir = new LocalDir(context.getExternalFilesDir(MEMO_ROOT_DIR_STR));
                break;
            case IDir.CONTACTS_ROOT_DIR:
                dir = new LocalDir(context.getExternalFilesDir(CONTACTS_ROOT_DIR_STR));
                break;
            case IDir.DIARY_EDIT_CACHE_DIR:
                dir = new LocalDir(context.getExternalFilesDir(EDIT_CACHE_DIARY_DIR_STR));
                break;
            case IDir.SETTING_DIR:
                dir = new LocalDir(context.getExternalFilesDir(SETTING_DIR_STR));
                break;
            case IDir.BACKUP_DIR:
                dir = new LocalDir(context.getExternalFilesDir(BACKUP_DIR_STR));
                break;
            default:
                dir = null;
                break;
        }
        return dir;
    }

    /**
     * Create diary dir
     */
    public static IDir CreateDiaryDir(Context context, long topicId, long diaryId) {
        return new LocalDir(context.getExternalFilesDir(DIARY_ROOT_DIR_STR + "/" + topicId + "/" + diaryId + "/"));
    }

    /**
     * Create diary temp file manager for auto save
     * /sdcard/Android/data/com.kiminonawa.mydiary/files/diary/TOPIC_ID/temp
     */
    public static IDir CreateDiaryAutoSaveDir(Context context, long diaryTopicId) {
        return new LocalDir(context.getExternalFilesDir(DIARY_ROOT_DIR_STR + "/" + diaryTopicId + "/temp/"));
    }

    /**
     * Create topic dir file manager for delete
     */
    public static IDir CreateTopicDir(Context context, @ITopic.TopicType int topicType, long topicId) {
        IDir dir = null;
        switch (topicType) {
            case ITopic.TYPE_MEMO:
                dir = new LocalDir(context.getExternalFilesDir(MEMO_ROOT_DIR_STR + "/" + topicId + "/"));
                break;
            case ITopic.TYPE_CONTACTS:
                dir = new LocalDir(context.getExternalFilesDir(CONTACTS_ROOT_DIR_STR + "/" + topicId + "/"));
                break;
            case ITopic.TYPE_DIARY:
                dir = new LocalDir(context.getExternalFilesDir(DIARY_ROOT_DIR_STR + "/" + topicId + "/"));
                break;
            default:
                dir = null;
                break;
        }
        return dir;
    }


}
