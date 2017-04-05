package com.kiminonawa.mydiary.backup;

import com.kiminonawa.mydiary.BuildConfig;
import com.kiminonawa.mydiary.backup.obj.BUContactsEntries;
import com.kiminonawa.mydiary.backup.obj.BUDiaryEntries;
import com.kiminonawa.mydiary.backup.obj.BUMemoEntries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daxia on 2017/2/17.
 */

public class BackupManager {

    /**
     * backup_topic_list : []
     * header : 79997e7ee0902e2010690e4f1951f81d
     * create_time : 1487944303719
     * version_code : 29
     */

    public final static String BACKUP_JSON_FILE_NAME = "backup.json";
    public final static String BACKUP_ZIP_FILE_HEADER = "MyDiaryBackup_";
    public final static String BACKUP_ZIP_FILE_SUB_FILE_NAME = ".zip";

    public final static String header = "79997e7ee0902e2010690e4f1951f81d";
    private long create_time;
    private int version_code;
    private List<BackupTopicListBean> backup_topic_list;

    public BackupManager() {
    }

    public void initBackupManagerExportInfo() {
        backup_topic_list = new ArrayList<>();
        version_code = BuildConfig.VERSION_CODE;
        create_time = System.currentTimeMillis();
    }

    public void addTopic(BackupTopicListBean topic) {
        backup_topic_list.add(topic);
    }

    public String getHeader() {
        return header;
    }


    public long getCreate_time() {
        return create_time;
    }

    public int getVersion_code() {
        return version_code;
    }


    public List<BackupTopicListBean> getBackup_topic_list() {
        return backup_topic_list;
    }

    public void setBackup_topic_list(List<BackupTopicListBean> backup_topic_list) {
        this.backup_topic_list = backup_topic_list;
    }

    public static class BackupTopicListBean {
        /**
         * contacts_topic_entries_list : []
         * topic_title : 緊急時以外かけちゃダメ！
         * topic_type : 1
         * topic_id : 4
         * topic_color : -16777216
         * topic_order : 3
         * diary_topic_entries_list : [{"diary_item_list":[{"diary_item_content":"85dfc217-d0aa-47fd-8528-2c85eec21686","diary_item_position":0,"diary_item_type":1},{"diary_item_content":"","diary_item_position":1,"diary_item_type":0}],"diary_entries_location":"","diary_entries_id":6,"diary_entries_time":1487691454693,"diary_entries_mood":0,"diary_entries_weather":0,"diary_entries_attachment":true},{"diary_item_list":[{"diary_item_content":"98819be5-dd6d-477c-9487-31daa878d5f9","diary_item_position":0,"diary_item_type":1},{"diary_item_content":"","diary_item_position":1,"diary_item_type":0}],"diary_entries_location":"","diary_entries_id":5,"diary_entries_time":1487688752707,"diary_entries_mood":0,"diary_entries_weather":0,"diary_entries_attachment":true},{"diary_item_list":[{"diary_item_content":"58fb4af4-a78a-4ef1-9a24-27cb6d126cc1","diary_item_position":0,"diary_item_type":1},{"diary_item_content":"","diary_item_position":1,"diary_item_type":0},{"diary_item_content":"b5c2eeaa-7519-40d9-8c5c-4e6aed7796c2","diary_item_position":2,"diary_item_type":1},{"diary_item_content":"","diary_item_position":3,"diary_item_type":0},{"diary_item_content":"17f31dc7-274b-4a8b-9611-6a4b71e05cac","diary_item_position":4,"diary_item_type":1},{"diary_item_content":"","diary_item_position":5,"diary_item_type":0},{"diary_item_content":"8b8c1c61-db90-4e1f-b9a1-6c183bbc3c29","diary_item_position":6,"diary_item_type":1},{"diary_item_content":"","diary_item_position":7,"diary_item_type":0},{"diary_item_content":"6b15b9e6-bc1d-463f-8f7c-24fe649b57a1","diary_item_position":8,"diary_item_type":1},{"diary_item_content":"","diary_item_position":9,"diary_item_type":0},{"diary_item_content":"18ea77ea-5abb-48c3-97a8-dd6ed2f0d61b","diary_item_position":10,"diary_item_type":1},{"diary_item_content":"","diary_item_position":11,"diary_item_type":0},{"diary_item_content":"005bf34c-eaf9-4307-8514-0ee9960ae128","diary_item_position":12,"diary_item_type":1},{"diary_item_content":"","diary_item_position":13,"diary_item_type":0}],"diary_entries_location":"","diary_entries_id":4,"diary_entries_time":1487687347039,"diary_entries_mood":0,"diary_entries_weather":0,"diary_entries_attachment":true},{"diary_item_list":[{"diary_item_content":"fd38fc7e-a310-498c-85d8-5b2e83ac9fcd","diary_item_position":0,"diary_item_type":1},{"diary_item_content":"","diary_item_position":1,"diary_item_type":0}],"diary_entries_location":"","diary_entries_id":3,"diary_entries_time":1487687062783,"diary_entries_mood":0,"diary_entries_weather":0,"diary_entries_attachment":true},{"diary_item_list":[{"diary_item_content":"635a8c1e-25e5-4b66-b8a8-1c2a7278128f","diary_item_position":0,"diary_item_type":1},{"diary_item_content":"","diary_item_position":1,"diary_item_type":0}],"diary_entries_location":"","diary_entries_id":2,"diary_entries_time":1487683670471,"diary_entries_mood":0,"diary_entries_weather":0,"diary_entries_attachment":true},{"diary_item_list":[{"diary_item_content":"There are many coffee shop in Tokyo!","diary_item_position":0,"diary_item_type":0}],"diary_entries_location":"Tokyo","diary_entries_id":1,"diary_entries_time":1475665800000,"diary_entries_mood":0,"diary_entries_weather":3,"diary_entries_attachment":true}]
         * memo_topic_entries_list : [{"memo_entries_content":"無駄つかい禁止！","checked":true,"memo_entries_order":5},{"memo_entries_content":"訛り禁止！","checked":false,"memo_entries_order":4},{"memo_entries_content":"遅刻するな！","checked":true,"memo_entries_order":3},{"memo_entries_content":"女言葉NG！","checked":false,"memo_entries_order":2},{"memo_entries_content":"奧寺先輩と馴れ馴れしくするな.....","checked":true,"memo_entries_order":1},{"memo_entries_content":"司とベタベタするな.....","checked":true,"memo_entries_order":0}]
         */

        private String topic_title;
        private int topic_type;
        private long topic_id;
        private int topic_color;
        private int topic_order;
        private List<BUContactsEntries> contacts_topic_entries_list;
        private List<BUDiaryEntries> diary_topic_entries_list;
        private List<BUMemoEntries> memo_topic_entries_list;

        public BackupTopicListBean(long topic_id, String topic_title, int topic_order,
                                   int topic_color) {
            this.topic_id = topic_id;
            this.topic_title = topic_title;
            this.topic_color = topic_color;
            this.topic_order = topic_order;
        }

        public List<BUContactsEntries> getContacts_topic_entries_list() {
            return contacts_topic_entries_list;
        }

        public void setContacts_topic_entries_list(List<BUContactsEntries> contacts_topic_entries_list) {
            this.contacts_topic_entries_list = contacts_topic_entries_list;
        }

        public List<BUDiaryEntries> getDiary_topic_entries_list() {
            return diary_topic_entries_list;
        }

        public void setDiary_topic_entries_list(List<BUDiaryEntries> diary_topic_entries_list) {
            this.diary_topic_entries_list = diary_topic_entries_list;
        }

        public List<BUMemoEntries> getMemo_topic_entries_list() {
            return memo_topic_entries_list;
        }

        public void setMemo_topic_entries_list(List<BUMemoEntries> memo_topic_entries_list) {
            this.memo_topic_entries_list = memo_topic_entries_list;
        }

        public String getTopic_title() {
            return topic_title;
        }

        public void setTopic_title(String topic_title) {
            this.topic_title = topic_title;
        }

        public int getTopic_type() {
            return topic_type;
        }

        public void setTopic_type(int topic_type) {
            this.topic_type = topic_type;
        }

        public long getTopic_id() {
            return topic_id;
        }

        public void setTopic_id(int topic_id) {
            this.topic_id = topic_id;
        }

        public int getTopic_color() {
            return topic_color;
        }

        public void setTopic_color(int topic_color) {
            this.topic_color = topic_color;
        }

        public int getTopic_order() {
            return topic_order;
        }

        public void setTopic_order(int topic_order) {
            this.topic_order = topic_order;
        }

    }

}
