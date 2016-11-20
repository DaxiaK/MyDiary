package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.kiminonawa.mydiary.shared.gui.DiaryPhotoLayout;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryPhoto implements IDairyRow {

    private DiaryPhotoLayout DiaryPhotoLayout;
    private String photoFileName;


    public DiaryPhoto(Context context, Bitmap bitmap, String photoFileName, int position, View.OnClickListener clickListener) {
        this.photoFileName = photoFileName;
        createPhoto(context, bitmap);
        DiaryPhotoLayout.setDeleteOnClick(clickListener,position);
    }

    private void createPhoto(Context context, Bitmap bitmap) {
        DiaryPhotoLayout = new DiaryPhotoLayout(context);
        DiaryPhotoLayout.setPhotoBitmap(bitmap);
    }

    public ImageView getPhoto() {
        return DiaryPhotoLayout.getPhoto();
    }

    @Override
    public int getType() {
        return TYPE_PHOTO;
    }

    @Override
    public View getView() {
        return DiaryPhotoLayout;
    }

    @Override
    public String getContent() {
        return photoFileName;
    }

}
