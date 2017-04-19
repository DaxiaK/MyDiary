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
    private Uri photoUri;
    private int position;

    public DiaryPhoto(Activity activity) {
        diaryPhotoLayout = new DiaryPhotoLayout(activity);
        //Default is editable
        setEditMode(true);
    }

    /**
     * This tag is only used in view mode
     * @param draweeViewPositionTag
     */
    public void setDraweeViewPositionTag(int draweeViewPositionTag) {
        diaryPhotoLayout.setDraweeViewPositionTag(draweeViewPositionTag);
    }

    /**
     * Edit mode , the delete button
     *
     * @param clickListener
     */
    public void setDeleteClickListener(View.OnClickListener clickListener) {
        diaryPhotoLayout.setDeleteOnClick(clickListener);
    }

    /**
     * The view mode , you can click to open the large imgae
     *
     * @param clickListener
     */
    public void setDraweeViewClickListener(View.OnClickListener clickListener) {
        diaryPhotoLayout.setDraweeViewClick(clickListener);
    }


    public void setPhoto(Uri photoUri, String photoFileName) {
        this.photoUri = photoUri;
        this.photoFileName = photoFileName;
        diaryPhotoLayout.setPhotoUri(photoUri);
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
        //When  content is modified(e.g.insert or delete) , update setDeletePositionTag
        diaryPhotoLayout.setDeletePositionTag(position);
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
