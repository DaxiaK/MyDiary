package com.kiminonawa.mydiary.backup;

import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public interface IBUTopic {


    String getTitle();

    int getOrder();

    int getColor();

    int getType();

    List getTopicContentItem();


}
