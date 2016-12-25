package com.kiminonawa.mydiary.contacts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;


/**
 * Created by daxia on 2016/8/27.
 */
public class ContactsDetailDialogFragment extends DialogFragment implements View.OnClickListener {


    public interface ContactsDetailCallback {
        void addContacts();

        void updateContacts();

        void deleteContacts();
    }

    /**
     * UI
     */
    private LinearLayout LL_contacts_detail_top_content;
    private EditText EDT_contacts_detail_name, EDT_contacts_detail_phone_number;
    private MyDiaryButton But_contacts_detail_delete, But_contacts_detail_cancel, But_contacts_detail_ok;

    /**
     * CallBack
     */
    private ContactsDetailCallback callback;

    /**
     * Contacts Info
     */
    private long contactsId;
    private String contactsName, contactsPhoneNumber;
    private long topicId;

    //Edit or add contacts
    private boolean isEditMode = false;
    public static final long ADD_NEW_CONTACTS = -1;


    public static ContactsDetailDialogFragment newInstance(long contactsId,
                                                           String contactsName, String contactsPhoneNumber, long topicId) {
        Bundle args = new Bundle();
        ContactsDetailDialogFragment fragment = new ContactsDetailDialogFragment();
        //contactsId = -1 is edit
        args.putLong("contactsId", contactsId);
        args.putString("contactsName", contactsName);
        args.putString("contactsPhoneNumber", contactsPhoneNumber);
        args.putLong("topicId", topicId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (ContactsDetailCallback) context;
        } catch (ClassCastException e) {
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        View rootView = inflater.inflate(R.layout.dialog_fragment_contacts_detail, container);

        LL_contacts_detail_top_content = (LinearLayout) rootView.findViewById(R.id.LL_contacts_detail_top_content);
        LL_contacts_detail_top_content.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        EDT_contacts_detail_name = (EditText) rootView.findViewById(R.id.EDT_contacts_detail_name);
        EDT_contacts_detail_phone_number = (EditText) rootView.findViewById(R.id.EDT_contacts_detail_phone_number);


        But_contacts_detail_delete = (MyDiaryButton) rootView.findViewById(R.id.But_contacts_detail_delete);
        But_contacts_detail_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_contacts_detail_cancel);
        But_contacts_detail_cancel.setOnClickListener(this);
        But_contacts_detail_ok = (MyDiaryButton) rootView.findViewById(R.id.But_contacts_detail_ok);
        But_contacts_detail_ok.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactsId = getArguments().getLong("contactsId", -1);
        if (contactsId == ADD_NEW_CONTACTS) {
            isEditMode = false;
            But_contacts_detail_delete.setVisibility(View.GONE);

            topicId = getArguments().getLong("topicId", -1);
        } else {
            isEditMode = true;
            But_contacts_detail_delete.setVisibility(View.VISIBLE);
            But_contacts_detail_delete.setOnClickListener(this);

            contactsName = getArguments().getString("contactsName", "");
            contactsPhoneNumber = getArguments().getString("contactsPhoneNumber", "");
            EDT_contacts_detail_name.setText(contactsName);
            EDT_contacts_detail_phone_number.setText(contactsPhoneNumber);
        }
    }



    private void addContacts() {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        dbManager.insertContacts(EDT_contacts_detail_name.getText().toString(),
                EDT_contacts_detail_phone_number.getText().toString(), "", topicId);
        dbManager.closeDB();
    }


    private void updateContacts() {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        dbManager.updateContacts(contactsId,
                EDT_contacts_detail_name.getText().toString(), EDT_contacts_detail_phone_number.getText().toString(), "");
        dbManager.closeDB();
    }


    private void deleteContacts() {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        dbManager.delContacts(contactsId);
        dbManager.closeDB();
    }

    private void buttonOkEvent() {
        if (EDT_contacts_detail_name.getText().toString().length() > 0
                && EDT_contacts_detail_phone_number.getText().toString().length() > 0) {
            if (isEditMode) {
                updateContacts();
                callback.updateContacts();
            } else {
                addContacts();
                callback.addContacts();
            }
            dismiss();
        } else {
            Toast.makeText(getActivity(), getString(R.string.toast_contacts_empty), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_contacts_detail_delete:
                deleteContacts();
                callback.deleteContacts();
                dismiss();
                break;
            case R.id.But_contacts_detail_cancel:
                dismiss();
                break;
            case R.id.But_contacts_detail_ok:
                buttonOkEvent();
                break;
        }
    }


}
