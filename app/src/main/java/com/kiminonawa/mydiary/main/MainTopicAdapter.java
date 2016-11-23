package com.kiminonawa.mydiary.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.contacts.ContactsActivity;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.memo.MemoActivity;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.List;

/**
 * Created by daxia on 2016/10/17.
 */

public class MainTopicAdapter extends RecyclerView.Adapter<MainTopicAdapter.TopicViewHolder> implements MainActivity.ItemTouchHelperAdapter {


    private List<ITopic> topicList;
    private Context mContext;

    public MainTopicAdapter(Context context, List<ITopic> topicList) {
        this.mContext = context;
        this.topicList = topicList;
    }


    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_topic_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    @Override
    public void onBindViewHolder(TopicViewHolder holder, final int position) {

        holder.getRootView().setBackgroundResource(ThemeManager.getInstance().getTopicItemSelectResource());
        holder.getIconView().setImageResource(topicList.get(position).getIcon());
        holder.getTitleView().setText(topicList.get(position).getTitle());
        holder.getTVCount().setText(String.valueOf(topicList.get(position).getCount()));
        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (topicList.get(position).getType()) {
                    case ITopic.TYPE_CONTACTS:
                        Intent goContactsPageIntent = new Intent(mContext, ContactsActivity.class);
                        goContactsPageIntent.putExtra("topicId", topicList.get(position).getId());
                        goContactsPageIntent.putExtra("diaryTitle", topicList.get(position).getTitle());
                        mContext.startActivity(goContactsPageIntent);
                        break;
                    case ITopic.TYPE_DIARY:
                        Intent goEntriesPageIntent = new Intent(mContext, DiaryActivity.class);
                        goEntriesPageIntent.putExtra("topicId", topicList.get(position).getId());
                        goEntriesPageIntent.putExtra("diaryTitle", topicList.get(position).getTitle());
                        mContext.startActivity(goEntriesPageIntent);
                        break;
                    case ITopic.TYPE_MEMO:
                        Intent goMemoPageIntent = new Intent(mContext, MemoActivity.class);
                        goMemoPageIntent.putExtra("topicId", topicList.get(position).getId());
                        goMemoPageIntent.putExtra("diaryTitle", topicList.get(position).getTitle());
                        mContext.startActivity(goMemoPageIntent);
                        break;
                }
            }
        });
    }

    @Override
    public void onItemDismiss(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle(mContext.getString(R.string.topic_dialog_delete_title))
                .setMessage(String.format(mContext.getResources().getString(R.string.topic_dialog_delete_content), topicList.get(position).getTitle()))
                .setNegativeButton(mContext.getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notifyDataSetChanged();
                    }
                })
                .setPositiveButton(mContext.getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBManager dbManager = new DBManager(mContext);
                        dbManager.opeDB();
                        switch (topicList.get(position).getType()) {
                            case ITopic.TYPE_CONTACTS:
                                dbManager.delAllContactsInTopic(topicList.get(position).getId());
                                break;
                            case ITopic.TYPE_DIARY:
                                //Because FOREIGN key is not work in this version,
                                //so delete diaryitem first , then delete diary
                                Cursor diaryCursor = dbManager.selectDiaryList(topicList.get(position).getId());
                                for (int i = 0; i < diaryCursor.getCount(); i++) {
                                    dbManager.delAllDiaryItemByDiaryId(diaryCursor.getLong(0));
                                    diaryCursor.moveToNext();
                                }
                                diaryCursor.close();
                                dbManager.delAllDiaryInTopic(topicList.get(position).getId());
                                break;
                            case ITopic.TYPE_MEMO:
                                dbManager.delAllMemoInTopic(topicList.get(position).getId());
                                break;
                        }
                        dbManager.delTopic(topicList.get(position).getId());
                        dbManager.closeDB();
                        topicList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                    }
                });
        builder.show();
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
