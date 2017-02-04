package com.kiminonawa.mydiary.entries.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.TimeTools;

import java.util.Calendar;

/**
 * Created by daxia on 2017/2/4.
 */

public class CalendarFactory {


    private Context mContext;
    private Calendar calendar;
    private int dateChange = 0;

    private TimeTools timeTools;
    private Paint monthPaint, datePaint, dayPaint;

    private Rect textRect;
    private int textBaseX;

    public CalendarFactory(Context context, int width, int height) {

        calendar = Calendar.getInstance();
        timeTools = TimeTools.getInstance(context);
        mContext = context;
        textRect = new Rect(0, 0, width, height / 2);


        //init Color
        monthPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        monthPaint.setTextSize(context.getResources().getDimension(R.dimen.calendar_months_full_name_text_size));
        monthPaint.setColor(ThemeManager.getInstance().getThemeDarkColor(context));
        monthPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetricsInt fontMetrics = monthPaint.getFontMetricsInt();
        textBaseX = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;

        //Need bold
        datePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.calendar_date_full_name_text_size));
        datePaint.setColor(ThemeManager.getInstance().getThemeDarkColor(context));
        datePaint.setTextAlign(Paint.Align.CENTER);

        dayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dayPaint.setTextSize(context.getResources().getDimension(R.dimen.calendar_day_full_name_text_size));
        dayPaint.setColor(ThemeManager.getInstance().getThemeDarkColor(context));
        dayPaint.setTextAlign(Paint.Align.CENTER);

    }


    public synchronized void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        updateCalendar(canvas);
    }


    public synchronized void nextDateDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        dateChange = 1;
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + dateChange);
        updateCalendar(canvas);
    }

    public synchronized void preDateDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        dateChange = -1;
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + dateChange);
        updateCalendar(canvas);
    }


    private void updateCalendar(Canvas canvas) {
        canvas.drawText(timeTools.getMonthsFullName()[calendar.get(Calendar.MONTH)], textBaseX, textRect.centerX() / 2, monthPaint);
        canvas.drawText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), textBaseX, textRect.centerX(), datePaint);
        canvas.drawText(timeTools.getDaysFullName()[calendar.get(Calendar.DAY_OF_WEEK) - 1], textBaseX, textRect.centerX() + 300, dayPaint);
    }

}
