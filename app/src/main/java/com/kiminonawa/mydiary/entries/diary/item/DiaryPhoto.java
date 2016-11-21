package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.kiminonawa.mydiary.shared.gui.DiaryPhotoLayout;

import java.io.File;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryPhoto implements IDairyRow {

    private DiaryPhotoLayout DiaryPhotoLayout;
    private String photoFileName;

    //For Save
    private Bitmap tempBitmap;


    public DiaryPhoto(Context context) {
        DiaryPhotoLayout = new DiaryPhotoLayout(context);
        //Default is editable
        setEditMode(true);
    }

    public void setDeleteClickListener(int positionTag, View.OnClickListener clickListener) {
        DiaryPhotoLayout.setDeleteOnClick(clickListener, positionTag);
    }

    public void setPhoto(Bitmap bitmap, String photoFileName) {
        this.photoFileName = photoFileName;
        DiaryPhotoLayout.setPhotoBitmap(bitmap);
    }

    public ImageView getPhoto() {
        return DiaryPhotoLayout.getPhoto();
    }


    public Bitmap getTempBitmap() {
        return tempBitmap;
    }

    public void setTempBitmap(Bitmap tempBitmap) {
        this.tempBitmap = tempBitmap;
    }

    @Override
    public void setContent(String content) {
        //This content is path
        File imgFile = new File(content);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(content);
            DiaryPhotoLayout.getPhoto().setImageBitmap(bitmap);
        }
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
    public void setEditMode(boolean isEditMode) {
        DiaryPhotoLayout.setVisibleViewByMode(isEditMode);
    }

    @Override
    public String getContent() {
        return photoFileName;
    }

}
