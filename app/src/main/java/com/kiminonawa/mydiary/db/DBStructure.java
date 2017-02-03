package com.kiminonawa.mydiary.db;

import android.provider.BaseColumns;

/**
 * Created by daxia on 2016/4/9.
 */
public class DBStructure {


    public static abstract class DiaryEntry implements BaseColumns {
        public static final String TABLE_NAME = "diary_entry";
        public static final String COLUMN_TIME = "diary_time";
        //Fix  diary_count -> diary_title in V2
        public static final String COLUMN_TITLE = "diary_count";
        public static final String COLUMN_CONTENT = "diary_content";
        public static final String COLUMN_MOOD = "diary_mood";
        public static final String COLUMN_WEATHER = "diary_weather";
        public static final String COLUMN_ATTACHMENT = "diary_attachment";
        public static final String COLUMN_REF_TOPIC__ID = "diary_ref_topic_id";
        public static final String COLUMN_LOCATION = "diary_location";
    }

    public static abstract class DiaryEntry_V2 implements BaseColumns {
        public static final String TABLE_NAME = "diary_entry_v2";
        public static final String COLUMN_TIME = "diary_time";
        public static final String COLUMN_TITLE = "diary_title";
        public static final String COLUMN_MOOD = "diary_mood";
        public static final String COLUMN_WEATHER = "diary_weather";
        public static final String COLUMN_ATTACHMENT = "diary_attachment";
        public static final String COLUMN_REF_TOPIC__ID = "diary_ref_topic_id";
        public static final String COLUMN_LOCATION = "diary_location";
    }

    /**
     * Type see @IDairyRow
     */
    public static abstract class DiaryItemEntry_V2 implements BaseColumns {
        public static final String TABLE_NAME = "diary_item_entry_v2";
        public static final String COLUMN_TYPE = "diary_item_type";
        public static final String COLUMN_POSITION = "diary_item_position";
        public static final String COLUMN_CONTENT = "diary_item_content";
        public static final String COLUMN_REF_DIARY__ID = "item_ref_diary_id";
    }


    public static abstract class TopicEntry implements BaseColumns {
        public static final String TABLE_NAME = "topic_entry";
        public static final String COLUMN_ORDER = "topic_order";
        public static final String COLUMN_NAME = "topic_name";
        public static final String COLUMN_TYPE = "topic_type";
        public static final String COLUMN_SUBTITLE = "topic_subtitle";
        public static final String COLUMN_COLOR = "topic_color";
    }


    public static abstract class TopicOrderEntry implements BaseColumns {
        public static final String TABLE_NAME = "topic_order";
        public static final String COLUMN_ORDER = "topic_order_order_in_parent";
        public static final String COLUMN_REF_TOPIC__ID = "topic_order_ref_topic_id";
    }

    public static abstract class MemoEntry implements BaseColumns {
        public static final String TABLE_NAME = "memo_entry";
        public static final String COLUMN_ORDER = "memo_order";
        public static final String COLUMN_CONTENT = "memo_content";
        public static final String COLUMN_CHECKED = "memo_checked";
        public static final String COLUMN_REF_TOPIC__ID = "memo_ref_topic_id";
    }

    public static abstract class MemoOrderEntry implements BaseColumns {
        public static final String TABLE_NAME = "memo_order";
        public static final String COLUMN_ORDER = "memo_order_order_in_parent";
        public static final String COLUMN_REF_TOPIC__ID = "memo_order_ref_topic_id";
        public static final String COLUMN_REF_MEMO__ID = "memo_order_ref_memo_id";
    }


    public static abstract class ContactsEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts_entry";
        public static final String COLUMN_NAME = "contacts_name";
        public static final String COLUMN_PHONENUMBER = "contacts_phone_number";
        public static final String COLUMN_PHOTO = "contacts_photo";
        public static final String COLUMN_REF_TOPIC__ID = "contacts_ref_topic_id";

    }
}
