package com.kiminonawa.mydiary.entries.diary.item;

import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryItemHelper extends Observable {

    public final static int MAX_PHOTO_COUNT = 7;


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

    /**
     * Observable
     */


    public int getVisibleHeight() {
        return visibleHeight;
    }

    public int getVisibleWidth() {
        return visibleWidth;
    }

    public void initDiary() {
        //Remove old data
        itemContentLayout.removeAllViews();
        diaryItemList.clear();
        nowPhotoCount = 0;
        setChanged();
        notifyObservers();
    }

    public void createItem(IDairyRow diaryItem) {
        if (diaryItem instanceof DiaryPhoto) {
            nowPhotoCount++;
            Log.e("test", "count - " + nowPhotoCount);
        }
        diaryItemList.add(diaryItem);
        itemContentLayout.addView(diaryItemList.get(diaryItemList.size() - 1).getView());
        if (diaryItemList.size() == 1) {
            setChanged();
            notifyObservers();
        }
    }

    public void createItem(IDairyRow diaryItem, int position) {
        if (diaryItem instanceof DiaryPhoto) {
            nowPhotoCount++;
            Log.e("test", "count - " + nowPhotoCount);
        }
        diaryItemList.add(position, diaryItem);
        itemContentLayout.addView(diaryItem.getView(), position);
        if (diaryItemList.size() == 1) {
            setChanged();
            notifyObservers();
        }

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

    public void remove(int position) {
        if (diaryItemList.get(position) instanceof DiaryPhoto) {
            nowPhotoCount--;
            Log.e("test", "count - " + nowPhotoCount);
        }
        diaryItemList.remove(position);
        if (diaryItemList.size() == 0) {
            setChanged();
            notifyObservers();
        }
    }

}
