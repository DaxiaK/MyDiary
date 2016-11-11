package com.kiminonawa.mydiary.contacts;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends FragmentActivity {


    /**
     * getId
     */
    private long topicId;

    /**
     * UI
     */
    private ThemeManager themeManager;

    private LinearLayout LL_contacts_topbar_content;
    private TextView IV_contacts_title;
    private EditText EDT_main_topic_search;
    private SortTextLayout STL_contacts;
    private ImageView IV_contacts_add;

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

    //Datalist
    private List<ContactsEntity> contactsNamesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        themeManager = ThemeManager.getInstance();

        topicId = getIntent().getLongExtra("topicId", -1);
        if (topicId == -1) {
            //TODO close this activity and show toast
        }
        /**
         * init UI
         */
        LL_contacts_topbar_content = (LinearLayout) findViewById(R.id.LL_contacts_topbar_content);
        EDT_main_topic_search = (EditText) findViewById(R.id.EDT_main_topic_search);
        IV_contacts_add = (ImageView) findViewById(R.id.IV_contacts_add);

        IV_contacts_title = (TextView) findViewById(R.id.IV_contacts_title);
        String diaryTitle = getIntent().getStringExtra("diaryTitle");
        if (diaryTitle == null) {
            diaryTitle = "Contacts";
        }
        IV_contacts_title.setText(diaryTitle);


        /**
         * init RecyclerVie
         */
        STL_contacts = (SortTextLayout) findViewById(R.id.STL_contacts);
        RecyclerView_contacts = (RecyclerView) findViewById(R.id.RecyclerView_contacts);
        contactsNamesList = new ArrayList<>();
        dbManager = new DBManager(ContactsActivity.this);

        initTopbar();
        loadContacts();
        initTopicAdapter();
    }

    private void initTopbar(){
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
    }

    private void initTopicAdapter() {
        //Init topic adapter
        layoutManager = new LinearLayoutManager(this);
        RecyclerView_contacts.setLayoutManager(layoutManager);
        RecyclerView_contacts.setHasFixedSize(true);
        contactsAdapter = new ContactsAdapter(ContactsActivity.this, contactsNamesList);
        RecyclerView_contacts.setAdapter(contactsAdapter);
    }

}
