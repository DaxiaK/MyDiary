package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryItemHelper extends Observable {

    public final static int MAX_PHOTO_COUNT = 7;
    public final static int MAX_BLOCK_ITEM = 7;
    public final static int MAX_BLOCK_COUNT = 1;

    private List<IDairyRow> diaryItemList;
    private LinearLayout itemContentLayout;
    private int nowPhotoCount = 0;
    private static int visibleHeight = -1, visibleWidth = -1;


    public DiaryItemHelper(LinearLayout itemContentLayout) {
        this.itemContentLayout = itemContentLayout;
        this.diaryItemList = new ArrayList<>();
    }

    /**
     * Make all item < 1 view, so It should be computed show area.
     * The height & Width is fixed value for a device.
     */
    public static void setVisibleArea(int weight, int height) {
        visibleHeight = height;
        visibleWidth = weight;
    }

    /**
     * Observable
     */
    public static int getVisibleHeight() {
        return visibleHeight;
    }

    public static int getVisibleWidth() {
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

    //TODO 鬆綁list and add view , 再最後再實作一個一次新增
    public void createItem(IDairyRow diaryItem) {
        if (diaryItem instanceof DiaryPhoto) {
            nowPhotoCount++;
        }
        diaryItemList.add(diaryItem);
        if (diaryItemList.size() == 1) {
            setChanged();
            notifyObservers();
        }
    }

    public void addViewToDiaryContent(Context context) {
        diaryItemList.get(diaryItemList.size() - 1).initView(context, itemContentLayout);
        itemContentLayout.addView(diaryItemList.get(diaryItemList.size() - 1).getView());
    }


    public void createItem(IDairyRow diaryItem, int position) {
        if (diaryItem instanceof DiaryPhoto) {
            nowPhotoCount++;
        }
        diaryItemList.add(position, diaryItem);
        if (diaryItemList.size() == 1) {
            setChanged();
            notifyObservers();
        }
    }

    public void addViewToDiaryContent(Context context, int position) {
        diaryItemList.get(position).initView(context, itemContentLayout);
        itemContentLayout.addView(diaryItemList.get(position).getView(), position);
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
        }
        diaryItemList.remove(position);
        if (diaryItemList.size() == 0) {
            setChanged();
            notifyObservers();
        }
    }

    public void resortPosition() {
        for (int i = 0; i < diaryItemList.size(); i++) {
            diaryItemList.get(i).setPosition(i);
        }
    }

    public void mergerAdjacentText(int position) {
        if (diaryItemList.size() > 0 && diaryItemList.get(position).getType() == IDairyRow.TYPE_TEXT) {
            if (position != 0 && diaryItemList.get(position - 1).getType() == IDairyRow.TYPE_TEXT) {
                //First Item
                String mergerStr = diaryItemList.get(position).getContent();
                ((DiaryText) diaryItemList.get(position - 1)).insertText(mergerStr);
                itemContentLayout.removeViewAt(position);
                diaryItemList.remove(position);
            }
        }
    }

}
