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

        PageEffectView pageEffectView = new PageEffectView(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        pageEffectView.setLayoutParams(params);
        RL_calendar_content.addView(pageEffectView);

        return rootView;
    }


}
