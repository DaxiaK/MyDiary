package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.kiminonawa.mydiary.shared.BitmapHelper;
import com.kiminonawa.mydiary.shared.ExifUtil;
import com.kiminonawa.mydiary.shared.gui.DiaryPhotoLayout;

import java.io.IOException;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryPhoto implements IDairyRow {

    private DiaryPhotoLayout diaryPhotoLayout;
    private String photoFileName;
    private int position;

    public DiaryPhoto(Context context) {
        diaryPhotoLayout = new DiaryPhotoLayout(context);
        //Default is editable
        setEditMode(true);
    }

    public void setDeleteClickListener(int positionTag, View.OnClickListener clickListener) {
        diaryPhotoLayout.setDeleteOnClick(clickListener);
        diaryPhotoLayout.setPositiontag(positionTag);
    }

    public void setPhoto(Bitmap bitmap, String photoFileName) {
        this.photoFileName = photoFileName;
        diaryPhotoLayout.setPhotoBitmap(bitmap);
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    @Override
    public void setContent(String content) {
        //For some big size image
        try {
            Bitmap bitmap = ExifUtil.rotateBitmap(content,
                    BitmapHelper.getBitmapFromTempFileSrc(content,
                            DiaryItemHelper.getVisibleWidth(), DiaryItemHelper.getVisibleHeight()));
            diaryPhotoLayout.setPhotoBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
        diaryPhotoLayout.setPositiontag(position);
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public int getType() {
        return TYPE_PHOTO;
    }

    @Override
    public View getView() {
        return diaryPhotoLayout;
    }

    @Override
    public void setEditMode(boolean isEditMode) {
        diaryPhotoLayout.setVisibleViewByMode(isEditMode);
    }

    @Override
    public String getContent() {
        return photoFileName;
    }

}
