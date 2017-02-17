package com.kiminonawa.mydiary.backup.obj;

/**
 * Created by daxia on 2017/2/14.
 */

public class BUContactsEntries {

    private long Contacts_entries_id;
    private String Contacts_entries_name;
    private String Contacts_entries_phonenumber;

    public BUContactsEntries(long contacts_entries_id, String contacts_entries_name, String contacts_entries_phonenumber) {
        Contacts_entries_id = contacts_entries_id;
        Contacts_entries_name = contacts_entries_name;
        Contacts_entries_phonenumber = contacts_entries_phonenumber;
    }

    public long getContactsEntriesId() {
        return Contacts_entries_id;
    }

    public String getContactsEntriesName() {
        return Contacts_entries_name;
    }

    public String getContactsEntriesPhonenumber() {
        return Contacts_entries_phonenumber;
    }
}
