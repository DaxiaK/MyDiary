package com.kiminonawa.mydiary.entries.diary.item;

/**
 * Created by daxia on 2016/12/13.
 */

public class DiaryBlockEntity {

    private String title;
    private String subtitle;
    private String url;


    public DiaryBlockEntity(String title) {
        this(title, "", "");
    }

    public DiaryBlockEntity(String title, String subtitle, String url) {
        this.title = title;
        this.subtitle = subtitle;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getUrl() {
        return url;
    }
}
