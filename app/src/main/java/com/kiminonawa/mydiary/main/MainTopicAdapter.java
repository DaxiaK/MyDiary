package com.kiminonawa.mydiary.main;

import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.annotation.DraggableItemStateFlags;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.contacts.ContactsActivity;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.memo.MemoActivity;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.ViewTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daxia on 2016/10/17.
 */

public class MainTopicAdapter extends RecyclerView.Adapter<MainTopicAdapter.TopicViewHolder> implements
        Filterable, DraggableItemAdapter<MainTopicAdapter.TopicViewHolder>,
        SwipeableItemAdapter<MainTopicAdapter.TopicViewHolder> {


    private List<ITopic> originalTopicList;
    private List<ITopic> filteredTopicList;
    private MainActivity activity;
    private TopicFilter topicFilter;
    private DBManager dbManager;

    public MainTopicAdapter(MainActivity activity, List<ITopic> topicList, DBManager dbManager) {
        this.activity = activity;
        this.dbManager = dbManager;

        this.originalTopicList = topicList;
        this.filteredTopicList = new ArrayList<>();

        topicFilter = new TopicFilter(this, originalTopicList);

        // MainTopicAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
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
    public long getItemId(int position) {
        return filteredTopicList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return filteredTopicList.size();
    }

    @Override
    public void onBindViewHolder(final TopicViewHolder holder, final int position) {

        holder.getSwipeableContainerView().setBackground(ThemeManager.getInstance().getTopicItemSelectDrawable(activity));
        holder.getTopicLeftSettingView().setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(activity));
        holder.getIconView().setImageResource(filteredTopicList.get(position).getIcon());
        holder.getIconView().setColorFilter(filteredTopicList.get(position).getColor());
        holder.getTitleView().setText(filteredTopicList.get(position).getTitle());
        holder.getTitleView().setTextColor(filteredTopicList.get(position).getColor());
        holder.getTVCount().setText(String.valueOf(filteredTopicList.get(position).getCount()));
        holder.getTVCount().setTextColor(filteredTopicList.get(position).getColor());
        holder.getArrow().setColorFilter(filteredTopicList.get(position).getColor());

        // set swiping properties
        holder.setMaxRightSwipeAmount(0.3f);
        holder.setMaxLeftSwipeAmount(0);
        holder.setSwipeItemHorizontalSlideAmount(filteredTopicList.get(position).isPinned() ? 0.3f : 0);

        //Click event
        holder.getRLTopic().setOnClickListener(new View.OnClickListener() {
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
    public Filter getFilter() {
        return topicFilter;
    }

    /*
     * Swipe
     */

    @Override
    public int onGetSwipeReactionType(TopicViewHolder holder, int position, int x, int y) {
        if (ViewTools.hitTest(holder.getSwipeableContainerView(), x, y)) {
            return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
        } else {
            return SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H;
        }
    }

    @Override
    public void onSetSwipeBackground(TopicViewHolder holder, int position, int type) {
        if (type == SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND) {
            holder.getTopicLeftSettingView().setVisibility(View.GONE);
        } else {
            holder.getTopicLeftSettingView().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public SwipeResultAction onSwipeItem(TopicViewHolder holder, int position, int result) {
        switch (result) {
            // swipe right --- pin
            case SwipeableItemConstants.RESULT_SWIPED_RIGHT:
                return new SwipeRightResultAction(this, position);
            // other --- do nothing
            case SwipeableItemConstants.RESULT_SWIPED_LEFT:
            case SwipeableItemConstants.RESULT_CANCELED:
            default:
                if (position != RecyclerView.NO_POSITION) {
                    return new UnpinResultAction(this, position);
                } else {
                    return null;
                }
        }
    }

    @Override
    public boolean onCheckCanStartDrag(TopicViewHolder holder, int position, int x, int y) {

        // x, y --- relative from the itemView's top-left
        final View containerView = holder.getSwipeableContainerView();

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return !topicFilter.isFilter() && ViewTools.hitTest(containerView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(TopicViewHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        //modify the original list
        final ITopic originalItem = originalTopicList.remove(fromPosition);
        originalTopicList.add(toPosition, originalItem);

        //Modify the filter list
        final ITopic filteredItem = filteredTopicList.remove(fromPosition);
        filteredTopicList.add(toPosition, filteredItem);

        //save the new topic order
        int orderNumber = originalTopicList.size();
        dbManager.opeDB();
        dbManager.deleteAllCurrentTopicOrder();
        for (ITopic topic : originalTopicList) {
            dbManager.insertTopicOrder(topic.getId(), --orderNumber);
        }
        dbManager.closeDB();
        notifyDataSetChanged(false);
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    private static class SwipeRightResultAction extends SwipeResultActionMoveToSwipedDirection {
        private MainTopicAdapter mAdapter;
        private final int mPosition;

        public SwipeRightResultAction(MainTopicAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {

            super.onPerformAction();

            ITopic item = mAdapter.getList().get(mPosition);

            if (!item.isPinned()) {
                item.setPinned(true);
                mAdapter.notifyItemChanged(mPosition);
            }
        }

        @Override
        protected void onSlideAnimationEnd() {

            super.onSlideAnimationEnd();
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class UnpinResultAction extends SwipeResultActionDefault {
        private MainTopicAdapter mAdapter;
        private final int mPosition;

        UnpinResultAction(MainTopicAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            ITopic item = mAdapter.getList().get(mPosition);
            if (item.isPinned()) {
                item.setPinned(false);
                mAdapter.notifyItemChanged(mPosition);
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }


    protected class TopicViewHolder extends AbstractSwipeableItemViewHolder implements DraggableItemViewHolder {

        @DraggableItemStateFlags
        private int mDragStateFlags;

        private ImageView IV_topic_icon;
        private TextView TV_topic_title;
        private TextView TV_topic_count;
        private ImageView IV_topic_arrow_right;
        private RelativeLayout RL_topic_view;
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
            this.RL_topic_view = (RelativeLayout) rootView.findViewById(R.id.RL_topic_view);
            this.LL_topic_left_setting = (LinearLayout) rootView.findViewById(R.id.LL_topic_left_setting);
            this.IV_topic_left_setting_edit = (ImageView) rootView.findViewById(R.id.IV_topic_left_setting_edit);
            this.IV_topic_left_setting_delete = (ImageView) rootView.findViewById(R.id.IV_topic_left_setting_delete);


        }

        @Override
        public View getSwipeableContainerView() {
            return RL_topic_content;
        }

        @Override
        public void setDragStateFlags(@DraggableItemStateFlags int flags) {
            mDragStateFlags = flags;
        }

        @Override
        @DraggableItemStateFlags
        public int getDragStateFlags() {
            return mDragStateFlags;
        }

        protected ImageView getIconView() {
            return IV_topic_icon;
        }


        protected RelativeLayout getRLTopic() {
            return RL_topic_view;
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
