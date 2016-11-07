package com.kiminonawa.mydiary.memo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.List;

/**
 * Created by daxia on 2016/10/17.
 */

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {


    private List<MemoEntity> memoList;
    private Context mContext;
    private DBManager dbManager;

    public MemoAdapter(Context mContext, List<MemoEntity> memoList, DBManager dbManager) {
        this.mContext = mContext;
        this.memoList = memoList;
        this.dbManager = dbManager;
    }


    @Override
    public MemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_memo_item, parent, false);
        return new MemoViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    @Override
    public void onBindViewHolder(final MemoViewHolder holder, final int position) {
        setMemoContent(holder, position);

        //Set onItemClick
        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoList.get(position).toggleChecked();
                dbManager.opeDB();
                dbManager.updateMemoChecked(memoList.get(position).getMemoId(), memoList.get(position).isChecked());
                dbManager.closeDB();
                setMemoContent(holder, position);
            }
        });

    }

    private void setMemoContent(final MemoViewHolder holder, final int position) {
        if (memoList.get(position).isChecked()) {
            SpannableString spannableContent = new SpannableString(memoList.get(position).getContent());
            spannableContent.setSpan(new StrikethroughSpan(), 0, spannableContent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.getTVContent().setText(spannableContent);
        } else {
            holder.getTVContent().setText(memoList.get(position).getContent());
        }
    }


    protected class MemoViewHolder extends RecyclerView.ViewHolder {

        private View rootView;
        private TextView TV_memo_item_content;


        protected MemoViewHolder(View view) {
            super(view);
            this.rootView = view;
            TV_memo_item_content = (TextView) rootView.findViewById(R.id.TV_memo_item_content);
            TV_memo_item_content.setTextColor(ThemeManager.getInstance().getThemeDarkColor(mContext));
        }

        public TextView getTVContent() {
            return TV_memo_item_content;
        }

        protected View getRootView() {
            return rootView;
        }
    }
}
