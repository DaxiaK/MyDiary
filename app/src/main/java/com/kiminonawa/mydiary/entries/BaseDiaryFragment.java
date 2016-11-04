package com.kiminonawa.mydiary.entries;


import android.content.Context;
import android.support.v4.app.Fragment;


public class BaseDiaryFragment extends Fragment {


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DiaryActivity activity;
        if (context instanceof DiaryActivity) {
            activity = (DiaryActivity) context;
            activity.popTopBar();
        }
    }

    public long getTopicId() {
        return ((DiaryActivity) getActivity()).getTopicId();
    }

}

