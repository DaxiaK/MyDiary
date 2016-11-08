package com.kiminonawa.mydiary.memo;

/**
 * Created by daxia on 2016/11/7.
 */
public class MemoEntity {

    private long memoId;
    private String content;
    private boolean isChecked;

    public MemoEntity(long memoId, String content, boolean isChecked) {
        this.memoId = memoId;
        this.content = content;
        this.isChecked = isChecked;
    }

    public long getMemoId() {
        return memoId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void toggleChecked() {
        isChecked = !isChecked;
    }
}
