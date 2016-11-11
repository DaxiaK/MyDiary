package com.kiminonawa.mydiary.contacts;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends FragmentActivity implements View.OnClickListener, SortTextLayout.CharacterClickListener {


    /**
     * getId
     */
    private long topicId;

    /**
     * UI
     */
    private SortTextLayout STL_contacts;

    /**
     * DB
     */
    private DBManager dbManager;
    /**
     * RecyclerView
     */
    private RecyclerView RecyclerView_contacts;
    private ContactsAdapter contactsAdapter;
    private List<ContactsEntity> contactsNamesList;
    private List<ContactsEntity> contactsSortedList;
    private LinearLayoutManager layoutManager;

    //character for sort
    private List<String> characterList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        topicId = getIntent().getLongExtra("topicId", -1);
        if (topicId == -1) {
            //TODO close this activity and show toast
        }
//        /**
//         * init UI
//         */
//        RL_memo_topbar_content = (RelativeLayout) findViewById(R.id.RL_memo_topbar_content);
//        RL_memo_topbar_content.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(this));
//
//        TV_memo_topbar_title = (TextView) findViewById(R.id.TV_memo_topbar_title);
//        IV_memo_edit = (ImageView) findViewById(R.id.IV_memo_edit);
//        IV_memo_edit.setOnClickListener(this);
//        String diaryTitle = getIntent().getStringExtra("diaryTitle");
//        if (diaryTitle == null) {
//            diaryTitle = "Memo";
//        }
//        TV_memo_topbar_title.setText(diaryTitle);

        STL_contacts = (SortTextLayout) findViewById(R.id.STL_contacts);
        RecyclerView_contacts = (RecyclerView) findViewById(R.id.RecyclerView_contacts);
        contactsNamesList = new ArrayList<>();
        dbManager = new DBManager(ContactsActivity.this);

        loadContacts();
        initTopicAdapter();
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

    @Override
    public void onClick(View v) {
        layoutManager.scrollToPositionWithOffset(contactsAdapter.getScrollPosition(character), 0);
    }

    @Override
    public void clickCharacter(String character) {

    }
}
