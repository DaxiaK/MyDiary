package com.kiminonawa.mydiary.entries.diary;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by daxia on 2016/12/13.
 */

public class DiaryBlockAdapter extends FragmentStatePagerAdapter {


    public DiaryBlockAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
