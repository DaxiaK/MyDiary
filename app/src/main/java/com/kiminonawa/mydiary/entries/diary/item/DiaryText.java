package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.kiminonawa.mydiary.shared.ViewTools;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryText implements IDairyRow {

    private EditText EDT_diary_text;


    public DiaryText(Context context) {
        createEditText(context);
        //Default is editable
        setEditMode(true);
    }

    private void createEditText(Context context) {
        EDT_diary_text = new EditText(context);
        EDT_diary_text.setTextColor(Color.BLACK);
        EDT_diary_text.setBackgroundColor(Color.TRANSPARENT);
        EDT_diary_text.setGravity(Gravity.TOP | Gravity.LEFT);
        //2dp paddding
        int padding = ViewTools.dpToPixel(context.getResources(), 2);
        EDT_diary_text.setPadding(padding, padding, padding, padding);
        EDT_diary_text.requestFocus();
    }

    public void setTag(int positionTag) {
        DiaryTextTag tag = new DiaryTextTag(positionTag);
        EDT_diary_text.setTag(tag);
    }

    @Override
    public void setContent(String content) {
        EDT_diary_text.setText(content);
    }

    @Override
    public int getType() {
        return TYPE_TEXT;
    }

    @Override
    public View getView() {
        return EDT_diary_text;
    }

    @Override
    public void setEditMode(boolean isEditMode) {
        if (isEditMode) {
            EDT_diary_text.setFocusable(true);
            EDT_diary_text.setFocusableInTouchMode(true);
            EDT_diary_text.setClickable(true);
            EDT_diary_text.setEnabled(true);
        } else {
            EDT_diary_text.setFocusable(false);
            EDT_diary_text.setFocusableInTouchMode(false);
            EDT_diary_text.setClickable(false);
            EDT_diary_text.setEnabled(false);

        }
    }

    @Override
    public String getContent() {
        return EDT_diary_text.getText().toString();
    }
}
