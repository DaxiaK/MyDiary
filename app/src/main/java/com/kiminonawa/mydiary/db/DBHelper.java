package com.kiminonawa.mydiary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import com.kiminonawa.mydiary.entries.diary.item.IDairyRow;

import static com.kiminonawa.mydiary.db.DBStructure.ContactsEntry;
import static com.kiminonawa.mydiary.db.DBStructure.DiaryEntry;
import static com.kiminonawa.mydiary.db.DBStructure.DiaryEntry_V2;
import static com.kiminonawa.mydiary.db.DBStructure.DiaryItemEntry_V2;
import static com.kiminonawa.mydiary.db.DBStructure.MemoEntry;
import static com.kiminonawa.mydiary.db.DBStructure.TopicEntry;

/**
 * Created by daxia on 2016/4/2.
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * Version 5 by Daxia
     * Add color message in topic
     * (Topic bg name is fixed in its dir)
     * --------------
     * Version 4 by Daxia:
     * design db DiaryEntry  -> DiaryEntry_v2
     * --------------
     * Version 3 by Daxia:
     * Add local contacts table
     * Add memo subtitle row.
     * --------------
     * Version 2 by Daxia:
     * Add location row.
     * Add memo table.
     * Add topic order.
     * --------------
     * Version 1 by Daxia：
     * First DB
     */
    public static final int DATABASE_VERSION = 5;
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
                    TopicEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                    TopicEntry.COLUMN_SUBTITLE + TEXT_TYPE + COMMA_SEP +
                    TopicEntry.COLUMN_COLOR + INTEGER_TYPE +
                    " )";

    /**
     * Discarded DIARY DB
     */
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

    private static final String SQL_CREATE_DIARY_ENTRIES_V2 =
            "CREATE TABLE " + DiaryEntry_V2.TABLE_NAME + " (" +
                    DiaryEntry_V2._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DiaryEntry_V2.COLUMN_TIME + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_MOOD + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_WEATHER + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_ATTACHMENT + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_LOCATION + TEXT_TYPE + COMMA_SEP +
                    FOREIGN + " (" + DiaryEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + TopicEntry._ID + ")" +
                    " )";

    private static final String SQL_CREATE_DIARY_ITEM_ENTRIES_V2 =
            "CREATE TABLE " + DiaryItemEntry_V2.TABLE_NAME + " (" +
                    DiaryItemEntry_V2._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DiaryItemEntry_V2.COLUMN_TYPE + INTEGER_TYPE + COMMA_SEP +
                    DiaryItemEntry_V2.COLUMN_POSITION + INTEGER_TYPE + COMMA_SEP +
                    DiaryItemEntry_V2.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                    DiaryItemEntry_V2.COLUMN_REF_DIARY__ID + INTEGER_TYPE + COMMA_SEP +
                    FOREIGN + " (" + DiaryItemEntry_V2.COLUMN_REF_DIARY__ID + ")" + REFERENCES + DiaryEntry_V2.TABLE_NAME + "(" + DiaryEntry_V2._ID + ")" +
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
                    ContactsEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_PHONENUMBER + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_PHOTO + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                    FOREIGN + " (" + ContactsEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + TopicEntry._ID + ")" +
                    " )";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TOPIC_ENTRIES);

        //Diary V2 work from db version 4
        db.execSQL(SQL_CREATE_DIARY_ENTRIES_V2);
        db.execSQL(SQL_CREATE_DIARY_ITEM_ENTRIES_V2);

        db.execSQL(SQL_CREATE_MEMO_ENTRIES);
        db.execSQL(SQL_CREATE_CONTACTS_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            try {
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
                    //SubTitle for topic only
                    String addTopicSubtitleSql = "ALTER TABLE  " + TopicEntry.TABLE_NAME + " ADD COLUMN " + TopicEntry.COLUMN_SUBTITLE + " " + TEXT_TYPE;
                    db.execSQL(addTopicSubtitleSql);
                    db.execSQL(SQL_CREATE_CONTACTS_ENTRIES);
                }
                if (oldVersion < 4) {
                    //Create  diary V2 db
                    db.execSQL(SQL_CREATE_DIARY_ENTRIES_V2);
                    db.execSQL(SQL_CREATE_DIARY_ITEM_ENTRIES_V2);
                    //Move the old diaryContent to DiaryItemEntry_V2
                    version4MoveData(db);
                    //Delete  diary v1 db
                    String deleteV1DiaryTable = "DROP TABLE IF EXISTS " + DiaryEntry.TABLE_NAME;
                    db.execSQL(deleteV1DiaryTable);
                }
                if (oldVersion < 5) {
                    //Add textcolor COLUMN
                    String addTopicTextColorSql = "ALTER TABLE  " + TopicEntry.TABLE_NAME + " ADD COLUMN " + TopicEntry.COLUMN_COLOR + " " + INTEGER_TYPE;
                    db.execSQL(addTopicTextColorSql);
                    //set textcolor default black color
                    version5AddTextColor(db);
                }
                //Check update success
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } else {
            onCreate(db);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    private void version5AddTextColor(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(TopicEntry.COLUMN_COLOR, Color.BLACK);
        db.update(TopicEntry.TABLE_NAME, values, null, null);
    }


    private void version4MoveData(SQLiteDatabase db) {
        DBManager dbManager = new DBManager(db);
        //Copy old diary into new diary_v2
        String copyOldDiaryToV2 = "INSERT INTO " + DiaryEntry_V2.TABLE_NAME + " (" +
                DiaryEntry_V2._ID + COMMA_SEP +
                DiaryEntry_V2.COLUMN_TIME + COMMA_SEP +
                DiaryEntry_V2.COLUMN_TITLE + COMMA_SEP +
                DiaryEntry_V2.COLUMN_MOOD + COMMA_SEP +
                DiaryEntry_V2.COLUMN_WEATHER + COMMA_SEP +
                DiaryEntry_V2.COLUMN_ATTACHMENT + COMMA_SEP +
                DiaryEntry_V2.COLUMN_REF_TOPIC__ID + COMMA_SEP +
                DiaryEntry_V2.COLUMN_LOCATION + ")" +
                " SELECT " +
                DiaryEntry_V2._ID + COMMA_SEP +
                DiaryEntry.COLUMN_TIME + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_MOOD + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_WEATHER + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_ATTACHMENT + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_LOCATION + TEXT_TYPE +
                " FROM " + DiaryEntry.TABLE_NAME;

        db.execSQL(copyOldDiaryToV2);


        //Old content add into diaryitem_v2
        Cursor oldDiaryCursor = dbManager.selectAllV1Diary();
        for (int i = 0; i < oldDiaryCursor.getCount(); i++) {
            //Old version , it is only diaryText , and only 1 row
            dbManager.insertDiaryContent(IDairyRow.TYPE_TEXT, 0,
                    oldDiaryCursor.getString(3), oldDiaryCursor.getLong(0));
            oldDiaryCursor.moveToNext();
        }

    }
}
