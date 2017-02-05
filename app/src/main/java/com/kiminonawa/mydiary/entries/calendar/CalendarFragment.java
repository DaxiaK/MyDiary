package com.kiminonawa.mydiary.entries.calendar;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.BaseDiaryFragment;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.TimeTools;

import java.util.Calendar;
import java.util.Date;


public class CalendarFragment extends BaseDiaryFragment {


    /**
     * Calendar
     */
    private Calendar calendar;
    private Date currentDate;

    private TimeTools timeTools;
    private ThemeManager themeManager;

    /**
     * UI
     */
    private RelativeLayout RL_calendar_content;
    private RelativeLayout RL_calendar_edit_bar;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        currentDate = new Date();
        calendar.setTime(currentDate);
        timeTools = TimeTools.getInstance(getActivity().getApplicationContext());
        themeManager = ThemeManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        RL_calendar_edit_bar = (RelativeLayout) rootView.findViewById(R.id.RL_calendar_edit_bar);
        RL_calendar_edit_bar.setBackgroundColor(themeManager.getThemeMainColor(getActivity()));

        RL_calendar_content = (RelativeLayout) rootView.findViewById(R.id.RL_calendar_content);


//        TV_calendar_months.setTextColor(themeManager.getThemeDarkColor(getActivity()));
//        TV_calendar_date.setTextColor(themeManager.getThemeDarkColor(getActivity()));
//        TV_calendar_day.setTextColor(themeManager.getThemeDarkColor(getActivity()));

        PageEffectView pageEffectView = new PageEffectView(getActivity());
        RL_calendar_content.addView(pageEffectView);


        return rootView;
    }

//    private void updateCalendar() {
//        TV_calendar_months.setText(timeTools.getMonthsFullName()[calendar.get(Calendar.MONTH)]);
//        TV_calendar_date.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
//        TV_calendar_day.setText(timeTools.getDaysFullName()[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
//    }



}
