package com.kiminonawa.mydiary.entries.photo;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kiminonawa.mydiary.R;

import java.io.File;
import java.util.List;

/**
 * Created by daxia on 2017/4/11.
 */

public class PhotoOverviewAdapter extends RecyclerView.Adapter<PhotoOverviewAdapter.SimpleViewHolder> {

    private final List<File> mFileList;
    private FragmentActivity actvity;

    public PhotoOverviewAdapter(FragmentActivity actvity, List<File> fileList) {
        this.actvity = actvity;
        mFileList = fileList;
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
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.mSimpleDraweeView.setImageURI(Uri.fromFile(mFileList.get(position)));
        holder.mSimpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoDetailViewerFragment photoDetailViewerFragment = new PhotoDetailViewerFragment();
                photoDetailViewerFragment.show(actvity.getSupportFragmentManager(), "diaryPhotoBottomSheet");

            }
        });
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        private final SimpleDraweeView mSimpleDraweeView;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.drawee_view);
        }
    }
}