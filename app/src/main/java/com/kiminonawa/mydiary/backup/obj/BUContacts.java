package com.kiminonawa.mydiary.backup.obj;

import com.kiminonawa.mydiary.main.topic.ITopic;

import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public class BUContacts extends BUTopic {


    private List<BUDiaryEntries> diary_topic_entries_list;

    public BUContacts(long topic_id, String topic_title, int topic_order,
                      int topic_color, List<BUDiaryEntries> diary_topic_entries_list) {
        super(topic_id, topic_title, topic_order, topic_color);
        this.topic_type = ITopic.TYPE_DIARY;
        this.diary_topic_entries_list = diary_topic_entries_list;
    }

    @Override
    public List getTopicEntries() {
        return diary_topic_entries_list;
    }


}
