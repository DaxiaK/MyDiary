package com.kiminonawa.mydiary.backup.obj;

import com.kiminonawa.mydiary.main.topic.ITopic;

import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public class BUMemo extends BUTopic {

    //For select memo data
    private List<BUMemoEntries> memo_topic_entries_list;


    public BUMemo(long topic_id, String topic_title, int topic_order,
                  int topic_color, List<BUMemoEntries> memo_topic_entries_list) {
        super(topic_id, topic_title, topic_order, topic_color);
        this.topic_type = ITopic.TYPE_MEMO;
        this.memo_topic_entries_list = memo_topic_entries_list;
    }

    @Override
    public List getTopicEntries() {
        return memo_topic_entries_list;
    }

}
