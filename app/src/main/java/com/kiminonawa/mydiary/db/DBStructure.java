package com.kiminonawa.mydiary.db;

import android.provider.BaseColumns;

/**
 * Created by daxia on 2016/4/9.
 */
public class DBStructure {


    public static abstract class DiaryEntry implements BaseColumns {
        public static final String TABLE_NAME = "diary_entry";
        public static final String COLUMN_TIME = "diary_time";
        //TODO Fix the column name
        public static final String COLUMN_TITLE = "diary_count";
        public static final String COLUMN_RIGHT_TITLE = "diary_title";
        public static final String COLUMN_CONTENT = "diary_content";
        public static final String COLUMN_MOOD = "diary_mood";
        public static final String COLUMN_WEATHER = "diary_weather";
        public static final String COLUMN_ATTACHMENT = "diary_attachment";
        public static final String COLUMN_REF_TOPIC__ID = "diary_ref_topic_id";
        public static final String COLUMN_LOCATION = "diary_location";

    }

    public static abstract class TopicEntry implements BaseColumns {
        public static final String TABLE_NAME = "topic_entry";
        public static final String COLUMN_ORDER = "topic_order";
        public static final String COLUMN_NAME = "topic_name";
        public static final String COLUMN_TYPE = "topic_type";
    }

    public static abstract class MemoEntry implements BaseColumns {
        public static final String TABLE_NAME = "memo_entry";
        public static final String COLUMN_ORDER = "memo_order";
        public static final String COLUMN_CONTENT = "memo_content";
        public static final String COLUMN_CHECKED = "memo_checked";
        public static final String COLUMN_REF_TOPIC__ID = "memo_ref_topic_id";
    }
}
