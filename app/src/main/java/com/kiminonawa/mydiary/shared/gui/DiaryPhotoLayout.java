package com.kiminonawa.mydiary.shared.gui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

    public DiaryPhotoLayout(Context context) {
        super(context);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_diaryphoto, this, true);
        SDV_diary_new_photo = (SimpleDraweeView) v.findViewById(R.id.SDV_diary_new_photo);
        SDV_diary_new_photo.setAspectRatio(ScreenHelper.getScreenRatio());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                DiaryItemHelper.getVisibleWidth(), FrameLayout.LayoutParams.WRAP_CONTENT);
        SDV_diary_new_photo.setLayoutParams(params);
        IV_diary_photo_delete = (ImageView) v.findViewById(R.id.IV_diary_photo_delete);

    }

    public void setPhotoUri(Uri photoUri) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(photoUri)
                .setResizeOptions(new ResizeOptions(DiaryItemHelper.getVisibleWidth(),
                        DiaryItemHelper.getVisibleHeight()))
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

    public void setPositiontag(int position) {
        IV_diary_photo_delete.setTag(position);
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
