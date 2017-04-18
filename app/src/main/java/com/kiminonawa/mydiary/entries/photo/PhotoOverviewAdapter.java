package com.kiminonawa.mydiary.entries.photo;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
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

    public PhotoOverviewAdapter(Context context, ArrayList<Uri> diaryPhotoFileList) {
        this.mContext = context;
        this.diaryPhotoFileList = diaryPhotoFileList;
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

        if (heightMap.containsKey(photoUri)) {
            int height = heightMap.get(photoUri);
            if (height > 0) {
                updateItemHeight(height, holder);
                holder.SDV_CV_diary_photo_overview.setImageURI(photoUri);
                return;
            }

        }
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                if (qualityInfo.isOfGoodEnoughQuality()) {
                    int heightTarget = (int) getTargetHeight(imageInfo.getWidth(), imageInfo.getHeight(), holder, photoUri);
                    if (heightTarget <= 0) return;
                    heightMap.put(photoUri, heightTarget);
                    updateItemHeight(heightTarget, holder);
                }
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
            }
        };
        //The temp height
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(diaryPhotoFileList.get(position))
                .setResizeOptions(
                        new ResizeOptions(ScreenHelper.dpToPixel(mContext.getResources(), 150),
                                ScreenHelper.dpToPixel(mContext.getResources(), 150)))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setControllerListener(controllerListener)
                .setTapToRetryEnabled(false)
                .build();
        holder.SDV_CV_diary_photo_overview.setController(controller);

    }

    @Override
    public int getItemCount() {
        return diaryPhotoFileList.size();
    }


    private float getTargetHeight(float width, float height, SimpleViewHolder viewHolder, Uri photoUri) {
        View childView = viewHolder.SDV_CV_diary_photo_overview;
        float widthTarget;
        if (widthMap.containsKey(photoUri)) widthTarget = widthMap.get(photoUri);
        else {
            widthTarget = childView.getMeasuredWidth();
            if (widthTarget > 0) {
                widthMap.put(photoUri, (int) widthTarget);
            }
        }

        return height * (widthTarget / width);
    }

    private void updateItemHeight(int height, SimpleViewHolder viewHolder) {
        View childView = viewHolder.SDV_CV_diary_photo_overview;
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) childView.getLayoutParams();
        layoutParams.height = height;
        viewHolder.CV_diary_photo_overview.updateViewLayout(childView, layoutParams);
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