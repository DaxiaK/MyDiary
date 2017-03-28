package com.kiminonawa.mydiary.init;

import android.content.Context;
import android.os.AsyncTask;

import com.kiminonawa.mydiary.BuildConfig;
import com.kiminonawa.mydiary.db.DBManager;
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
