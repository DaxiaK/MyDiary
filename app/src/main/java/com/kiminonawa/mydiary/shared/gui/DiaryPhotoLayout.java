package com.kiminonawa.mydiary.shared.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kiminonawa.mydiary.R;

/**
 * Created by daxia on 2016/11/20.
 */

public class DiaryPhotoLayout extends LinearLayout {

    private ImageView IV_diary_photo, IV_diary_photo_delete;

    public DiaryPhotoLayout(Context context) {
        super(context);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_diaryphoto, this, true);
        IV_diary_photo = (ImageView) v.findViewById(R.id.IV_diary_photo);
        IV_diary_photo_delete = (ImageView) v.findViewById(R.id.IV_diary_photo_delete);
    }

    public void setPhotoBitmap(Bitmap bitmap) {
        IV_diary_photo.setImageBitmap(bitmap);
    }

    public void setDeleteOnClick(OnClickListener listener , int position) {
        IV_diary_photo_delete.setOnClickListener(listener);
        IV_diary_photo_delete.setTag(position);
    }

    public ImageView getPhoto() {
        return IV_diary_photo;
    }


}
