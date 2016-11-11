package com.kiminonawa.mydiary.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;

import java.util.List;

/**
 * Created by daxia on 2016/10/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.TopicViewHolder> {


    private List<ContactsEntity> contactsList;
    private Context mContext;

    public ContactsAdapter(Context context, List<ContactsEntity> contactsList) {
        this.mContext = context;
        this.contactsList = contactsList;
    }


    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_topic_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, final int position) {


    }


    protected class TopicViewHolder extends RecyclerView.ViewHolder {

        private ImageView IV_topic_icon;
        private TextView TV_topic_title;
        private TextView TV_topic_count;
        private View rootView;

        protected TopicViewHolder(View view) {
            super(view);
            this.rootView = view;
            this.IV_topic_icon = (ImageView) rootView.findViewById(R.id.IV_topic_icon);
            this.TV_topic_title = (TextView) rootView.findViewById(R.id.TV_topic_title);
            this.TV_topic_count = (TextView) rootView.findViewById(R.id.TV_topic_count);
        }

        protected ImageView getIconView() {
            return IV_topic_icon;
        }

        protected TextView getTitleView() {
            return TV_topic_title;
        }

        public TextView getTVCount() {
            return TV_topic_count;
        }

        protected View getRootView() {
            return rootView;
        }
    }
}
