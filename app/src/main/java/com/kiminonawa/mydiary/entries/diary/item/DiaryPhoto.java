package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryPhoto implements IDairyRow {

    private ImageView IV_diary_photo;


    public DiaryPhoto(Context context, Bitmap bitmap) {
        createEditText(context, bitmap);

    }

    private void createEditText(Context context, Bitmap bitmap) {
        IV_diary_photo = new ImageView(context);
        IV_diary_photo.setImageBitmap(bitmap);
        IV_diary_photo.setAdjustViewBounds(true);
    }

    @Override
    public int getType() {
        return TYPE_PHOTO;
    }

    @Override
    public View getView() {
        return IV_diary_photo;
    }
}
