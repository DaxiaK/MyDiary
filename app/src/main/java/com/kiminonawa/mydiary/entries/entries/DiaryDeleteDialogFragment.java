package com.kiminonawa.mydiary.entries.entries;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.gui.DeleteDialogFragment;

/**
 * Created by daxia on 2016/11/14.
 */

public class DiaryDeleteDialogFragment extends DeleteDialogFragment {


    private long diaryId;

    public static DiaryDeleteDialogFragment newInstance(long diaryId) {
        Bundle args = new Bundle();
        DiaryDeleteDialogFragment fragment = new DiaryDeleteDialogFragment();
        args.putLong("diaryId", diaryId);
        fragment.setArguments(args);
        return fragment;
    }


    private void deleteDiary() {
//        DBManager dbManager = new DBManager(getActivity());
//        dbManager.opeDB();
//        dbManager.delDiary(diaryId);
//        dbManager.closeDB();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        diaryId = getArguments().getLong("diaryId", -1L);
        this.TV_delete_content.setText(getString(R.string.entries_edit_dialog_delete_content));
    }

    @Override
    protected void okButtonEvent() {
        if (diaryId != -1) {
            deleteDiary();
            this.callback.delete();
        }
        dismiss();
    }

    @Override
    protected void cancelButtonEvent() {
        dismiss();
    }
}
