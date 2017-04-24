package com.kiminonawa.mydiary.init;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.kiminonawa.mydiary.BuildConfig;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.diary.DiaryInfoHelper;
import com.kiminonawa.mydiary.entries.diary.item.IDairyRow;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.shared.OldVersionHelper;
import com.kiminonawa.mydiary.shared.SPFManager;

/**
 * Version History
 * 20170104
 * Add order Memo in version 25
 * ----
 * 20161203 The photo dir of diary should add one dir , So I modify it in version 17
 * ----
 * 20161120
 * Implement diaryDB v2 , update sample data
 * ----
 * 20161109
 * Add contacts function in version 10
 * ----
 * 20161108
 * Add memo function & show memo sample data in versionCode 6
 * ----
 */
public class InitTask extends AsyncTask<Long, Void, Boolean> {

    public interface InitCallBack {
        void onInitCompiled(boolean showReleaseNote);
    }

    private InitCallBack callBack;
    private Context mContext;
    boolean showReleaseNote;


    public InitTask(Context context, InitCallBack callBack) {
        this.mContext = context;
        this.callBack = callBack;
        this.showReleaseNote = SPFManager.getReleaseNoteClose(mContext);
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        try {
            DBManager dbManager = new DBManager(mContext);
            dbManager.opeDB();
            loadSampleData(dbManager);
            updateData(dbManager);
            dbManager.closeDB();
            saveCurrentVersionCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showReleaseNote;
    }

    @Override
    protected void onPostExecute(Boolean showReleaseNote) {
        super.onPostExecute(showReleaseNote);
        callBack.onInitCompiled(showReleaseNote);
    }

    private void loadSampleData(DBManager dbManager) throws Exception {

        //Because memo function is run in version 6 ,
        //So , if version < 6 , show the sample memo data
        if (SPFManager.getVersionCode(mContext) < 6) {
            //Insert sample topic
            long mitsuhaMemoId = dbManager.insertTopic("ゼッタイ禁止", ITopic.TYPE_MEMO, Color.BLACK);
            long takiMemoId = dbManager.insertTopic("禁止事項 Ver.5", ITopic.TYPE_MEMO, Color.BLACK);

            dbManager.insertTopicOrder(mitsuhaMemoId, 0);
            dbManager.insertTopicOrder(takiMemoId, 1);
            //Insert sample memo
            if (mitsuhaMemoId != -1) {
                dbManager.insertMemoOrder(mitsuhaMemoId,
                        dbManager.insertMemo("女子にも触るな！", false, mitsuhaMemoId)
                        , 0);
                dbManager.insertMemoOrder(mitsuhaMemoId,
                        dbManager.insertMemo("男子に触るな！", false, mitsuhaMemoId)
                        , 1);
                dbManager.insertMemoOrder(mitsuhaMemoId,
                        dbManager.insertMemo("脚をひらくな！", true, mitsuhaMemoId)
                        , 2);
                dbManager.insertMemoOrder(mitsuhaMemoId,
                        dbManager.insertMemo("体は見ない！/触らない！！", false, mitsuhaMemoId)
                        , 3);
                dbManager.insertMemoOrder(mitsuhaMemoId,
                        dbManager.insertMemo("お風呂ぜっっったい禁止！！！！！！！", true, mitsuhaMemoId)
                        , 4);
            }
            if (takiMemoId != -1) {
                dbManager.insertMemoOrder(takiMemoId,
                        dbManager.insertMemo("司とベタベタするな.....", true, takiMemoId)
                        , 0);
                dbManager.insertMemoOrder(takiMemoId,
                        dbManager.insertMemo("奧寺先輩と馴れ馴れしくするな.....", true, takiMemoId)
                        , 1);
                dbManager.insertMemoOrder(takiMemoId,
                        dbManager.insertMemo("女言葉NG！", false, takiMemoId)
                        , 2);
                dbManager.insertMemoOrder(takiMemoId,
                        dbManager.insertMemo("遅刻するな！", true, takiMemoId)
                        , 3);
                dbManager.insertMemoOrder(takiMemoId,
                        dbManager.insertMemo("訛り禁止！", false, takiMemoId)
                        , 4);
                dbManager.insertMemoOrder(takiMemoId,
                        dbManager.insertMemo("無駄つかい禁止！", true, takiMemoId)
                        , 5);
            }
        }

        if (SPFManager.getVersionCode(mContext) < 10) {
            //Insert sample topic
            long topicOnDiarySampleId = dbManager.insertTopic("DIARY", ITopic.TYPE_DIARY, Color.BLACK);
            dbManager.insertTopicOrder(topicOnDiarySampleId, 2);
            if (topicOnDiarySampleId != -1) {
                //Insert sample diary
                long diarySampleId = dbManager.insertDiaryInfo(1475665800000L, "東京生活3❤",
                        DiaryInfoHelper.MOOD_HAPPY, DiaryInfoHelper.WEATHER_RAINY, true, topicOnDiarySampleId, "Tokyo");
                dbManager.insertDiaryContent(IDairyRow.TYPE_TEXT, 0, "There are many coffee shops in Tokyo!", diarySampleId);
            }
        }

        //Contacts function work in version 10
        if (SPFManager.getVersionCode(mContext) < 10) {
            //Insert sample contacts
            long sampleContactsId = dbManager.insertTopic("緊急時以外かけちゃダメ！", ITopic.TYPE_CONTACTS, Color.BLACK);
            dbManager.insertTopicOrder(sampleContactsId, 3);
            //Insert sample contacts
            if (sampleContactsId != -1) {
                dbManager.insertContacts(mContext.getString(R.string.profile_username_mitsuha), "090000000", "", sampleContactsId);
            }
        }

    }

    private void updateData(DBManager dbManager) throws Exception {
        //Photo path modify in version 17
        if (SPFManager.getVersionCode(mContext) < 17) {
            OldVersionHelper.Version17MoveTheDiaryIntoNewDir(mContext);
        }

    }

    private void saveCurrentVersionCode() {
        //Save currentVersion
        if (SPFManager.getVersionCode(mContext) < BuildConfig.VERSION_CODE) {
            SPFManager.setReleaseNoteClose(mContext, false);
            showReleaseNote = true;
            SPFManager.setVersionCode(mContext);
        }
    }
}
