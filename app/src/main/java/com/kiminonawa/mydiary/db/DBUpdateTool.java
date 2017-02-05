package com.kiminonawa.mydiary.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.kiminonawa.mydiary.db.DBStructure.TopicEntry;

/**
 * Created by daxia on 2017/2/5.
 */

public class DBUpdateTool {


    //TODO add SQLiteException
    private SQLiteDatabase db;

    public DBUpdateTool(SQLiteDatabase db) {
        this.db = db;
    }

    /*
     * Version 6
     */

    public Cursor version_6_SelectMemo(long topicId) {
        Cursor c = db.query(DBStructure.MemoEntry.TABLE_NAME, null, DBStructure.MemoEntry.COLUMN_REF_TOPIC__ID + " = ?", new String[]{String.valueOf(topicId)},
                null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public long version_6_InsertMemoOrder(long topicId, long memoId, long order) {
        ContentValues values = new ContentValues();
        values.put(DBStructure.MemoOrderEntry.COLUMN_ORDER, order);
        values.put(DBStructure.MemoOrderEntry.COLUMN_REF_TOPIC__ID, topicId);
        values.put(DBStructure.MemoOrderEntry.COLUMN_REF_MEMO__ID, memoId);
        return db.insert(
                DBStructure.MemoOrderEntry.TABLE_NAME,
                null,
                values);
    }

    /**
     * Old selectTopic method 
     * @return
     */
    
    public Cursor version_6_SelectTopic() {
        Cursor c = db.query(TopicEntry.TABLE_NAME, null, null, null, null, null,
                TopicEntry._ID + " DESC");
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /*
     * Version 7
     */

    public long version_7_InsertTopicOrder(long topicId, long order) {
        ContentValues values = new ContentValues();
        values.put(DBStructure.TopicOrderEntry.COLUMN_ORDER, order);
        values.put(DBStructure.TopicOrderEntry.COLUMN_REF_TOPIC__ID, topicId);
        return db.insert(
                DBStructure.TopicOrderEntry.TABLE_NAME,
                null,
                values);
    }

    public Cursor version_7_SelectTopic() {
        Cursor c = db.query(TopicEntry.TABLE_NAME, null, null, null, null, null,
                TopicEntry._ID + " ASC");
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


}
