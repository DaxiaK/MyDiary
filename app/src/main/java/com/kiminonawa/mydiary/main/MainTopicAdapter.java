package com.kiminonawa.mydiary.main;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.contacts.ContactsActivity;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.main.itemhelper.ItemTouchHelperWithFilterAdapter;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.memo.MemoActivity;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by daxia on 2016/10/17.
 */

public class MainTopicAdapter extends RecyclerView.Adapter<MainTopicAdapter.TopicViewHolder> implements
        ItemTouchHelperWithFilterAdapter, Filterable {


    private List<ITopic> originalTopicList;
    private List<ITopic> filteredTopicList;
    private MainActivity activity;
    private DBManager dbManager;
    private TopicFilter topicFilter;


    public MainTopicAdapter(MainActivity activity, List<ITopic> topicList, DBManager dbManager) {
        this.activity = activity;
        this.dbManager = dbManager;

        originalTopicList = topicList;
        filteredTopicList = new ArrayList<>();

        topicFilter = new TopicFilter(this, originalTopicList);
    }


    @Override
    public boolean isFilter() {
        return topicFilter.isFilter();
    }

    public void notifyDataSetChanged(boolean clear) {
        if (clear) {
            filteredTopicList.clear();
            filteredTopicList.addAll(originalTopicList);
        }
        super.notifyDataSetChanged();
    }

    public List<ITopic> getList() {
        return filteredTopicList;
    }

    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_topic_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return filteredTopicList.size();
    }

    @Override
    public void onBindViewHolder(final TopicViewHolder holder, final int position) {

        holder.getContentView().setBackground(ThemeManager.getInstance().getTopicItemSelectDrawable(activity));
        holder.getTopicLeftSettingView().setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(activity));
        holder.getIconView().setImageResource(filteredTopicList.get(position).getIcon());
        holder.getIconView().setColorFilter(filteredTopicList.get(position).getColor());
        holder.getTitleView().setText(filteredTopicList.get(position).getTitle());
        holder.getTitleView().setTextColor(filteredTopicList.get(position).getColor());
        holder.getTVCount().setText(String.valueOf(filteredTopicList.get(position).getCount()));
        holder.getTVCount().setTextColor(filteredTopicList.get(position).getColor());
        holder.getArrow().setColorFilter(filteredTopicList.get(position).getColor());

        //Revert the left view
        holder.getDMJSLView().close();
        holder.getDMJSLView().getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTopic(filteredTopicList.get(position).getType(), position);
            }
        });
        holder.getTopicLeftSettingEditView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopicDetailDialogFragment createTopicDialogFragment =
                        TopicDetailDialogFragment.newInstance(true, position, filteredTopicList.get(position).getId(),
                                filteredTopicList.get(position).getTitle(), filteredTopicList.get(position).getType(),
                                filteredTopicList.get(position).getColor());
                createTopicDialogFragment.show(activity.getSupportFragmentManager(),
                        "createTopicDialogFragment");
            }
        });

        holder.getTopicLeftSettingDeleteView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopicDeleteDialogFragment topicDeleteDialogFragment =
                        TopicDeleteDialogFragment.newInstance(position, filteredTopicList.get(position).getTitle());
                topicDeleteDialogFragment.show(activity.getSupportFragmentManager(), "topicDeleteDialogFragment");
            }
        });
    }

    public void gotoTopic(final int type, final int position) {
        switch (type) {
            case ITopic.TYPE_CONTACTS:
                Intent goContactsPageIntent = new Intent(activity, ContactsActivity.class);
                goContactsPageIntent.putExtra("topicId", filteredTopicList.get(position).getId());
                goContactsPageIntent.putExtra("diaryTitle", filteredTopicList.get(position).getTitle());
                activity.startActivity(goContactsPageIntent);
                break;
            case ITopic.TYPE_DIARY:
                Intent goEntriesPageIntent = new Intent(activity, DiaryActivity.class);
                goEntriesPageIntent.putExtra("topicId", filteredTopicList.get(position).getId());
                goEntriesPageIntent.putExtra("diaryTitle", filteredTopicList.get(position).getTitle());
                goEntriesPageIntent.putExtra("has_entries", true);
                activity.startActivity(goEntriesPageIntent);
                break;
            case ITopic.TYPE_MEMO:
                Intent goMemoPageIntent = new Intent(activity, MemoActivity.class);
                goMemoPageIntent.putExtra("topicId", filteredTopicList.get(position).getId());
                goMemoPageIntent.putExtra("diaryTitle", filteredTopicList.get(position).getTitle());
                activity.startActivity(goMemoPageIntent);
                break;
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(filteredTopicList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwap(int position) {
        //Do nothing
    }

    @Override
    public void onItemMoveFinish() {
        //save the new topic order
        int orderNumber = filteredTopicList.size();
        dbManager.opeDB();
        dbManager.deleteAllCurrentTopicOrder();
        for (ITopic topic : filteredTopicList) {
            dbManager.insertTopicOrder(topic.getId(), --orderNumber);
        }
        dbManager.closeDB();
        notifyDataSetChanged(false);
    }

    @Override
    public Filter getFilter() {
        return topicFilter;
    }


    protected class TopicViewHolder extends RecyclerView.ViewHolder {

        private ImageView IV_topic_icon;
        private TextView TV_topic_title;
        private TextView TV_topic_count;
        private ImageView IV_topic_arrow_right;
        private SwipeLayout DMJSL_topic;
        private LinearLayout LL_topic_left_setting;
        private RelativeLayout RL_topic_content;
        private ImageView IV_topic_left_setting_edit, IV_topic_left_setting_delete;

        protected TopicViewHolder(View rootView) {
            super(rootView);
            this.RL_topic_content = (RelativeLayout) rootView.findViewById(R.id.RL_topic_content);
            this.IV_topic_icon = (ImageView) rootView.findViewById(R.id.IV_topic_icon);
            this.TV_topic_title = (TextView) rootView.findViewById(R.id.TV_topic_title);
            this.TV_topic_count = (TextView) rootView.findViewById(R.id.TV_topic_count);
            this.IV_topic_arrow_right = (ImageView) rootView.findViewById(R.id.IV_topic_arrow_right);

            //Left setting view
            this.DMJSL_topic = (SwipeLayout) rootView.findViewById(R.id.DMJSL_topic);
            this.LL_topic_left_setting = (LinearLayout) rootView.findViewById(R.id.LL_topic_left_setting);
            this.IV_topic_left_setting_edit = (ImageView) rootView.findViewById(R.id.IV_topic_left_setting_edit);
            this.IV_topic_left_setting_delete = (ImageView) rootView.findViewById(R.id.IV_topic_left_setting_delete);

            this.DMJSL_topic.setRightSwipeEnabled(false);
            this.DMJSL_topic.setShowMode(SwipeLayout.ShowMode.PullOut);
            this.DMJSL_topic.setDrag(SwipeLayout.DragEdge.Left, LL_topic_left_setting);

        }

        protected ImageView getIconView() {
            return IV_topic_icon;
        }


        protected SwipeLayout getDMJSLView() {
            return DMJSL_topic;
        }

        protected TextView getTitleView() {
            return TV_topic_title;
        }

        protected TextView getTVCount() {
            return TV_topic_count;
        }

        protected ImageView getArrow() {
            return IV_topic_arrow_right;
        }

        protected View getContentView() {
            return RL_topic_content;
        }

        protected View getTopicLeftSettingView() {
            return LL_topic_left_setting;
        }

        protected View getTopicLeftSettingEditView() {
            return IV_topic_left_setting_edit;
        }

        protected View getTopicLeftSettingDeleteView() {
            return IV_topic_left_setting_delete;
        }
    }

    private static class TopicFilter extends Filter {

        private final MainTopicAdapter adapter;

        private final List<ITopic> originalList;

        private final List<ITopic> filteredList;

        private boolean isFilter = false;

        public boolean isFilter() {
            return isFilter;
        }

        public void setFilter(boolean filter) {
            isFilter = filter;
        }

        private TopicFilter(MainTopicAdapter adapter, List<ITopic> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
                isFilter = false;
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (final ITopic topic : originalList) {
                    if (topic.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(topic);
                    }
                }
                Log.e("test", "performFiltering =" + filteredList.size());
                isFilter = true;
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredTopicList.clear();
            adapter.filteredTopicList.addAll((ArrayList<ITopic>) results.values);
            adapter.notifyDataSetChanged(false);
        }
    }
}
