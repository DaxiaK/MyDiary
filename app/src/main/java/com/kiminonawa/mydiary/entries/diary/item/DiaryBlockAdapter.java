package com.kiminonawa.mydiary.entries.diary.item;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by daxia on 2016/12/14.
 */

public class DiaryBlockAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> diaryBlockFragmentList;
    private List<DiaryBlockEntity> diaryBlockDataList;

    public DiaryBlockAdapter(FragmentActivity activity,
                             List<Fragment> diaryBlockFragmentList, List<DiaryBlockEntity> diaryBlockDataList) {
        super(activity.getSupportFragmentManager());
        this.diaryBlockFragmentList = diaryBlockFragmentList;
        this.diaryBlockDataList = diaryBlockDataList;
    }

    @Override
    public Fragment getItem(int position) {
        return diaryBlockFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return diaryBlockFragmentList.size();
    }
}
