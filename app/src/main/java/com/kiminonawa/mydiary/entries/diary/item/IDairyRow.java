package com.kiminonawa.mydiary.entries.diary.item;

import android.view.View;

/**
 * Created by daxia on 2016/10/17.
 */

public interface IDairyRow {

    int TYPE_TEXT = 0;
    int TYPE_PHOTO = 1;
    int TYPE_WEB_BLOCK = 2;

    int getType();
    View getView();
    String getContent();
}
