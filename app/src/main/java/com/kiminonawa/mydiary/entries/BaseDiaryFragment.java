package com.kiminonawa.mydiary.entries;


import android.support.v4.app.Fragment;

import com.kiminonawa.mydiary.entries.entries.EntriesEntity;

import java.util.List;


public class BaseDiaryFragment extends Fragment {


    public long getTopicId() {
        return ((DiaryActivity) getActivity()).getTopicId();
    }

    public void setIsCreating(boolean isCreating) {
        ((DiaryActivity) getActivity()).setCreating(isCreating);
    }

    public List<EntriesEntity> getEntriesList() {
        return ((DiaryActivity) getActivity()).getEntriesList();
    }

    public void updateEntriesList() {
        ((DiaryActivity) getActivity()).loadEntries();
    }
}

