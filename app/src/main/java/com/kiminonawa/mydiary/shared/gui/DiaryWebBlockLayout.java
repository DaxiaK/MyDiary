package com.kiminonawa.mydiary.shared.gui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by daxia on 2016/11/20.
 */

public class DiaryWebBlockLayout extends LinearLayout {

    private RelativeLayout RL_diary_block_content;
    private ImageView IV_diary_block_remove;
    private TextView TV_diary_block_view_title;
    private ViewPager ViewPager_diary_block;

    private CircleIndicator CI_diary_view_block;

    public DiaryWebBlockLayout(Context context) {
        super(context);
        View rootView = LayoutInflater.from(context).inflate(R.layout.diary_block_view, this, true);

        RL_diary_block_content = (RelativeLayout) rootView.findViewById(R.id.RL_diary_block_content);
        TV_diary_block_view_title = (TextView) rootView.findViewById(R.id.TV_diary_block_view_title);
        ViewPager_diary_block = (ViewPager) rootView.findViewById(R.id.ViewPager_diary_view_block);
        CI_diary_view_block = (CircleIndicator) rootView.findViewById(R.id.CI_diary_view_block);
        IV_diary_block_remove = (ImageView) rootView.findViewById(R.id.IV_diary_block_remove);
    }

    public RelativeLayout getRLDiaryBlockContent() {
        return RL_diary_block_content;
    }

    public ImageView getIVDiaryBlockRemove() {
        return IV_diary_block_remove;
    }

    public TextView getTVDiaryBlockViewTitle() {
        return TV_diary_block_view_title;
    }

    public ViewPager getDiaryBlockViewPager() {
        return ViewPager_diary_block;
    }

    public CircleIndicator getCIDiaryViewBlock() {
        return CI_diary_view_block;
    }

    public void setDeleteOnClick(OnClickListener listener) {
        IV_diary_block_remove.setOnClickListener(listener);
    }

    public void setPositiontag(int position) {
        IV_diary_block_remove.setTag(position);
    }


}
