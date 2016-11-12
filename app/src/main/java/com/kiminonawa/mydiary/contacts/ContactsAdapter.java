package com.kiminonawa.mydiary.contacts;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.List;

/**
 * Created by daxia on 2016/10/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.TopicViewHolder> {


    private FragmentActivity mActivity;

    private ContactsDetailDialogFragment.ContactsDetailCallback callback;
    private long topicId;
    //Datalist
    private List<ContactsEntity> contactsNamesList;

    public ContactsAdapter(FragmentActivity activity, List<ContactsEntity> contactsNamesList, long topicId,
                           ContactsDetailDialogFragment.ContactsDetailCallback callback) {
        this.mActivity = activity;
        this.contactsNamesList = contactsNamesList;
        this.topicId = topicId;
        this.callback = callback;
    }


    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_contacts_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return contactsNamesList.size();
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, final int position) {
        holder.getTVName().setText(contactsNamesList.get(position).getName());
        holder.getTVPhoneNumber().setText(contactsNamesList.get(position).getPhoneNumber());
        holder.setItemPosition(position);
    }


    protected class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView IV_contacts_photo;
        private TextView TV_contacts_name;
        private TextView TV_contacts_phone_number;
        private View rootView;
        private int itemPosition;

        protected TopicViewHolder(View view) {
            super(view);
            this.rootView = view;
            this.rootView.setOnClickListener(this);
            this.rootView.setOnLongClickListener(this);

            this.IV_contacts_photo = (ImageView) rootView.findViewById(R.id.IV_contacts_photo);
            this.TV_contacts_name = (TextView) rootView.findViewById(R.id.TV_contacts_name);
            this.TV_contacts_phone_number = (TextView) rootView.findViewById(R.id.TV_contacts_phone_number);
            this.TV_contacts_name.setTextColor(ThemeManager.getInstance().getThemeMainColor(mActivity));
        }

        public ImageView getIVPhoto() {
            return IV_contacts_photo;
        }

        public TextView getTVName() {
            return TV_contacts_name;
        }

        public TextView getTVPhoneNumber() {
            return TV_contacts_phone_number;
        }

        protected View getRootView() {
            return rootView;
        }

        public void setItemPosition(int itemPosition) {
            this.itemPosition = itemPosition;
        }

        @Override
        public void onClick(View v) {
            CallDialogFragment callDialogFragment =
                    CallDialogFragment.newInstance(contactsNamesList.get(itemPosition).getName(),
                            contactsNamesList.get(itemPosition).getPhoneNumber());
            callDialogFragment.show(mActivity.getSupportFragmentManager(), "callDialogFragment");
        }

        @Override
        public boolean onLongClick(View v) {
            ContactsDetailDialogFragment contactsDetailDialogFragment =
                    ContactsDetailDialogFragment.newInstance(contactsNamesList.get(itemPosition).getContactsId(),
                            contactsNamesList.get(itemPosition).getName(), contactsNamesList.get(itemPosition).getPhoneNumber(),
                            topicId);
            contactsDetailDialogFragment.setCallBack(callback);
            contactsDetailDialogFragment.show(mActivity.getSupportFragmentManager(), "contactsDetailDialogFragment");
            return true;
        }
    }
}
