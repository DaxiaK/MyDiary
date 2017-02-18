package com.kiminonawa.mydiary.entries.diary.item;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.kiminonawa.mydiary.shared.gui.DiaryPhotoLayout;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryPhoto implements IDairyRow {

    private DiaryPhotoLayout diaryPhotoLayout;
    private String photoFileName;
    private int position;

    public DiaryPhoto(Activity activity) {
        diaryPhotoLayout = new DiaryPhotoLayout(activity);
        //Default is editable
        setEditMode(true);
    }

    public void setDeleteClickListener(int positionTag, View.OnClickListener clickListener) {
        diaryPhotoLayout.setDeleteOnClick(clickListener);
        diaryPhotoLayout.setPositiontag(positionTag);
    }

    public void setPhoto(Uri photoUri, String photoFileName) {
        this.photoFileName = photoFileName;
        diaryPhotoLayout.setPhotoUri( photoUri);
    }


    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    @Override
    public void setContent(String content) {
        diaryPhotoLayout.setPhotoUri(Uri.parse(content));
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
