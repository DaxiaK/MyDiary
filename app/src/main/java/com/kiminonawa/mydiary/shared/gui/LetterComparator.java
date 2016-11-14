package com.kiminonawa.mydiary.shared.gui;

import com.kiminonawa.mydiary.contacts.ContactsEntity;

import java.util.Comparator;

/**
 * Created by daxia on 2016/11/13.
 */

public class LetterComparator implements Comparator<ContactsEntity> {

    @Override
    public int compare(ContactsEntity lhs, ContactsEntity rhs) {
        return lhs.getSortLetters().compareTo(rhs.getSortLetters());
    }
}
