package com.kiminonawa.mydiary.entries.photo;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kiminonawa.mydiary.R;

import java.util.ArrayList;

/**
 * Created by daxia on 2017/4/11.
 */

public class PhotoOverviewAdapter extends RecyclerView.Adapter<PhotoOverviewAdapter.SimpleViewHolder> {

    private final ArrayList<Uri> diaryPhotoFileList;
    private FragmentActivity activity;

    public PhotoOverviewAdapter(FragmentActivity activity, ArrayList<Uri> diaryPhotoFileList) {
        this.activity = activity;
        this.diaryPhotoFileList = diaryPhotoFileList;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.rv_diary_photo_overview_item, parent, false);
        return new SimpleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        holder.mSimpleDraweeView.setImageURI(diaryPhotoFileList.get(position));
        holder.mSimpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoDetailViewerDialogFragment photoDetailViewerDialogFragment =
                        PhotoDetailViewerDialogFragment.newInstance(diaryPhotoFileList, position);
                photoDetailViewerDialogFragment.show(activity.getSupportFragmentManager(), "diaryPhotoBottomSheet");

            }
        });
    }

    @Override
    public int getItemCount() {
        return diaryPhotoFileList.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        private final SimpleDraweeView mSimpleDraweeView;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.drawee_view);
        }
    }
}