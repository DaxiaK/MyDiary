package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ThemeManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryBlock implements IDairyRow {

    private int position;
    private String title;
    private JSONArray blockJsonContent;
    private List<Fragment> diaryBlockFragmentList;
    private List<DiaryBlockEntity> diaryBlockDataList;
    private DiaryBlockAdapter diaryBlockAdapter;
    private CircleIndicator CI_diary_view_block;

    /**
     * UI
     */
    private View blockView;
    private LinearLayout TV_diary_content;
    private TextView TV_diary_block_view_title;
    private ViewPager ViewPager_diary_block;

    public DiaryBlock() {
        //Test data
        title = "Taki test";
        diaryBlockDataList = new ArrayList();
        diaryBlockDataList.add(new DiaryBlockEntity("1111", "1111", "www.google.com.tw"));
        diaryBlockDataList.add(new DiaryBlockEntity("2222", "2222", "www.facebook.comwww.facebook.comwww.facebook.comwww.facebook.comwww.facebook.comwww.facebook.comwww.facebook.com"));
        diaryBlockDataList.add(new DiaryBlockEntity("33", "33", "www.google.com.tw"));
        diaryBlockDataList.add(new DiaryBlockEntity("44", "44", "www.facebook.comwww.facebook.comwww.facebook.comwww.facebook.comwww.facebook.comwww.facebook.comwww.facebook.com"));
        diaryBlockDataList.add(new DiaryBlockEntity("55", "55", "www.google.com.tw"));
        diaryBlockFragmentList = new ArrayList();

    }


    @Override
    public void setContent(String content) {
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public int getType() {
        return TYPE_BLOCK;
    }


    @Override
    public void initView(Context context, ViewGroup parent) {
        //set UI
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        blockView =
                layoutInflater
                        .inflate(R.layout.diary_block_view, parent, false);
        TV_diary_content = (LinearLayout) blockView.findViewById(R.id.TV_diary_content);
        TV_diary_block_view_title = (TextView) blockView.findViewById(R.id.TV_diary_block_view_title);
        ViewPager_diary_block = (ViewPager) blockView.findViewById(R.id.ViewPager_diary_view_block);
        CI_diary_view_block = (CircleIndicator) blockView.findViewById(R.id.CI_diary_view_block);
        //Bind Ui
        TV_diary_content.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(context));
        //Bind data
        TV_diary_block_view_title.setText(title);
        for (DiaryBlockEntity diaryBlockEntity : diaryBlockDataList) {
            diaryBlockFragmentList.add(
                    DiaryBlockFragment.newInstance(diaryBlockEntity.getTitle(), diaryBlockEntity.getSubtitle(),
                            diaryBlockEntity.getUrl()));
        }
        diaryBlockAdapter = new DiaryBlockAdapter((FragmentActivity) context, diaryBlockFragmentList);
        ViewPager_diary_block.setAdapter(diaryBlockAdapter);
        CI_diary_view_block.setViewPager(ViewPager_diary_block);
    }

    @Override
    public View getView() {
        return blockView;
    }

    @Override
    public void setEditMode(boolean isEditMode) {
    }

    @Override
    public String getContent() {
        return blockJsonContent.toString();
    }

}
