package com.kiminonawa.mydiary.main.topic;

import android.support.annotation.DrawableRes;

/**
 * Created by daxia on 2016/10/17.
 */

public interface ITopic {
    /**
     * The contacts , Mitsuha  and Taki change their cell phone numberin this function.
     */
    int TYPE_CONTACTS = 0;
    /**
     * Mitsuha and Taki write daily diary when their soul change.
     */
    int TYPE_DIARY = 1;

    /**
     * Mitsuha and Taki add some memo for notice that something can't do.
     */
    int TYPE_MEMO = 2;

    String getTitle();

    int getType();

    long getId();

    @DrawableRes
    int getIcon();

    int getCount();

    int getColor();

}
