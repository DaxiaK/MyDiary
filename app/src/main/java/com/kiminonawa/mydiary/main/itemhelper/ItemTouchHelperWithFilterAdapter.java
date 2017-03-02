package com.kiminonawa.mydiary.main.itemhelper;

/**
 * Created by daxia on 2017/1/5.
 */

public interface ItemTouchHelperWithFilterAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemSwap(int position);

    void onItemMoveFinish();

    boolean isFilter();

}
