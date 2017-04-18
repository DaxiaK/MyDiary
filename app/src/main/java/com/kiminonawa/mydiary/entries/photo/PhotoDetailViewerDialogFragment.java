package com.kiminonawa.mydiary.entries.photo;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiminonawa.mydiary.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by daxia on 2017/4/12.
 */

public class PhotoDetailViewerDialogFragment extends DialogFragment {

    /**
     * GUI
     */
    @BindView(R.id.VP_diary_photo_detail)
    ViewPager VPDiaryPhotoDetail;
    Unbinder unbinder;


    private PhotoDetailPagerAdapter mAdapter;
    /**
     * Open this dialog from diay photo overview.
     */
    private ArrayList<Uri> diaryPhotoFileList;
    private int selectPosition;

    public static PhotoDetailViewerDialogFragment newInstance(ArrayList<Uri> diaryPhotoFileList, int selectPosition) {
        Bundle args = new Bundle();
        PhotoDetailViewerDialogFragment fragment = new PhotoDetailViewerDialogFragment();
        args.putParcelableArrayList("diaryPhotoFileList", diaryPhotoFileList);
        args.putInt("selectPosition", selectPosition);
        fragment.setArguments(args);
        return fragment;
    }

    public static PhotoDetailViewerDialogFragment newInstance(long topicId, long diaryId, String photoFileName) {
        Bundle args = new Bundle();
        PhotoDetailViewerDialogFragment fragment = new PhotoDetailViewerDialogFragment();
        args.putLong("topicId", topicId);
        args.putLong("diaryId", diaryId);
        args.putString("photoFileName", photoFileName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TransparentDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_diary_photo_detail_viewer, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get args
        //The arraylist is form overview
        diaryPhotoFileList = getArguments().getParcelableArrayList("diaryPhotoFileList");
        selectPosition = getArguments().getInt("selectPosition", -1);
        if (diaryPhotoFileList == null || selectPosition == -1) {
            //TODO Do something to close this dialog
        } else {
            //Init The view pager
            //**Notice** It MUST use getChildFragmentManager()
            //@See:http://stackoverflow.com/questions/23088309/no-view-found-for-id-dialogfragment-fragment
            mAdapter = new PhotoDetailPagerAdapter(getChildFragmentManager(), diaryPhotoFileList);
            VPDiaryPhotoDetail.setAdapter(mAdapter);
            VPDiaryPhotoDetail.setCurrentItem(selectPosition);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}