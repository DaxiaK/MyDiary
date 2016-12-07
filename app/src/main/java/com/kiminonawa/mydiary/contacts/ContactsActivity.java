package com.kiminonawa.mydiary.contacts;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.gui.LetterComparator;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactsActivity extends FragmentActivity implements View.OnClickListener,
        ContactsDetailDialogFragment.ContactsDetailCallback, LatterSortLayout.OnTouchingLetterChangedListener {


    /**
     * getId
     */
    private long topicId;

    /**
     * UI
     */
    private ThemeManager themeManager;

    private RelativeLayout RL_contacts_content;
    private TextView IV_contacts_title;
    private EditText EDT_main_topic_search;
    private LatterSortLayout STL_contacts;
    private ImageView IV_contacts_add;
    private TextView TV_contact_short_sort;


    /**
     * DB
     */
    private DBManager dbManager;
    /**
     * RecyclerView
     */
    private RecyclerView RecyclerView_contacts;
    private ContactsAdapter contactsAdapter;
    private LinearLayoutManager layoutManager;

    //Contacts list from DB
    private List<ContactsEntity> contactsNamesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        themeManager = ThemeManager.getInstance();

        topicId = getIntent().getLongExtra("topicId", -1);
        if (topicId == -1) {
            finish();
        }
        /**
         * init UI
         */
        RL_contacts_content = (RelativeLayout) findViewById(R.id.RL_contacts_content);
        RL_contacts_content.setBackground(themeManager.getContactsBgDrawable(this, topicId));

        TV_contact_short_sort = (TextView) findViewById(R.id.TV_contact_short_sort);
        TV_contact_short_sort.setBackgroundColor(themeManager.getThemeDarkColor(ContactsActivity.this));

        STL_contacts = (LatterSortLayout) findViewById(R.id.STL_contacts);
        STL_contacts.setSortTextView(TV_contact_short_sort);
        STL_contacts.setOnTouchingLetterChangedListener(this);

        EDT_main_topic_search = (EditText) findViewById(R.id.EDT_main_topic_search);
        IV_contacts_add = (ImageView) findViewById(R.id.IV_contacts_add);
        IV_contacts_add.setOnClickListener(this);

        IV_contacts_title = (TextView) findViewById(R.id.IV_contacts_title);
        String diaryTitle = getIntent().getStringExtra("diaryTitle");
        if (diaryTitle == null) {
            diaryTitle = "Contacts";
        }
        IV_contacts_title.setText(diaryTitle);


        /**
         * init RecyclerVie
         */
        STL_contacts = (LatterSortLayout) findViewById(R.id.STL_contacts);
        RecyclerView_contacts = (RecyclerView) findViewById(R.id.RecyclerView_contacts);
        contactsNamesList = new ArrayList<>();
        dbManager = new DBManager(ContactsActivity.this);

        initTopbar();
        loadContacts();
        initTopicAdapter();
    }

    private void initTopbar() {
        EDT_main_topic_search.getBackground().setColorFilter(themeManager.getThemeMainColor(this),
                PorterDuff.Mode.SRC_ATOP);
        IV_contacts_title.setTextColor(themeManager.getThemeMainColor(this));
        IV_contacts_add.setColorFilter(themeManager.getThemeMainColor(this));
    }

    private void loadContacts() {
        contactsNamesList.clear();
        dbManager.opeDB();
        Cursor contactsCursor = dbManager.selectContacts(topicId);
        for (int i = 0; i < contactsCursor.getCount(); i++) {
            contactsNamesList.add(
                    new ContactsEntity(contactsCursor.getLong(0), contactsCursor.getString(1),
                            contactsCursor.getString(2), contactsCursor.getString(3)));
            contactsCursor.moveToNext();
        }
        contactsCursor.close();
        dbManager.closeDB();
        sortContacts();
    }

    private void sortContacts() {
        for (ContactsEntity contactsEntity : contactsNamesList) {
            String sortString = contactsEntity.getName().substring(0, 1).toUpperCase();
            if (sortString.matches("[\\u4E00-\\u9FA5]")) {
                String[] arr = PinyinHelper.toHanyuPinyinStringArray(sortString.trim().charAt(0));
                sortString = arr[0].substring(0, 1).toUpperCase();
            }
            if (sortString.matches("[A-Z]")) {
                contactsEntity.setSortLetters(sortString.toUpperCase());
            } else {
                contactsEntity.setSortLetters("#");
            }
        }
        Collections.sort(contactsNamesList, new LetterComparator());
    }

    private void initTopicAdapter() {
        //Init topic adapter
        layoutManager = new LinearLayoutManager(this);
        RecyclerView_contacts.setLayoutManager(layoutManager);
        RecyclerView_contacts.setHasFixedSize(true);
        contactsAdapter = new ContactsAdapter(ContactsActivity.this, contactsNamesList, topicId, this);
        RecyclerView_contacts.setAdapter(contactsAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_contacts_add:
                ContactsDetailDialogFragment contactsDetailDialogFragment =
                        ContactsDetailDialogFragment.newInstance(ContactsDetailDialogFragment.ADD_NEW_CONTACTS,
                                "", "", topicId);
                contactsDetailDialogFragment.setCallBack(this);
                contactsDetailDialogFragment.show(getSupportFragmentManager(), "contactsDetailDialogFragment");
                break;
        }
    }

    @Override
    public void addContacts() {
        loadContacts();
        contactsAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateContacts() {
        loadContacts();
        contactsAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteContacts() {
        loadContacts();
        contactsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = contactsAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            RecyclerView_contacts.getLayoutManager().scrollToPosition(position);
        }
    }
}
