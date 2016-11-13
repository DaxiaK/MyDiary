package com.kiminonawa.mydiary.contacts;

/**
 * Created by daxia on 2016/11/7.
 */
public class ContactsEntity {

    private long contactsId;
    private String name;
    private String phoneNumber;
    private String photo;
    private String sortLetters;


    public ContactsEntity(long contactsId, String name, String phoneNumber, String photo) {
        this.contactsId = contactsId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
    }

    public long getContactsId() {
        return contactsId;
    }


    public String getName() {
        return name;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

}


