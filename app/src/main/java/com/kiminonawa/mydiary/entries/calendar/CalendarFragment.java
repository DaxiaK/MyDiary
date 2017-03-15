package com.kiminonawa.mydiary.entries.calendar;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.BaseDiaryFragment;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;
import java.util.Date;


public class CalendarFragment extends BaseDiaryFragment implements View.OnClickListener, OnDateSelectedListener {


    /**
     * Calendar
     */
    private Calendar calendar;
    private Date currentDate;

    private ThemeManager themeManager;

    /**
     * UI
     */
    private RelativeLayout RL_calendar_content;
    private RelativeLayout RL_calendar_edit_bar;
    private FloatingActionButton FAB_calendar_change_mode;

    /**
     * calendar Mode
     */
    private PageEffectView pageEffectView;
    private MaterialCalendarView materialCalendarView;

    private int currentMode;

    private static final int MODE_DAY = 1;
    private static final int MODE_MONTH = 2;


    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        currentDate = new Date();
        calendar.setTime(currentDate);
        themeManager = ThemeManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        RL_calendar_edit_bar = (RelativeLayout) rootView.findViewById(R.id.RL_calendar_edit_bar);
        RL_calendar_edit_bar.setBackgroundColor(themeManager.getThemeMainColor(getActivity()));

        RL_calendar_content = (RelativeLayout) rootView.findViewById(R.id.RL_calendar_content);

        FAB_calendar_change_mode = (FloatingActionButton) rootView.findViewById(R.id.FAB_calendar_change_mode);
        FAB_calendar_change_mode.setOnClickListener(this);

        //default mode
        currentMode = MODE_DAY;
        setCalendarMode(currentMode);
        return rootView;
    }


    private void setCalendarMode(int mode) {
        RL_calendar_content.removeAllViews();
        switch (mode) {
            case MODE_DAY:
                materialCalendarView = null;
                pageEffectView = new PageEffectView(getActivity(), calendar);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                pageEffectView.setLayoutParams(params);
                RL_calendar_content.addView(pageEffectView);
                break;
            case MODE_MONTH:
                pageEffectView = null;
                materialCalendarView = new MaterialCalendarView(getActivity());
                RelativeLayout.LayoutParams calendarViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                materialCalendarView.setLayoutParams(calendarViewParams);
                materialCalendarView.state().edit()
                        .setFirstDayOfWeek(Calendar.WEDNESDAY)
                        .setCalendarDisplayMode(CalendarMode.MONTHS)
                        .commit();
                materialCalendarView.setCurrentDate(calendar);
                materialCalendarView.setDateSelected(calendar, true);
                materialCalendarView.setOnDateChangedListener(this);
                RL_calendar_content.addView(materialCalendarView);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.FAB_calendar_change_mode:
                if (currentMode == MODE_DAY) {
                    currentMode = MODE_MONTH;
                } else {
                    currentMode = MODE_DAY;
                }
                setCalendarMode(currentMode);
                break;
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        calendar.setTime(date.getDate());
    }
}
