package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryItemHelper {

    private List<IDairyRow> diaryItemList;
    private LinearLayout itemContentLayout;
    private int nowPhotoCount = 0;

    /**
     * Make all item < 1 view, so It should be computed show area.
     */
    private int visibleHeight, visibleWidth;

    public DiaryItemHelper(LinearLayout itemContentLayout) {
        this.itemContentLayout = itemContentLayout;
        this.diaryItemList = new ArrayList<>();
        this.visibleHeight = itemContentLayout.getHeight();
        this.visibleWidth = itemContentLayout.getWidth();
        Log.e("test", "height = " + visibleHeight + " width = " + visibleWidth);
    }

    public int getVisibleHeight() {
        return visibleHeight;
    }

    public int getVisibleWidth() {
        return visibleWidth;
    }

    public void initDiary(Context context) {
        //Remove old data
        itemContentLayout.removeAllViews();
        diaryItemList.clear();
        nowPhotoCount = 0;
        //Add defult edittest item
        CreateItem(new DiaryText(context, true));
    }

    public void CreateItem(IDairyRow diaryItem) {
        if (diaryItem instanceof DiaryPhoto) {
            nowPhotoCount++;
            Log.e("test", "count - " + nowPhotoCount);
        }
        diaryItemList.add(diaryItem);
        itemContentLayout.addView(diaryItemList.get(diaryItemList.size() - 1).getView());
    }

    public int getNowPhotoCount() {
        return nowPhotoCount;
    }

    public int getItemSize() {
        return diaryItemList.size();
    }

    public IDairyRow get(int position) {
        return diaryItemList.get(position);
    }


}
