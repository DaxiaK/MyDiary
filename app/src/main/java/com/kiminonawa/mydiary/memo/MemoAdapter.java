package com.kiminonawa.mydiary.memo;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.EditMode;
import com.kiminonawa.mydiary.shared.ScreenHelper;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.marshalchen.ultimaterecyclerview.itemTouchHelper.ItemTouchHelperViewHolder;

import java.util.Collections;
import java.util.List;


/**
 * Created by daxia on 2016/10/17.
 */

public class MemoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements EditMode,
        ItemTouchHelperAdapter {


    //Data
    private List<MemoEntity> memoList;

    private FragmentActivity mActivity;
    private long topicId;
    private DBManager dbManager;
    private boolean isEditMode = false;
    private EditMemoDialogFragment.MemoCallback callback;
    private OnStartDragListener dragStartListener;


    public MemoAdapter(FragmentActivity activity, long topicId, List<MemoEntity> memoList,
                       DBManager dbManager, EditMemoDialogFragment.MemoCallback callback,
                       OnStartDragListener dragStartListener) {
        this.mActivity = activity;
        this.topicId = topicId;
        this.memoList = memoList;
        this.dbManager = dbManager;
        this.callback = callback;
        this.dragStartListener = dragStartListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_memo_item, parent, false);
        return new MemoViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }


    @Override
    public long getItemId(int position) {
        return memoList.get(position).getMemoId();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MemoViewHolder) {
            ((MemoViewHolder) holder).setItemPosition(position);
            ((MemoViewHolder) holder).initView();
            setMemoContent(((MemoViewHolder) holder), position);
        }
    }

    private void setMemoContent(MemoViewHolder holder, final int position) {
        if (memoList.get(position).isChecked()) {
            SpannableString spannableContent = new SpannableString(memoList.get(position).getContent());
            spannableContent.setSpan(new StrikethroughSpan(), 0, spannableContent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.getTVContent().setText(spannableContent);
            holder.getTVContent().setAlpha(0.4F);
        } else {
            holder.getTVContent().setText(memoList.get(position).getContent());
            holder.getTVContent().setAlpha(1F);
        }
    }


    @Override
    public boolean isEditMode() {
        return isEditMode;
    }

    @Override
    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    @Override
    public void onItemSwap(int position) {
        //Do nothing
    }

    @Override
    public void onItemMoveFinish() {
        //save the new order
        int orderNumber = memoList.size();
        dbManager.opeDB();
        dbManager.deleteAllCurrentMemoOrder(topicId);
        for (MemoEntity memoEntity : memoList) {
            dbManager.insertMemoOrder(topicId, memoEntity.getMemoId(), --orderNumber);
        }
        dbManager.closeDB();
        notifyDataSetChanged();
    }

    @Override
    public void onItemMove(int from, int to) {
        Collections.swap(memoList, from, to);
        notifyItemMoved(from, to);
    }

    private class MemoViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnTouchListener, ItemTouchHelperViewHolder {

        private View rootView;
        private ImageView IV_memo_item_dot;
        private TextView TV_memo_item_content;
        private ImageView IV_memo_item_delete;
        private RelativeLayout RL_memo_item_root_view;
        private int itemPosition;


        private MemoViewHolder(View view) {
            super(view);
            this.rootView = view;
            RL_memo_item_root_view = (RelativeLayout) rootView.findViewById(R.id.RL_memo_item_root_view);
            IV_memo_item_dot = (ImageView) rootView.findViewById(R.id.IV_memo_item_dot);
            TV_memo_item_content = (TextView) rootView.findViewById(R.id.TV_memo_item_content);
            IV_memo_item_delete = (ImageView) rootView.findViewById(R.id.IV_memo_item_delete);
            TV_memo_item_content.setTextColor(ThemeManager.getInstance().getThemeDarkColor(mActivity));
        }

        private TextView getTVContent() {
            return TV_memo_item_content;
        }


        private void setItemPosition(int itemPosition) {
            this.itemPosition = itemPosition;
        }

        private void initView() {
            if (isEditMode) {
                IV_memo_item_dot.setImageResource(R.drawable.ic_memo_swap_vert_black_24dp);
                ViewGroup.LayoutParams layoutParams = IV_memo_item_dot.getLayoutParams();
                layoutParams.width = layoutParams.height = ScreenHelper.dpToPixel(mActivity.getResources(), 24);
                IV_memo_item_delete.setVisibility(View.VISIBLE);
                IV_memo_item_dot.setOnTouchListener(this);
                IV_memo_item_delete.setOnClickListener(this);
                RL_memo_item_root_view.setOnClickListener(this);
            } else {
                IV_memo_item_dot.setImageResource(R.drawable.ic_memo_dot_24dp);
                ViewGroup.LayoutParams layoutParams = IV_memo_item_dot.getLayoutParams();
                layoutParams.width = layoutParams.height = ScreenHelper.dpToPixel(mActivity.getResources(), 10);
                IV_memo_item_delete.setVisibility(View.GONE);
                IV_memo_item_dot.setOnTouchListener(null);
                IV_memo_item_delete.setOnClickListener(null);
                RL_memo_item_root_view.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.IV_memo_item_delete:
                    dbManager.opeDB();
                    dbManager.delMemo(memoList.get(itemPosition).getMemoId());
                    dbManager.deleteMemoOrder(memoList.get(itemPosition).getMemoId());
                    dbManager.closeDB();
                    memoList.remove(itemPosition);
                    notifyDataSetChanged();
                    break;
                case R.id.RL_memo_item_root_view:
                    if (isEditMode) {
                        EditMemoDialogFragment editMemoDialogFragment = EditMemoDialogFragment.newInstance(
                                topicId, memoList.get(itemPosition).getMemoId(), false, memoList.get(itemPosition).getContent());
                        editMemoDialogFragment.show(mActivity.getSupportFragmentManager(), "editMemoDialogFragment");
                    } else {
                        memoList.get(itemPosition).toggleChecked();
                        dbManager.opeDB();
                        dbManager.updateMemoChecked(memoList.get(itemPosition).getMemoId(), memoList.get(itemPosition).isChecked());
                        dbManager.closeDB();
                        setMemoContent(this, itemPosition);
                    }
                    break;
            }
        }

        @Override
        public void onItemSelected() {
            RL_memo_item_root_view.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(mActivity));
        }

        @Override
        public void onItemClear() {
            RL_memo_item_root_view.setBackgroundColor(Color.WHITE);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(this);
            }
            return false;
        }
    }
}
