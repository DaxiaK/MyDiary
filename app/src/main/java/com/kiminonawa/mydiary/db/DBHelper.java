package com.kiminonawa.mydiary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.kiminonawa.mydiary.db.DBStructure.DiaryEntry;
import static com.kiminonawa.mydiary.db.DBStructure.MemoEntry;
import static com.kiminonawa.mydiary.db.DBStructure.TopicEntry;
import static com.kiminonawa.mydiary.db.DBStructure.ContactsEntry;

/**
 * Created by daxia on 2016/4/2.
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * Version 3 by Daxia:
     * Add mydiary's contacts
     * --------------
     * Version 2 by Daxia:
     * Add location row.
     * Add memo table.
     * Add topic order.
     * --------------
     * Version 1 by Daxia：
     * First DB
     */
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "mydiary.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";
    private static final String FOREIGN = " FOREIGN KEY ";
    private static final String REFERENCES = " REFERENCES ";

    private static final String SQL_CREATE_TOPIC_ENTRIES =
            "CREATE TABLE " + TopicEntry.TABLE_NAME + " (" +
                    TopicEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    TopicEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    TopicEntry.COLUMN_TYPE + INTEGER_TYPE + COMMA_SEP +
                    TopicEntry.COLUMN_ORDER + INTEGER_TYPE +
                    " )";

    private static final String SQL_CREATE_DIARY_ENTRIES =
            "CREATE TABLE " + DiaryEntry.TABLE_NAME + " (" +
                    DiaryEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DiaryEntry.COLUMN_TIME + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    DiaryEntry.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                    DiaryEntry.COLUMN_MOOD + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry.COLUMN_WEATHER + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry.COLUMN_ATTACHMENT + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry.COLUMN_LOCATION + TEXT_TYPE + COMMA_SEP +
                    FOREIGN + " (" + DiaryEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + TopicEntry._ID + ")" +
                    " )";

    private static final String SQL_CREATE_MEMO_ENTRIES =
            "CREATE TABLE " + MemoEntry.TABLE_NAME + " (" +
                    MemoEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    MemoEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                    MemoEntry.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                    MemoEntry.COLUMN_CHECKED + INTEGER_TYPE + COMMA_SEP +
                    MemoEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                    FOREIGN + " (" + MemoEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + TopicEntry._ID + ")" +
                    " )";

    private static final String SQL_CREATE_CONTACTS_ENTRIES =
            "CREATE TABLE " + ContactsEntry.TABLE_NAME + " (" +
                    ContactsEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ContactsEntry.TABLE_NAME + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_NUMBER + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_PHOTO + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                    FOREIGN + " (" + MemoEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + TopicEntry._ID + ")" +
                    " )";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TOPIC_ENTRIES);
        db.execSQL(SQL_CREATE_DIARY_ENTRIES);
        db.execSQL(SQL_CREATE_MEMO_ENTRIES);
        db.execSQL(SQL_CREATE_CONTACTS_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.beginTransaction();
            if (oldVersion < 2) {
                oldVersion++;
                String addLocationSql = "ALTER TABLE  " + DiaryEntry.TABLE_NAME + " ADD COLUMN " + DiaryEntry.COLUMN_LOCATION + " " + TEXT_TYPE;
                String addTopicOrderSql = "ALTER TABLE  " + TopicEntry.TABLE_NAME + " ADD COLUMN " + TopicEntry.COLUMN_ORDER + " " + INTEGER_TYPE;
                db.execSQL(addLocationSql);
                db.execSQL(addTopicOrderSql);
                db.execSQL(SQL_CREATE_MEMO_ENTRIES);
            }
            if (oldVersion < 3) {
                db.execSQL(SQL_CREATE_MEMO_ENTRIES);
            }

            //Check update success
            db.setTransactionSuccessful();
            db.endTransaction();
        } else {
            onCreate(db);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
