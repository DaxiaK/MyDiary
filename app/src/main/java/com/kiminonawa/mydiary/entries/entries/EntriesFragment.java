package com.kiminonawa.mydiary.entries.entries;


import android.content.Intent;
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
import com.kiminonawa.mydiary.entries.BaseDiaryFragment;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.entries.photo.PhotoOverviewActivity;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.ViewTools;


public class EntriesFragment extends BaseDiaryFragment implements
        DiaryViewerDialogFragment.DiaryViewerCallback, View.OnClickListener {

    /**
     * UI
     */
    private TextView TV_entries_count;
    private RelativeLayout RL_entries_edit_bar;
    private TextView TV_entries_edit_msg;
    private ImageView IV_entries_edit, IV_entries_photo;

    /**
     * RecyclerView
     */
    private RecyclerView RecyclerView_entries;
    private EntriesAdapter entriesAdapter;

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
        IV_entries_photo.setOnClickListener(this);
        TV_entries_edit_msg = (TextView) rootView.findViewById(R.id.TV_entries_edit_msg);
        TV_entries_edit_msg.setTextColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        RecyclerView_entries = (RecyclerView) rootView.findViewById(R.id.RecyclerView_entries);
        TV_entries_count = (TextView) rootView.findViewById(R.id.TV_entries_count);
        RL_entries_edit_bar = (RelativeLayout) rootView.findViewById(R.id.RL_entries_edit_bar);
        RL_entries_edit_bar.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerView();
        countEntries();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void initRecyclerView() {
        LinearLayoutManager lmr = new LinearLayoutManager(getActivity());
        RecyclerView_entries.setLayoutManager(lmr);
        entriesAdapter = new EntriesAdapter(EntriesFragment.this, getEntriesList());
        RecyclerView_entries.setAdapter(entriesAdapter);
        //true for close all view
        setEditModeUI(true);
    }


    private void countEntries() {
        TV_entries_count.setText(
                getResources().getQuantityString(R.plurals.entries_count,
                        getEntriesList().size(),getEntriesList().size()));
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

    public void gotoDiaryPosition(int position) {
        RecyclerView_entries.scrollToPosition(position);
    }

    public void updateEntriesData() {
        updateEntriesList();
        entriesAdapter.notifyDataSetChanged();
        countEntries();
        ((DiaryActivity) getActivity()).callCalendarRefresh();
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
                Intent gotoPhotoOverviewIntent = new Intent(getActivity(), PhotoOverviewActivity.class);
                gotoPhotoOverviewIntent.putExtra(PhotoOverviewActivity.PHOTO_OVERVIEW_TOPIC_ID, getTopicId());
                getActivity().startActivity(gotoPhotoOverviewIntent);
                break;

        }
    }
}
