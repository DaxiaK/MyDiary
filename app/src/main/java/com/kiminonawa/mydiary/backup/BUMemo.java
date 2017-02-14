package com.kiminonawa.mydiary.backup;

import com.kiminonawa.mydiary.backup.entries.BUMemoEntries;
import com.kiminonawa.mydiary.main.topic.ITopic;

import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public class BUMemo implements IBUTopic {

    //common
    private String memo_topic_title;
    private int memo_topic_order;
    private int memo_topic_color;
    private List<BUMemoEntries> memoItemItemList;


    public BUMemo(String memo_topic_title, int memo_topic_order,
                  int memo_topic_color, List<BUMemoEntries> memoItemItemList) {
        this.memo_topic_title = memo_topic_title;
        this.memo_topic_order = memo_topic_order;
        this.memo_topic_color = memo_topic_color;
        this.memoItemItemList = memoItemItemList;
    }

    @Override
    public List getTopicContentItem() {
        return memoItemItemList;
    }

    @Override
    public int getOrder() {
        return memo_topic_order;
    }

    @Override
    public String getTitle() {
        return memo_topic_title;
    }

    @Override
    public int getType() {
        return ITopic.TYPE_MEMO;
    }

    @Override
    public int getColor() {
        return memo_topic_color;
    }
}
