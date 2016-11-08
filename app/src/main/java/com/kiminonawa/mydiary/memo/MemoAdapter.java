package com.kiminonawa.mydiary.memo;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.EditMode;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.List;


/**
 * Created by daxia on 2016/10/17.
 */

public class MemoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements EditMode {


    //Data
    private List<MemoEntity> memoList;

    private FragmentActivity mActivity;
    private long topicId;
    private DBManager dbManager;
    private boolean isEditMode = false;
    private EditMemoDialogFragment.MemoCallback callback;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public MemoAdapter(FragmentActivity activity, long topicId, List<MemoEntity> memoList, DBManager dbManager, EditMemoDialogFragment.MemoCallback callback) {
        this.mActivity = activity;
        this.topicId = topicId;
        this.memoList = memoList;
        this.dbManager = dbManager;
        this.callback = callback;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (isEditMode) {
            if (viewType == TYPE_HEADER) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_memo_item_add_header, parent, false);
                return new MemoViewHeader(view);
            } else if (viewType == TYPE_ITEM) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_memo_item, parent, false);
                return new MemoViewHolder(view);
            }
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_memo_item, parent, false);
            return new MemoViewHolder(view);
        }
        throw new RuntimeException("Can't find view");
    }

    @Override
    public int getItemCount() {
        int size;
        if (isEditMode) {
            size = memoList.size() + 1;
        } else {
            size = memoList.size();
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (isEditMode) {
            if (isPositionHeader(position)) {
                return TYPE_HEADER;
            } else {
                return TYPE_ITEM;
            }
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MemoViewHeader) {

        } else if (holder instanceof MemoViewHolder) {
            ((MemoViewHolder) holder).setItemPosition(getShiftPosition(position));
            setMemoContent(((MemoViewHolder) holder), getShiftPosition(position));
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

    public int getShiftPosition(int position) {
        if (isEditMode) {
            return position - 1;
        } else {
            return position;
        }
    }

    protected class MemoViewHeader extends RecyclerView.ViewHolder {
        private View rootView;
        private TextView TV_memo_item_add;


        public MemoViewHeader(View view) {
            super(view);
            this.rootView = view;
            TV_memo_item_add = (TextView) rootView.findViewById(R.id.TV_memo_item_add);
            TV_memo_item_add.setTextColor(ThemeManager.getInstance().getThemeDarkColor(mActivity));
            TV_memo_item_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditMemoDialogFragment editMemoDialogFragment = EditMemoDialogFragment.newInstance(
                            topicId, -1, true, "");
                    editMemoDialogFragment.setCallBack(callback);
                    editMemoDialogFragment.show(mActivity.getSupportFragmentManager(), "editMemoDialogFragment");
                }
            });
        }
    }

    protected class MemoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View rootView;
        private TextView TV_memo_item_content;
        private ImageView IV_memo_item_delete;
        private RelativeLayout RL_memo_item_root_view;
        private int itemPosition;


        protected MemoViewHolder(View view) {
            super(view);
            this.rootView = view;
            RL_memo_item_root_view = (RelativeLayout) rootView.findViewById(R.id.RL_memo_item_root_view);
            TV_memo_item_content = (TextView) rootView.findViewById(R.id.TV_memo_item_content);
            IV_memo_item_delete = (ImageView) rootView.findViewById(R.id.IV_memo_item_delete);
            TV_memo_item_content.setTextColor(ThemeManager.getInstance().getThemeDarkColor(mActivity));
        }

        public TextView getTVContent() {
            return TV_memo_item_content;
        }


        public void setItemPosition(int itemPosition) {
            if (isEditMode) {
                IV_memo_item_delete.setVisibility(View.VISIBLE);
                IV_memo_item_delete.setOnClickListener(this);
                RL_memo_item_root_view.setOnClickListener(this);
            } else {
                IV_memo_item_delete.setVisibility(View.GONE);
                IV_memo_item_delete.setOnClickListener(null);
                RL_memo_item_root_view.setOnClickListener(this);
            }
            this.itemPosition = itemPosition;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.IV_memo_item_delete:
                    dbManager.opeDB();
                    dbManager.delMemo(memoList.get(itemPosition).getMemoId());
                    dbManager.closeDB();
                    memoList.remove(itemPosition);
                    notifyDataSetChanged();
                    break;
                case R.id.RL_memo_item_root_view:
                    if (isEditMode) {
                        EditMemoDialogFragment editMemoDialogFragment = EditMemoDialogFragment.newInstance(
                                topicId, memoList.get(itemPosition).getMemoId(), false, memoList.get(itemPosition).getContent());
                        editMemoDialogFragment.setCallBack(callback);
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
    }
}
