package com.kiminonawa.mydiary.entries.entries;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.BaseDiaryFragment;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.entries.diary.item.IDairyRow;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.ViewTools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EntriesFragment extends BaseDiaryFragment implements
        DiaryViewerDialogFragment.DiaryViewerCallback, View.OnClickListener {

    /**
     * UI
     */
    private TextView TV_entries_count;
    private RelativeLayout RL_entries_edit_bar;
    private TextView TV_entries_edit_msg;
    private ImageView IV_entries_edit, IV_entries_photo;
    private final static int MAX_TEXT_LENGTH = 10;

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

        IV_entries_edit = (ImageView) rootView.findViewById(R.id.IV_entries_edit);
        IV_entries_edit.setOnClickListener(this);
        IV_entries_photo = (ImageView) rootView.findViewById(R.id.IV_entries_photo);
        TV_entries_edit_msg = (TextView) rootView.findViewById(R.id.TV_entries_edit_msg);
        TV_entries_edit_msg.setTextColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        RecyclerView_entries = (RecyclerView) rootView.findViewById(R.id.RecyclerView_entries);
        TV_entries_count = (TextView) rootView.findViewById(R.id.TV_entries_count);
        RL_entries_edit_bar = (RelativeLayout) rootView.findViewById(R.id.RL_entries_edit_bar);
        RL_entries_edit_bar.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
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
        if (isCreatedView && isVisibleToUser && ((DiaryActivity) getActivity()).isEntriesRefresh()) {
            updateEntriesData();
            ((DiaryActivity) getActivity()).setEntriesRefresh(false);
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager lmr = new LinearLayoutManager(getActivity());
        RecyclerView_entries.setLayoutManager(lmr);
        entriesAdapter = new EntriesAdapter(EntriesFragment.this, entriesList, this);
        RecyclerView_entries.setAdapter(entriesAdapter);
        //true for close all view
        setEditModeUI(true);
    }

    private void loadEntries() {
        entriesList.clear();
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        //Select diary info
        Cursor diaryCursor = dbManager.selectDiaryList(getTopicId());
        for (int i = 0; i < diaryCursor.getCount(); i++) {
            //get diary info
            String title = diaryCursor.getString(2);
            if ("".equals(title)) {
                title = getString(R.string.diary_no_title);
            }
            EntriesEntity entity = new EntriesEntity(diaryCursor.getLong(0), new Date(diaryCursor.getLong(1)),
                    title.substring(0, Math.min(MAX_TEXT_LENGTH, title.length())),
                    diaryCursor.getInt(4), diaryCursor.getInt(3),
                    diaryCursor.getInt(5) > 0 ? true : false);

            //select first diary content
            Cursor diaryContentCursor = dbManager.selectDiaryContentByDiaryId(entity.getId());
            if (diaryContentCursor != null && diaryContentCursor.getCount() > 0) {
                String summary = "";
                //Check content Type
                if (diaryContentCursor.getInt(1) == IDairyRow.TYPE_PHOTO) {
                    summary = getString(R.string.entries_summary_photo);
                } else if (diaryContentCursor.getInt(1) == IDairyRow.TYPE_TEXT) {
                    summary = diaryContentCursor.getString(3)
                            .substring(0, Math.min(MAX_TEXT_LENGTH, diaryContentCursor.getString(3).length()));
                }
                entity.setSummary(summary);
                diaryContentCursor.close();
            }
            //Add entity
            entriesList.add(entity);
            diaryCursor.moveToNext();
        }
        diaryCursor.close();
        dbManager.closeDB();
    }

    private void countEntries() {
        TV_entries_count.setText(
                String.format(getResources().getString(R.string.entries_count), entriesList.size()));
    }

    public void setEditModeUI(boolean isEditMode) {
        if (isEditMode) {
            entriesAdapter.setEditMode(false);
            TV_entries_edit_msg.setVisibility(View.GONE);
            IV_entries_edit.setImageDrawable(ViewTools.getDrawable(getActivity(), R.drawable.ic_mode_edit_white_24dp));
        } else {
            entriesAdapter.setEditMode(true);
            TV_entries_edit_msg.setVisibility(View.VISIBLE);
            IV_entries_edit.setImageDrawable(ViewTools.getDrawable(getActivity(), R.drawable.ic_mode_edit_cancel_white_24dp));
        }
    }

    public void updateEntriesData() {
        loadEntries();
        entriesAdapter.notifyDataSetChanged();
        countEntries();
    }

    @Override
    public void deleteDiary() {
        updateEntriesData();
    }

    @Override
    public void updateDiary() {
        updateEntriesData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_entries_edit:
                setEditModeUI(entriesAdapter.isEditMode());
                break;
            case R.id.IV_entries_photo:
                //TODO show some thing like album
                break;

        }
    }
}
