package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ViewTools;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryText implements IDairyRow {

    private EditText EDT_diary_text;


    public DiaryText(Context context, boolean hasHint) {
        createEditText(context, hasHint);
    }

    private void createEditText(Context context, boolean hasHint) {
        EDT_diary_text = new EditText(context);
        EDT_diary_text.setTextColor(Color.BLACK);
        EDT_diary_text.setBackgroundColor(Color.TRANSPARENT);
        EDT_diary_text.setGravity(Gravity.TOP | Gravity.LEFT);
        //2dp
        int padding = ViewTools.dpToPixel(context.getResources(), 2);
        EDT_diary_text.setPadding(padding, padding, padding, padding);
        if (hasHint) {
            EDT_diary_text.setHint(context.getString(R.string.diary_content_hint));
        }
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
    public String getContent() {
        return EDT_diary_text.getText().toString();
    }
}
