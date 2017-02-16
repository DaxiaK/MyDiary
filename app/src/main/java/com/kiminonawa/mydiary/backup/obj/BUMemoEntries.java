package com.kiminonawa.mydiary.backup.obj;

/**
 * Created by daxia on 2017/2/14.
 */

public class BUMemoEntries {


    private String memo_entries_content;
    private int memo_entries_order;
    private boolean checked;


    public BUMemoEntries(String memo_entries_content, int memo_entries_order, boolean checked) {
        this.memo_entries_content = memo_entries_content;
        this.memo_entries_order = memo_entries_order;
        this.checked = checked;
    }

    public int getMemoEntriesOrder() {
        return memo_entries_order;
    }

    public String getMemoEntriesContent() {
        return memo_entries_content;
    }


    public boolean isChecked() {
        return checked;
    }
}
