package com.kiminonawa.mydiary.entries.entries;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.BaseDiaryFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EntriesFragment extends BaseDiaryFragment implements DiaryViewerDialogFragment.DiaryViewerCallback {

    /**
     * UI
     */
    private TextView TV_entries_count;
    private final static int MAX_TEXT_LENGTH = 15;

    /**
     * Lazy load
     */

    private boolean isCreatedView = false;
    /**
     * RecyclerView
     */
    private RecyclerView RecyclerView_entries;
    private EntriesAdapter entriesAdapter;
    private List<EntriesEntity> entriesList;

    public EntriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entries, container, false);
        RecyclerView_entries = (RecyclerView) rootView.findViewById(R.id.RecyclerView_entries);
        TV_entries_count = (TextView) rootView.findViewById(R.id.TV_entries_count);

        entriesList = new ArrayList<>();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isCreatedView) {
            loadEntries();
            initRecyclerView();
            countEntries();
        }
        isCreatedView = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isCreatedView && isVisibleToUser) {
            loadEntries();
            initRecyclerView();
            countEntries();
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager lmr = new LinearLayoutManager(getActivity());
        RecyclerView_entries.setLayoutManager(lmr);
        entriesAdapter = new EntriesAdapter(EntriesFragment.this, entriesList, this);
        RecyclerView_entries.setAdapter(entriesAdapter);
    }

    private void loadEntries() {
        entriesList.clear();
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        Cursor diaryCursor = dbManager.selectDiary(getTopicId());
        for (int i = 0; i < diaryCursor.getCount(); i++) {
            String title = diaryCursor.getString(2);
            String content = diaryCursor.getString(3);
            entriesList.add(
                    new EntriesEntity(diaryCursor.getLong(0), new Date(diaryCursor.getLong(1)),
                            title.substring(0, Math.min(MAX_TEXT_LENGTH, title.length())),
                            content.substring(0, Math.min(MAX_TEXT_LENGTH, content.length())),
                            diaryCursor.getInt(5), diaryCursor.getInt(4),
                            diaryCursor.getInt(6) > 0 ? true : false));
            diaryCursor.moveToNext();
        }
        diaryCursor.close();
        dbManager.closeDB();
    }

    private void countEntries() {
        TV_entries_count.setText(
                String.format(getResources().getString(R.string.entries_count), entriesList.size()));
    }

    @Override
    public void deleteDiary() {
        loadEntries();
        entriesAdapter.notifyDataSetChanged();
        countEntries();
    }
}
