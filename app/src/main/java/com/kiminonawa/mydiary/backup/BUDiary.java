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
    private int diary_topic_order;
    private int diary_topic_color;
    private List<BUDiaryItem> diary_topic_sub_content_list;

    public BUDiary(String diary_topic_title, int diary_topic_order,
                   int diary_topic_color, List<BUDiaryItem> diary_topic_sub_content_list) {
        this.diary_topic_title = diary_topic_title;
        this.diary_topic_order = diary_topic_order;
        this.diary_topic_color = diary_topic_color;
        this.diary_topic_sub_content_list = diary_topic_sub_content_list;
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

}
