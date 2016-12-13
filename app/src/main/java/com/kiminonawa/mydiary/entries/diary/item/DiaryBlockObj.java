package com.kiminonawa.mydiary.entries.diary.item;

/**
 * Created by daxia on 2016/12/13.
 */

public class DiaryBlockObj {

    private String blockItemTitle;
    private String blockItemContent;


    public DiaryBlockObj(String blockItemTitle) {
        this(blockItemTitle, "");
    }

    public DiaryBlockObj(String blockItemTitle, String blockItemContent) {
        this.blockItemTitle = blockItemTitle;
        this.blockItemContent = blockItemContent;
    }

    public String getBlockItemTitle() {
        return blockItemTitle;
    }

    public void setBlockItemTitle(String blockItemTitle) {
        this.blockItemTitle = blockItemTitle;
    }

    public String getBlockItemContent() {
        return blockItemContent;
    }

    public void setBlockItemContent(String blockItemContent) {
        this.blockItemContent = blockItemContent;
    }
}
