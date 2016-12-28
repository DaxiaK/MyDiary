package com.kiminonawa.mydiary.entries.diary.item;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.gui.DiaryWebBlockLayout;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

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

    private DiaryWebBlockLayout diaryWebBlockLayout;


    //TODO check the data is losing in 2st block
    public DiaryBlock(Context context) {
        diaryBlockDataList = new ArrayList();
        diaryBlockFragmentList = new ArrayList();
        diaryWebBlockLayout = new DiaryWebBlockLayout(context);

        //Test data
        title = "Taki test";
        diaryBlockDataList.add(new DiaryBlockEntity("1111", "1111", "www.google.com.tw"));
        diaryBlockDataList.add(new DiaryBlockEntity("2222", "2222", "www.facebook.com"));
        diaryBlockDataList.add(new DiaryBlockEntity("33", "33", "https://tw.yahoo.com/"));
        diaryBlockDataList.add(new DiaryBlockEntity("44", "44", "twitter.com"));
        diaryBlockDataList.add(new DiaryBlockEntity("55", "55", "www.google.com.tw"));
        Log.e("test", "diaryBlockDataList =" + diaryBlockDataList.size());
    }

    public void setDeleteBlockListener(int positionTag, View.OnClickListener listener) {
        diaryWebBlockLayout.setDeleteOnClick(listener);
        diaryWebBlockLayout.setPositiontag(positionTag);
    }


    @Override
    public void setContent(String content) {
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
        diaryWebBlockLayout.setPositiontag(position);
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
        //Bind Ui
        diaryWebBlockLayout.getRLDiaryBlockContent().setBackground(ThemeManager.getInstance().getRadiusBgDrawable(context));
        //Bind data
        diaryWebBlockLayout.getTVDiaryBlockViewTitle().setText(title);
        for (DiaryBlockEntity diaryBlockEntity : diaryBlockDataList) {
            diaryBlockFragmentList.add(
                    DiaryBlockFragment.newInstance(diaryBlockEntity.getTitle(), diaryBlockEntity.getSubtitle(),
                            diaryBlockEntity.getUrl()));
        }
        diaryBlockAdapter = new DiaryBlockAdapter((FragmentActivity) context, diaryBlockFragmentList);
        diaryWebBlockLayout.getDiaryBlockViewPager().setAdapter(diaryBlockAdapter);
        diaryWebBlockLayout.getCIDiaryViewBlock().setViewPager(diaryWebBlockLayout.getDiaryBlockViewPager());
    }

    @Override
    public View getView() {
        return diaryWebBlockLayout;
    }

    @Override
    public void setEditMode(boolean isEditMode) {
    }

    @Override
    public String getContent() {
        return blockJsonContent.toString();
    }

}
