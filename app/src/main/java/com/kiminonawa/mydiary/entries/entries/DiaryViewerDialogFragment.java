package com.kiminonawa.mydiary.entries.entries;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.diary.DiaryInfo;
import com.kiminonawa.mydiary.entries.diary.ImageArrayAdapter;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.TimeTools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by daxia on 2016/10/27.
 */

public class DiaryViewerDialogFragment extends DialogFragment implements View.OnClickListener {


    /**
     * Callback
     */
    public interface DiaryViewerCallback {
        void deleteDiary();
        void updateDiary();
    }

    private DiaryViewerCallback callback;


    /**
     * UI
     */

    private RelativeLayout RL_diary_info, RL_diary_edit_bar;

    private TextView TV_diary_month, TV_diary_date, TV_diary_day, TV_diary_time, TV_diary_location;
    private ImageView IV_diary_weather, IV_diary_mood;
    private Spinner SP_diary_weather, SP_diary_mood;

    private EditText EDT_diary_title, EDT_diary_content;
    private ImageView IV_diary_close_dialog, IV_diary_location, IV_diary_delete, IV_diary_clear, IV_diary_save;

    private boolean isEditMode;

    /**
     * Info
     */
    private long diaryId;


    //TODO Make this dialog's background has radius.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static DiaryViewerDialogFragment newInstance(long diaryId, boolean isEditMode) {
        Bundle args = new Bundle();
        DiaryViewerDialogFragment fragment = new DiaryViewerDialogFragment();
        args.putLong("diaryId", diaryId);
        args.putBoolean("isEditMode", isEditMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isEditMode = getArguments().getBoolean("isEditMode", false);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);

        View rootView = inflater.inflate(R.layout.fragment_diary, container);

        RL_diary_info = (RelativeLayout) rootView.findViewById(R.id.RL_diary_info);
        RL_diary_edit_bar = (RelativeLayout) rootView.findViewById(R.id.RL_diary_edit_bar);
        RL_diary_info.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
        RL_diary_edit_bar.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        EDT_diary_title = (EditText) rootView.findViewById(R.id.EDT_diary_title);
        EDT_diary_title.getBackground().mutate().setColorFilter(ThemeManager.getInstance().getThemeMainColor(getActivity()),
                PorterDuff.Mode.SRC_ATOP);
        EDT_diary_content = (EditText) rootView.findViewById(R.id.EDT_diary_content);

        TV_diary_month = (TextView) rootView.findViewById(R.id.TV_diary_month);
        TV_diary_date = (TextView) rootView.findViewById(R.id.TV_diary_date);
        TV_diary_day = (TextView) rootView.findViewById(R.id.TV_diary_day);
        TV_diary_time = (TextView) rootView.findViewById(R.id.TV_diary_time);
        TV_diary_location = (TextView) rootView.findViewById(R.id.TV_diary_location);


        IV_diary_close_dialog = (ImageView) rootView.findViewById(R.id.IV_diary_close_dialog);
        IV_diary_close_dialog.setVisibility(View.VISIBLE);
        IV_diary_close_dialog.setOnClickListener(this);
        IV_diary_location = (ImageView) rootView.findViewById(R.id.IV_diary_location);

        IV_diary_delete = (ImageView) rootView.findViewById(R.id.IV_diary_delete);
        IV_diary_clear = (ImageView) rootView.findViewById(R.id.IV_diary_clear);
        IV_diary_save = (ImageView) rootView.findViewById(R.id.IV_diary_save);

        initView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            diaryId = getArguments().getLong("diaryId", -1L);
            if (diaryId != -1) {
                DBManager dbManager = new DBManager(getActivity());
                dbManager.opeDB();
                Cursor diaryCursor = dbManager.selectDiaryByDiaryId(diaryId);
                EDT_diary_title.setText(diaryCursor.getString(2));
                EDT_diary_content.setText(diaryCursor.getString(3));
                setDiaryTime(new Date(diaryCursor.getLong(1)));
                String locationNmae = diaryCursor.getString(8);
                if (locationNmae != null && !"".equals(locationNmae)) {
                    TV_diary_location.setText(locationNmae);
                    IV_diary_location.setImageResource(R.drawable.ic_location_on_white_24dp);
                } else {
                    TV_diary_location.setText(getActivity().getString(R.string.diary_no_location));
                    IV_diary_location.setImageResource(R.drawable.ic_location_off_white_24dp);
                }
                setIcon(diaryCursor.getInt(4), diaryCursor.getInt(5));
                diaryCursor.close();
                dbManager.closeDB();
            }
        } catch (NullPointerException e) {
            Log.d("DiaryViewerDialog", e.toString());
        }
    }

    private void initView(View rootView) {
        if (isEditMode) {
            SP_diary_mood = (Spinner) rootView.findViewById(R.id.SP_diary_mood);
            SP_diary_mood.setVisibility(View.VISIBLE);
            SP_diary_weather = (Spinner) rootView.findViewById(R.id.SP_diary_weather);
            SP_diary_weather.setVisibility(View.VISIBLE);

            initMoodSpinner();
            initWeatherSpinner();

            IV_diary_delete.setVisibility(View.GONE);
            IV_diary_clear.setOnClickListener(this);
            IV_diary_save.setOnClickListener(this);
        } else {
            IV_diary_weather = (ImageView) rootView.findViewById(R.id.IV_diary_weather);
            IV_diary_weather.setVisibility(View.VISIBLE);
            IV_diary_mood = (ImageView) rootView.findViewById(R.id.IV_diary_mood);
            IV_diary_mood.setVisibility(View.VISIBLE);

            IV_diary_delete.setOnClickListener(this);
            IV_diary_clear.setVisibility(View.GONE);
            IV_diary_save.setVisibility(View.GONE);

            EDT_diary_title.setClickable(false);
            EDT_diary_title.setKeyListener(null);
            EDT_diary_content.setClickable(false);
            EDT_diary_content.setKeyListener(null);
        }
    }


    private void initMoodSpinner() {
        ImageArrayAdapter moodArrayAdapter = new ImageArrayAdapter(getActivity(), DiaryInfo.getMoodArray());
        SP_diary_mood.setAdapter(moodArrayAdapter);
    }

    private void initWeatherSpinner() {
        ImageArrayAdapter weatherArrayAdapter = new ImageArrayAdapter(getActivity(), DiaryInfo.getWeatherArray());
        SP_diary_weather.setAdapter(weatherArrayAdapter);
    }


    private void setDiaryTime(Date diaryDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(diaryDate);
        TimeTools timeTools = TimeTools.getInstance(getActivity().getApplicationContext());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        TV_diary_month.setText(timeTools.getMonthsFullName()[calendar.get(Calendar.MONTH)]);
        TV_diary_date.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        TV_diary_day.setText(timeTools.getDaysFullName()[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        TV_diary_time.setText(sdf.format(calendar.getTime()));
    }

    private void setIcon(int mood, int weather) {
        if (isEditMode) {
            SP_diary_mood.setSelection(mood);
            SP_diary_weather.setSelection(weather);
        } else {
            IV_diary_mood.setImageResource(DiaryInfo.getMoodResourceId(mood));
            IV_diary_weather.setImageResource(DiaryInfo.getWeathetrResourceId(weather));
        }
    }


    public void setCallBack(DiaryViewerCallback callback) {
        this.callback = callback;
    }


    private void deleteDiary() {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        dbManager.delDiary(diaryId);
        dbManager.closeDB();
    }

    private void updateDiary() {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        dbManager.updateDiary(diaryId, EDT_diary_title.getText().toString(), EDT_diary_content.getText().toString(),
                SP_diary_mood.getSelectedItemPosition(), SP_diary_weather.getSelectedItemPosition());
        dbManager.closeDB();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.IV_diary_close_dialog:
                dismiss();
                break;
            case R.id.IV_diary_delete:
                DiaryDeleteDialogFragment diaryDeleteDialogFragment = new DiaryDeleteDialogFragment();
                diaryDeleteDialogFragment.show(getFragmentManager(), "diaryDeleteDialogFragment");
//                deleteDiary();
//                callback.deleteDiary();
                dismiss();
                break;
            case R.id.IV_diary_clear:
                dismiss();
                break;
            case R.id.IV_diary_save:
                updateDiary();
                callback.updateDiary();
                dismiss();
                break;
        }
    }
}
