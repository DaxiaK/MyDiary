package com.kiminonawa.mydiary.backup;

import java.util.List;

/**
 * Created by daxia on 2017/2/14.
 */

public interface IBUTopic  {


    String getTitle();

    int getType();

    int getColor();

    List getTopicContentItem();

    int getOrder();



}
