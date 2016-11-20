package com.kiminonawa.mydiary.entries.entries;

import java.util.Date;

/**
 * Created by daxia on 2016/10/17.
 */

public class EntriesEntity {

    private long id;
    private Date createDate;
    private String title;
    private String summary;
    private int weatherId;
    private int moodId;
    private boolean hasAttachment;


    public EntriesEntity(long id, Date createDate, String title,
                         int weatherId, int moodId, boolean hasAttachment) {
        this.id = id;
        this.createDate = createDate;
        this.title = title;
        this.weatherId = weatherId;
        this.moodId = moodId;
        this.hasAttachment = hasAttachment;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getId() {
        return id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public boolean hasAttachment() {
        return hasAttachment;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public int getMoodId() {
        return moodId;
    }


}
