package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by daxia on 2016/10/17.
 */

public interface IDairyRow {

    int TYPE_TEXT = 0;
    int TYPE_PHOTO = 1;
    int TYPE_BLOCK = 2;

    void setContent(String content);

    String getContent();

    int getType();

    View getView();

    void initView(Context context, ViewGroup parent);

    void setEditMode(boolean isEditMode);

    //For resort after add new item
    void setPosition(int position);

    int getPosition();

}
