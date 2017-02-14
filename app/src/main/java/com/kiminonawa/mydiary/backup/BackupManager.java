package com.kiminonawa.mydiary.backup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public class BackupManager {

    private List<IBUTopic> buTopicList;


    public BackupManager() {
        buTopicList = new ArrayList<>();
    }


    public void addTopic(IBUTopic topic) {
        buTopicList.add(topic);
    }

    public IBUTopic getTopic(int position) {
        return buTopicList.get(position);
    }

}

