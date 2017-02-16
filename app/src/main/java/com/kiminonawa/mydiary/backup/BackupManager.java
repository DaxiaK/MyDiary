package com.kiminonawa.mydiary.backup;

import com.kiminonawa.mydiary.BuildConfig;
import com.kiminonawa.mydiary.backup.obj.BUTopic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public class BackupManager {

    //For assert this json file is for mydiary backup
    //This value is "MyDiaryBackup" before encoding
    private String header = "79997e7ee0902e2010690e4f1951f81d";
    private int version_code;
    private long create_time;
    private List<BUTopic> backup_topic_list;

    public final static String BACKUP_JSON_FILE_NAME = "backup.json";


    public BackupManager() {
        version_code = BuildConfig.VERSION_CODE;
        create_time = System.currentTimeMillis();
        backup_topic_list = new ArrayList<>();
    }


    public void addTopic(BUTopic topic) {
        backup_topic_list.add(topic);
    }

    public BUTopic getTopic(int position) {
        return backup_topic_list.get(position);
    }

}

