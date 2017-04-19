package com.kiminonawa.mydiary.shared.gui;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.shared.ScreenHelper;

/**
 * Created by daxia on 2016/11/20.
 */

public class DiaryPhotoLayout extends LinearLayout {

    private SimpleDraweeView SDV_diary_new_photo;
    private ImageView IV_diary_photo_delete;

    public DiaryPhotoLayout(Activity activity) {
        super(activity);
        View v = LayoutInflater.from(activity).inflate(R.layout.layout_diaryphoto, this, true);
        SDV_diary_new_photo = (SimpleDraweeView) v.findViewById(R.id.SDV_diary_new_photo);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) SDV_diary_new_photo.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = DiaryItemHelper.getVisibleHeight(activity);
        SDV_diary_new_photo.setLayoutParams(params);
        SDV_diary_new_photo.setAspectRatio(ScreenHelper.getScreenRatio(activity));
        IV_diary_photo_delete = (ImageView) v.findViewById(R.id.IV_diary_photo_delete);
    }

    public void setPhotoUri( Uri photoUri) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(photoUri)
                .setResizeOptions(new ResizeOptions(DiaryItemHelper.getVisibleWidth(getContext()),
                        DiaryItemHelper.getVisibleHeight(getContext())))
                .setRotationOptions(RotationOptions.autoRotate())
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        SDV_diary_new_photo.setController(controller);
    }

    public void setDeleteOnClick(OnClickListener listener) {
        IV_diary_photo_delete.setOnClickListener(listener);
    }

    public void setDeletePositionTag(int position) {
        IV_diary_photo_delete.setTag(position);
    }

    public void setDraweeViewClick(OnClickListener listener) {
        SDV_diary_new_photo.setOnClickListener(listener);
    }

    public void setDraweeViewPositionTag(int position) {
        SDV_diary_new_photo.setTag(position);
    }

    public void setVisibleViewByMode(boolean isEditMode) {
        if (isEditMode) {
            IV_diary_photo_delete.setVisibility(VISIBLE);
        } else {
            IV_diary_photo_delete.setVisibility(GONE);
        }
    }

    public SimpleDraweeView getPhoto() {
        return SDV_diary_new_photo;
    }


}
