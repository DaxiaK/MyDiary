package com.kiminonawa.mydiary.entries.entries;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.file.DirFactory;
import com.kiminonawa.mydiary.shared.file.IDir;
import com.kiminonawa.mydiary.shared.file.MyDiaryFileUtils;

import java.io.File;

/**
 * Created by daxia on 2016/11/21.
 */

public class CopyDiaryToEditCacheTask extends AsyncTask<Long, Void, Integer> {

    public interface EditTaskCallBack {
        void onCopyToEditCacheCompiled(int result);
    }

    private EditTaskCallBack callBack;
    private IDir editCacheFileManage;
    private Context mContext;

    public final static int RESULT_COPY_SUCCESSFUL = 1;
    public final static int RESULT_COPY_ERROR = 2;


    public CopyDiaryToEditCacheTask(Context context, IDir editCacheFileManage,
                                    EditTaskCallBack callBack) {
        this.mContext = context;
        this.editCacheFileManage = editCacheFileManage;
        this.callBack = callBack;
    }

    @Override
    protected Integer doInBackground(Long... params) {

        int copyResult = RESULT_COPY_SUCCESSFUL;
        long topicId = params[0];
        long diaryId = params[1];
        try {
            IDir diaryLocalDir = DirFactory.CreateDiaryDir(mContext, topicId, diaryId);
            File[] childrenPhoto = diaryLocalDir.getDir().listFiles();
            for (int i = 0; i < diaryLocalDir.getDir().listFiles().length; i++) {
                copyPhoto(childrenPhoto[i].getName(), diaryLocalDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
            copyResult = RESULT_COPY_ERROR;
        }
        return copyResult;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (result == RESULT_COPY_ERROR) {
            Toast.makeText(mContext, mContext.getString(R.string.toast_diary_copy_to_edit_fail), Toast.LENGTH_LONG).show();
        }
        callBack.onCopyToEditCacheCompiled(result);
    }

    private void copyPhoto(String filename, IDir diaryLocalDir) throws Exception {
        MyDiaryFileUtils.copy(new File(diaryLocalDir.getDirAbsolutePath() + "/" + filename),
                new File(editCacheFileManage.getDirAbsolutePath() + "/" + filename));
    }
}
