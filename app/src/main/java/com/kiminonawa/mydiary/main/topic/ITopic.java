package com.kiminonawa.mydiary.main.topic;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by daxia on 2016/10/17.
 */

public interface ITopic {
    /**
     * The contacts , Mitsuha  and Taki change their cell phone number in this function.
     */
    int TYPE_CONTACTS = 0;
    /**
     * Mitsuha and Taki write daily diary when their soul change.
     */
    int TYPE_DIARY = 1;

    /**
     * Mitsuha and Taki add some memo to notice that something can't do.
     */
    int TYPE_MEMO = 2;

    @Retention(SOURCE)
    @IntDef({TYPE_CONTACTS, TYPE_DIARY, TYPE_MEMO})
    public @interface TopicType {
    }

    String getTitle();

    /**
     * For update topic
     */
    void setTitle(String title);

    @TopicType
    int getType();

    long getId();

    @DrawableRes
    int getIcon();

    /**
     * For update count in Main Page
     */
    void setCount(long count);

    long getCount();

    int getColor();

    /**
     * For update topic
     */
    void setColor(int color);

    /**
     * For the left swipe
     */

    void setPinned(boolean pinned);

    boolean isPinned();
}
