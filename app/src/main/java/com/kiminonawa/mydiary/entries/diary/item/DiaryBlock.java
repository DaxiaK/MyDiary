package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
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

/**
 * Created by daxia on 2016/11/19.
 */

public class DiaryBlock implements IDairyRow {

    private int position;
    private String titile;
    private JSONArray blockJsonContent;
    private List blockDataList;

    /**
     * UI
     */
    private View blockView;
    private LinearLayout TV_diary_content;
    private TextView TV_diary_block_title;
    private ViewPager ViewPager_diary_block;

    public DiaryBlock(Context context) {
        //Test data
        titile = "Taki test";
        blockDataList = new ArrayList();
        blockDataList.add(new DiaryBlockObj("12333333", "12333333"));
        blockDataList.add(new DiaryBlockObj("123456789", "123456"));
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
                        .inflate(R.layout.diary_block_item, parent, false);
        TV_diary_content = (LinearLayout) blockView.findViewById(R.id.TV_diary_content);
        TV_diary_block_title = (TextView) blockView.findViewById(R.id.TV_diary_block_title);
        ViewPager_diary_block = (ViewPager) blockView.findViewById(R.id.ViewPager_diary_block);
        //Bind Ui & data
        TV_diary_content.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(context));
        TV_diary_block_title.setText(titile);
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
