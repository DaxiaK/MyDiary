package com.kiminonawa.mydiary.main.topic;

import com.kiminonawa.mydiary.R;

/**
 * Created by daxia on 2016/10/17.
 */

public class Contacts implements ITopic {

    private String title;
    private long id;
    private long count;
    private int color;
    private boolean pinned = false;

    public Contacts(long id, String title, int color) {
        this.id = id;
        this.title = title;
        this.color = color;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getType() {
        return TYPE_CONTACTS;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_topic_contacts;
    }

    @Override
    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public long getCount() {
        return count;
    }


    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    @Override
    public boolean isPinned() {
        return pinned;
    }
}
