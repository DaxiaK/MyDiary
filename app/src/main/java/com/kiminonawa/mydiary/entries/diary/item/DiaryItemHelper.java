package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.widget.LinearLayout;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ScreenHelper;
import com.kiminonawa.mydiary.shared.statusbar.ChinaPhoneHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryItemHelper extends Observable {

    public final static int MAX_PHOTO_COUNT = 7;


    //For test to Public
    private List<IDairyRow> diaryItemList;
    private LinearLayout itemContentLayout;
    private int nowPhotoCount = 0;


    public DiaryItemHelper(LinearLayout itemContentLayout) {
        this.itemContentLayout = itemContentLayout;
        this.diaryItemList = new ArrayList<>();
    }

    /**
     * Make all item < 1 screen, so It should be computed show area.
     * The height & Width is fixed value for a device.
     */
    public static int getVisibleHeight(Context context) {
        int topbarHeight = context.getResources().getDimensionPixelOffset(R.dimen.top_bar_height);
        int imageHeight;
        if (ChinaPhoneHelper.getDeviceStatusBarType() == ChinaPhoneHelper.OTHER) {
            imageHeight = ScreenHelper.getScreenHeight(context)
                    - ScreenHelper.getStatusBarHeight(context)
                    //diary activity top bar  -( diary info + diary bottom bar + diary padding+ photo padding)
                    - topbarHeight - ScreenHelper.dpToPixel(context.getResources(), 120 + 40 + (2 * 5) + (2 * 5));
        } else {
            imageHeight = ScreenHelper.getScreenHeight(context)
                    //diary activity top bar  -( diary info + diary bottom bar + diary padding + photo padding)
                    - topbarHeight - ScreenHelper.dpToPixel(context.getResources(), 120 + 40 + (2 * 5) + (2 * 5));
        }
        return imageHeight;
    }

    public static int getVisibleWidth(Context context) {
        int imageWeight = ScreenHelper.getScreenWidth(context) -
                //(diary padding + photo padding)
                ScreenHelper.dpToPixel(context.getResources(), (2 * 5) + (2 * 5));
        return imageWeight;
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
        itemContentLayout.addView(diaryItemList.get(diaryItemList.size() - 1).getView());
        if (diaryItemList.size() == 1) {
            setChanged();
            notifyObservers();
        }
    }

    public void createItem(IDairyRow diaryItem, int position) {
        if (diaryItem instanceof DiaryPhoto) {
            nowPhotoCount++;
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
