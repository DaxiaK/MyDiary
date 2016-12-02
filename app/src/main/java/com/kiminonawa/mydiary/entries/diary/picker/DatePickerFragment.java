package com.kiminonawa.mydiary.entries.diary.picker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.kiminonawa.mydiary.shared.ThemeManager;

import java.util.Calendar;

/**
 * Created by daxia on 2016/11/25.
 */

public class DatePickerFragment extends DialogFragment {


    private long savedTime;
    private DatePickerDialog.OnDateSetListener onDateSetListener;


    public static DatePickerFragment newInstance(long savedTime) {
        Bundle args = new Bundle();
        DatePickerFragment fragment = new DatePickerFragment();
        args.putLong("savedTime", savedTime);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar;
        savedTime = getArguments().getLong("savedTime", -1);
        calendar = Calendar.getInstance();
        if (savedTime != -1) {
            calendar.setTimeInMillis(savedTime);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), ThemeManager.getInstance().getPickerStyle(),
                onDateSetListener, year, month, day);
    }
}