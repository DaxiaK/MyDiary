package com.kiminonawa.mydiary.entries;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.calendar.CalendarFragment;
import com.kiminonawa.mydiary.entries.diary.DiaryFragment;
import com.kiminonawa.mydiary.entries.entries.EntriesFragment;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.statusbar.ChinaPhoneHelper;

import info.hoang8f.android.segmented.SegmentedGroup;

public class DiaryActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener,
        ActivityBackDialogFragmentFrom.BackDialogCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    /**
     * Public data
     */
    private long topicId;
    private boolean isCreating;
    private boolean hasEntries;

    /**
     * UI
     */
    private LinearLayout LL_diary_topbar_content;
    private ViewPager ViewPager_diary_content;
    private TextView TV_diary_topbar_title;
    private SegmentedGroup SG_diary_topbar;
    private RadioButton But_diary_topbar_entries, But_diary_topbar_calendar, But_diary_topbar_diary;


    /**
     * View pager
     */
    private ScreenSlidePagerAdapter mPagerAdapter;

    /**
     * Google API
     */
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        //For set status bar
        ChinaPhoneHelper.setStatusBar(this, true);

        topicId = getIntent().getLongExtra("topicId", -1);
        hasEntries = getIntent().getBooleanExtra("has_entries", true);
        if (topicId == -1) {
            finish();
        }
        /**
         * init UI
         */
        LL_diary_topbar_content = (LinearLayout) findViewById(R.id.LL_diary_topbar_content);
        SG_diary_topbar = (SegmentedGroup) findViewById(R.id.SG_diary_topbar);
        SG_diary_topbar.setOnCheckedChangeListener(this);
        SG_diary_topbar.setTintColor(ThemeManager.getInstance().getThemeDarkColor(this));
        But_diary_topbar_entries = (RadioButton) findViewById(R.id.But_diary_topbar_entries);
        But_diary_topbar_calendar = (RadioButton) findViewById(R.id.But_diary_topbar_calendar);
        But_diary_topbar_diary = (RadioButton) findViewById(R.id.But_diary_topbar_diary);
        TV_diary_topbar_title = (TextView) findViewById(R.id.TV_diary_topbar_title);
        TV_diary_topbar_title.setTextColor(ThemeManager.getInstance().getThemeDarkColor(this));

        String diaryTitle = getIntent().getStringExtra("diaryTitle");
        if (diaryTitle == null) {
            diaryTitle = "Diary";
        }
        TV_diary_topbar_title.setText(diaryTitle);
        //After SegmentedGroup was created
        initViewPager();
        initGoogleAPi();
    }

    @Override
    public void onBackPressed() {
        if (isCreating) {
            ActivityBackDialogFragmentFrom activityBackDialogFragmentFrom = new ActivityBackDialogFragmentFrom();
            activityBackDialogFragmentFrom.show(getSupportFragmentManager(), "activityBackDialogFragmentFrom");
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Init Viewpager
     */
    private void initViewPager() {
        ViewPager_diary_content = (ViewPager) findViewById(R.id.ViewPager_diary_content);
        //Make viewpager load one fragment every time.
        ViewPager_diary_content.setOffscreenPageLimit(2);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        ViewPager_diary_content.setAdapter(mPagerAdapter);
        ViewPager_diary_content.addOnPageChangeListener(onPageChangeListener);
        ViewPager_diary_content.setBackground(
                ThemeManager.getInstance().getEntriesBgDrawable(this, getTopicId()));
        if (!hasEntries) {
            ViewPager_diary_content.setCurrentItem(2);
            //Set Default Checked Item
            But_diary_topbar_diary.setChecked(true);
        } else {
            //Set Default Checked Item
            But_diary_topbar_entries.setChecked(true);
        }
    }

    private void initGoogleAPi() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    public void setCreating(boolean creating) {
        isCreating = creating;
    }

    public long getTopicId() {
        return topicId;
    }

    public void gotoPage(int position) {
        ViewPager_diary_content.setCurrentItem(position);
    }


    public void callEntriesListRefresh() {
        ((EntriesFragment) mPagerAdapter.getRegisteredFragment(0)).updateDiary();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.But_diary_topbar_entries:
                ViewPager_diary_content.setCurrentItem(0);
                break;
            case R.id.But_diary_topbar_calendar:
                ViewPager_diary_content.setCurrentItem(1);
                break;
            case R.id.But_diary_topbar_diary:
                ViewPager_diary_content.setCurrentItem(2);
                break;
        }
    }


    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                default:
                    But_diary_topbar_entries.setChecked(true);
                    break;
                case 1:
                    But_diary_topbar_calendar.setChecked(true);
                    break;
                case 2:
                    But_diary_topbar_diary.setChecked(true);
                    break;
            }
        }
    };

    @Override
    public void onBack() {
        super.onBackPressed();
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public BaseDiaryFragment getItem(int position) {
            BaseDiaryFragment fragment;
            switch (position) {
                default:
                    fragment = new EntriesFragment();
                    break;
                case 1:
                    fragment = new CalendarFragment();
                    break;
                case 2:
                    fragment = new DiaryFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
