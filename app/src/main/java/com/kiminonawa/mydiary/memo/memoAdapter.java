package com.kiminonawa.mydiary.memo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.main.topic.ITopic;

import java.util.List;

/**
 * Created by daxia on 2016/10/17.
 */

public class memoAdapter extends RecyclerView.Adapter<memoAdapter.MemoViewHolder>  {


    private List<ITopic> topicList;
    private Context mContext;

    public memoAdapter(Context context, List<ITopic> topicList) {
        this.mContext = context;
        this.topicList = topicList;
    }


    @Override
    public MemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_topic_item, parent, false);
        return new MemoViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    @Override
    public void onBindViewHolder(MemoViewHolder holder, final int position) {

        if (topicList.get(position).getType() == ITopic.TYPE_CONTACTS) {
            //Alpha 125 , disable color
            holder.getRootView().setBackgroundColor(Color.parseColor("#7D9D9FA2"));
        } else {
            holder.getRootView().setBackgroundColor(Color.WHITE);
        }

        holder.getIconView().setImageResource(topicList.get(position).getIcon());
        holder.getTitleView().setText(topicList.get(position).getTitle());
        holder.getTVCount().setText(String.valueOf(topicList.get(position).getCount()));
        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (topicList.get(position).getType()) {
                    case ITopic.TYPE_DIARY:
                        Intent goEntriesPageIntent = new Intent(mContext, DiaryActivity.class);
                        //Send topicId for memo & entries
                        goEntriesPageIntent.putExtra("topicId", topicList.get(position).getId());
                        goEntriesPageIntent.putExtra("diaryTitle", topicList.get(position).getTitle());
                        mContext.startActivity(goEntriesPageIntent);
                        break;
                    case ITopic.TYPE_MEMO:
                        Intent goMemoPageIntent = new Intent(mContext, MemoActivity.class);
                        //Send topicId for memo & entries
                        goMemoPageIntent.putExtra("topicId", topicList.get(position).getId());
                        goMemoPageIntent.putExtra("diaryTitle", topicList.get(position).getTitle());
                        mContext.startActivity(goMemoPageIntent);
                        break;
                }
            }
        });
    }


    protected class MemoViewHolder extends RecyclerView.ViewHolder {

        private ImageView IV_topic_icon;
        private TextView TV_topic_title;
        private TextView TV_topic_count;
        private View rootView;

        protected MemoViewHolder(View view) {
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
