package com.kiminonawa.mydiary.entries.photo;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ScreenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daxia on 2017/4/11.
 */

public class PhotoOverviewAdapter extends RecyclerView.Adapter<PhotoOverviewAdapter.SimpleViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    private final ArrayList<Uri> diaryPhotoFileList;
    private Map<Uri, Integer> heightMap = new HashMap<>();
    private Map<Uri, Integer> widthMap = new HashMap<>();
    private OnItemClickListener mItemClickListener;
    private Context mContext;
    private int resizePhotoWidth, resizePhotoHeight;

    public PhotoOverviewAdapter(Context context, ArrayList<Uri> diaryPhotoFileList) {
        this.mContext = context;
        this.diaryPhotoFileList = diaryPhotoFileList;
        resizePhotoWidth = ScreenHelper.getScreenWidth(context) / 3;
        resizePhotoHeight = ScreenHelper.dpToPixel(mContext.getResources(), 150);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_diary_photo_overview_item, parent, false);
        return new SimpleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        final Uri photoUri = diaryPhotoFileList.get(position);


        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(diaryPhotoFileList.get(position))
                .setResizeOptions(new ResizeOptions(resizePhotoWidth, resizePhotoHeight))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(false)
                .build();
        holder.SDV_CV_diary_photo_overview.setController(controller);

    }

    @Override
    public int getItemCount() {
        return diaryPhotoFileList.size();
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final SimpleDraweeView SDV_CV_diary_photo_overview;
        private final CardView CV_diary_photo_overview;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            SDV_CV_diary_photo_overview = (SimpleDraweeView) itemView.findViewById(R.id.SDV_CV_diary_photo_overview);
            CV_diary_photo_overview = (CardView) itemView.findViewById(R.id.CV_diary_photo_overview);
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(getAdapterPosition());
            }

        }
    }
}