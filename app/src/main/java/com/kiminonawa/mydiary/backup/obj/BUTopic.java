package com.kiminonawa.mydiary.backup.obj;

import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public abstract class BUTopic {

    protected long topic_id;
    protected String topic_title;
    protected int topic_type;
    protected int topic_order;
    protected int topic_color;


    protected BUTopic(long topic_id, String topic_title, int topic_order, int topic_color) {
        this.topic_id = topic_id;
        this.topic_title = topic_title;
        this.topic_order = topic_order;
        this.topic_color = topic_color;
    }

    public int getOrder() {
        return topic_order;
    }

    public String getTitle() {
        return topic_title;
    }

    public int getType() {
        return topic_type;
    }

    public long getId() {
        return topic_id;
    }

    public int getColor() {
        return topic_color;
    }

    abstract List getTopicEntries();

}
