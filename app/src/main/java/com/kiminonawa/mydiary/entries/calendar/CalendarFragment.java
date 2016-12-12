package com.kiminonawa.mydiary.entries.calendar;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.BaseDiaryFragment;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.TimeTools;

import java.util.Calendar;
import java.util.Date;


public class CalendarFragment extends BaseDiaryFragment implements Animation.AnimationListener {


    /**
     * Calendar
     */
    private Calendar calendar;
    private Date currentDate;
    private int dateChange = 0;
    //Temp value
    private int miniTouchGestureWidth = 120;
    private TimeTools timeTools;
    private ThemeManager themeManager;
    /**
     * Animation
     */
    private Animation fadeOutAm, fadeInAm;

    /**
     * UI
     */
    private LinearLayout LL_calendar_content;
    private RelativeLayout  RL_calendar_edit_bar;
    private View View_calendar_content_shadow;
    private TextView TV_calendar_months, TV_calendar_date, TV_calendar_day;



    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        currentDate = new Date();
        calendar.setTime(currentDate);
        miniTouchGestureWidth = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        timeTools = TimeTools.getInstance(getActivity().getApplicationContext());
        themeManager = ThemeManager.getInstance();
        initAnimation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cnlendar, container, false);

        RL_calendar_edit_bar = (RelativeLayout) rootView.findViewById(R.id.RL_calendar_edit_bar);
        RL_calendar_edit_bar.setBackgroundColor(themeManager.getThemeMainColor(getActivity()));

        LL_calendar_content = (LinearLayout) rootView.findViewById(R.id.LL_calendar_content);
        View_calendar_content_shadow = rootView.findViewById(R.id.View_calendar_content_shadow);

        TV_calendar_months = (TextView) rootView.findViewById(R.id.TV_calendar_months);
        TV_calendar_date = (TextView) rootView.findViewById(R.id.TV_calendar_date);
        TV_calendar_day = (TextView) rootView.findViewById(R.id.TV_calendar_day);

        TV_calendar_months.setTextColor(themeManager.getThemeDarkColor(getActivity()));
        TV_calendar_date.setTextColor(themeManager.getThemeDarkColor(getActivity()));
        TV_calendar_day.setTextColor(themeManager.getThemeDarkColor(getActivity()));

        updateCalendar();

        LL_calendar_content.setOnTouchListener(canlderOnTouchListener);


        return rootView;
    }

    private void updateCalendar() {
        TV_calendar_months.setText(timeTools.getMonthsFullName()[calendar.get(Calendar.MONTH)]);
        TV_calendar_date.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        TV_calendar_day.setText(timeTools.getDaysFullName()[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
    }

    private void initAnimation() {
        //TODO This aniimation is temp , it should be used by paper curl effect!
        fadeInAm = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_am);
        fadeOutAm = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_am);
        fadeOutAm.setAnimationListener(this);
    }


    private void startAnimation() {
        try {
            fadeOutAm.reset();
            LL_calendar_content.clearAnimation();
            LL_calendar_content.startAnimation(fadeOutAm);
            View_calendar_content_shadow.startAnimation(fadeOutAm);
        } catch (Resources.NotFoundException e) {
            Log.e("NotFoundException", e.toString());
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == fadeOutAm) {
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + dateChange);
            updateCalendar();
            LL_calendar_content.startAnimation(fadeInAm);
            View_calendar_content_shadow.startAnimation(fadeInAm);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private View.OnTouchListener canlderOnTouchListener = new View.OnTouchListener() {
        private float initialTouchX;
        private float initialTouchY;
        private float dx;
        private float dy;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:

                    dx=initialTouchX-event.getRawX();
                    dy=initialTouchY-event.getRawY();

                    if(Math.abs(dx)>=Math.abs(dy)){
                        if (dx > miniTouchGestureWidth) {
                            //move right
                            dateChange = -1;

                        } else if (dx < (-miniTouchGestureWidth)) {
                            //move left
                            dateChange = 1;
                        }
                    }else{
                        if (dy> miniTouchGestureWidth) {
                            //move up
                            dateChange = -1;
                        } else if (dy < (-miniTouchGestureWidth)) {
                            //move down
                            dateChange = 1;
                        }
                    }
                    startAnimation();
                    v.getParent().requestDisallowInterceptTouchEvent(false);

                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }

            return true;
        }
    };


}
