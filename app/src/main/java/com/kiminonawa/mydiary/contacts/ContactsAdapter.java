package com.kiminonawa.mydiary.contacts;

import android.content.Context;
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


    private Context mContext;


    //Datalist
    private List<ContactsEntity> contactsNamesList;

    public ContactsAdapter(Context context, List<ContactsEntity> contactsNamesList) {
        this.mContext = context;
        this.contactsNamesList = contactsNamesList;
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
    }


    protected class TopicViewHolder extends RecyclerView.ViewHolder {

        private ImageView IV_contacts_photo;
        private TextView TV_contacts_name;
        private TextView TV_contacts_phone_number;
        private View rootView;

        protected TopicViewHolder(View view) {
            super(view);
            this.rootView = view;
            this.IV_contacts_photo = (ImageView) rootView.findViewById(R.id.IV_contacts_photo);
            this.TV_contacts_name = (TextView) rootView.findViewById(R.id.TV_contacts_name);
            this.TV_contacts_phone_number = (TextView) rootView.findViewById(R.id.TV_contacts_phone_number);

            this.TV_contacts_name.setTextColor(ThemeManager.getInstance().getThemeMainColor(mContext));
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
    }
}
