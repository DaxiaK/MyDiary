package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    private RelativeLayout RL_diary_block_content;
    private ImageView IV_diary_block_remove;
    private TextView TV_diary_block_view_title;
    private ViewPager ViewPager_diary_block;


    //TODO check the data is losing in 2st block
    public DiaryBlock() {
        diaryBlockDataList = new ArrayList();
        diaryBlockFragmentList = new ArrayList();
        //Test data
        title = "Taki test";
        diaryBlockDataList.add(new DiaryBlockEntity("1111", "1111", "www.google.com.tw"));
        diaryBlockDataList.add(new DiaryBlockEntity("2222", "2222", "www.facebook.com"));
        diaryBlockDataList.add(new DiaryBlockEntity("33", "33", "https://tw.yahoo.com/"));
        diaryBlockDataList.add(new DiaryBlockEntity("44", "44", "twitter.com"));
        diaryBlockDataList.add(new DiaryBlockEntity("55", "55", "www.google.com.tw"));
        Log.e("test", "diaryBlockDataList =" + diaryBlockDataList.size());
    }

    public void setDeleteBlockListener(View.OnClickListener listener) {
        IV_diary_block_remove.setOnClickListener(listener);
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
        RL_diary_block_content = (RelativeLayout) blockView.findViewById(R.id.RL_diary_block_content);
        TV_diary_block_view_title = (TextView) blockView.findViewById(R.id.TV_diary_block_view_title);
        ViewPager_diary_block = (ViewPager) blockView.findViewById(R.id.ViewPager_diary_view_block);
        CI_diary_view_block = (CircleIndicator) blockView.findViewById(R.id.CI_diary_view_block);
        IV_diary_block_remove = (ImageView) blockView.findViewById(R.id.IV_diary_block_remove);
        //Bind Ui
        RL_diary_block_content.setBackground(ThemeManager.getInstance().getRadiusBgDrawable(context));
        //Bind data
        IV_diary_block_remove.setTag(position);
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
