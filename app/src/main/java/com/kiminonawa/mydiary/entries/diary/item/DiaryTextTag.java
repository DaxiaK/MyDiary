package com.kiminonawa.mydiary.entries.diary.item;

/**
 * Created by daxia on 2016/11/21.
 */

public class DiaryTextTag {

    private int positionTag;
    private String nextEditTextStr;

    public DiaryTextTag(int positionTag) {
        this.positionTag = positionTag;
    }

    public int getPositionTag() {
        return positionTag;
    }

    public void setPositionTag(int positionTag) {
        this.positionTag = positionTag;
    }

    public String getNextEditTextStr() {
        return nextEditTextStr;
    }

    public void setNextEditTextStr(String nextEditTextStr) {
        this.nextEditTextStr = nextEditTextStr;
    }
}
