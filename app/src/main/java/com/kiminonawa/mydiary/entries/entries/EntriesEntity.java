package com.kiminonawa.mydiary.entries.entries;

import android.support.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;

/**
 * Created by daxia on 2016/10/17.
 */

public class EntriesEntity implements Comparable<CalendarDay> {

    private long id;
    private CalendarDay createDate;
    private String title;
    private String summary;
    private int weatherId;
    private int moodId;
    private boolean hasAttachment;


    public EntriesEntity(long id, CalendarDay createDate, String title,
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

    public CalendarDay getCreateDate() {
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


    @Override
    public int compareTo(@NonNull CalendarDay calendarDay) {
        return Long.valueOf( calendarDay.getCalendar().getTimeInMillis()).compareTo(
                this.createDate.getCalendar().getTimeInMillis());
    }
}
