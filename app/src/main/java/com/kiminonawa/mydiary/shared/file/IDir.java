package com.kiminonawa.mydiary.shared.file;

import java.io.File;

/**
 * Created by daxia on 2018/2/8.
 */

public interface IDir {

    int ROOT_DIR = 0;
    int TEMP_DIR = 1;
    int DIARY_EDIT_CACHE_DIR = 2;
    int DIARY_ROOT_DIR = 3;
    int MEMO_ROOT_DIR = 4;
    int CONTACTS_ROOT_DIR = 5;
    int SETTING_DIR = 6;
    int BACKUP_DIR = 7;

    File getDir();

    String getDirAbsolutePath();

    void clearDir();

}
