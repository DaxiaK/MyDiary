package com.kiminonawa.mydiary.backup;

import com.kiminonawa.mydiary.main.topic.ITopic;

import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public class BUDiary implements IBUTopic{

    private long diary_time;
    private String diary_title;
    private int diary_mood, diary_weather;
    private boolean diary_attachment;
    private List<BUDiaryItem> diaryItemList;

    @Override
    public List getTopicContentItem() {
        return diaryItemList;
    }

    @Override
    public String getTitle() {
        return diary_title;
    }

    @Override
    public int getType() {
        return ITopic.TYPE_DIARY;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public int getIcon() {
        return 0;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public int getColor() {
        return 0;
    }
}
