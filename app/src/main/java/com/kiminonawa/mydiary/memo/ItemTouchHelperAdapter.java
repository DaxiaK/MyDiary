package com.kiminonawa.mydiary.memo;

/**
 * Created by daxia on 2017/1/5.
 */

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemSwap(int position);

    void onItemMoveFinish();

}
