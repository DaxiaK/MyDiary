package com.kiminonawa.mydiary.backup;

import com.kiminonawa.mydiary.backup.item.BUDiaryItem;
import com.kiminonawa.mydiary.main.topic.ITopic;

import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public class BUDiary implements IBUTopic {


    //common
    private String diary_topic_title;
    private int diary_topic_color;
    private int diary_topic_order;
    private List<BUDiaryItem> diary_topic_sub_content_list;

    //private diary
    private long diary_topic_time;
    private int diary_topic_mood, diary_topic_weather;
    private boolean diary_topic_attachment;

    public BUDiary(String diary_topic_title, int diary_topic_color, int diary_topic_order, List<BUDiaryItem> diary_topic_sub_content_list,
                   long diary_topic_time, int diary_topic_mood, int diary_topic_weather, boolean diary_topic_attachment) {
        this.diary_topic_title = diary_topic_title;
        this.diary_topic_color = diary_topic_color;
        this.diary_topic_order = diary_topic_order;
        this.diary_topic_sub_content_list = diary_topic_sub_content_list;
        this.diary_topic_time = diary_topic_time;
        this.diary_topic_mood = diary_topic_mood;
        this.diary_topic_weather = diary_topic_weather;
        this.diary_topic_attachment = diary_topic_attachment;
    }

    @Override
    public List getTopicContentItem() {
        return diary_topic_sub_content_list;
    }

    @Override
    public int getOrder() {
        return diary_topic_order;
    }

    @Override
    public String getTitle() {
        return diary_topic_title;
    }

    @Override
    public int getType() {
        return ITopic.TYPE_DIARY;
    }


    @Override
    public int getColor() {
        return diary_topic_color;
    }

    public long getDiaryTopicTime() {
        return diary_topic_time;
    }

    public int getDiaryTopicMood() {
        return diary_topic_mood;
    }

    public int getDiaryTopicWeather() {
        return diary_topic_weather;
    }

    public boolean isDiaryTopicAttachment() {
        return diary_topic_attachment;
    }
}
