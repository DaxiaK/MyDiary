package com.kiminonawa.mydiary.contacts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ColorTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daxia on 2016/11/9.
 */

public class LatterSortLayout extends LinearLayout {

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;

    private Context mContext;
    private List<String> sortTextList = new ArrayList();
    private int Choose = -1;
    private TextView sortTextView;


    public void setSortTextView(TextView sortTextView) {
        this.sortTextView = sortTextView;
    }

    public LatterSortLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(VERTICAL);
        initSortText();
    }

    private void initSortText() {
        addView(buildTextLayout("#"));
        for (char i = 'A'; i <= 'Z'; i++) {
            final String character = i + "";
            TextView tv = buildTextLayout(character);
            addView(tv);
        }
    }

    private TextView buildTextLayout(final String character) {

        sortTextList.add(character);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);

        TextView sortTextView = new TextView(mContext);
        sortTextView.setLayoutParams(layoutParams);
        sortTextView.setGravity(Gravity.CENTER);
        sortTextView.setClickable(true);
        sortTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        sortTextView.setText(character);
        sortTextView.setTextColor(ColorTools.getColor(getContext(), R.color.contacts_latter_text));
        sortTextView.setShadowLayer(1, 1, 1, R.color.contacts_latter_text_shadow);
        return sortTextView;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float y = event.getY();
        final int oldChoose = Choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int clickItem = (int) (y / getHeight() * sortTextList.size());

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Choose = -1;//
                invalidate();
                if (sortTextView != null) {
                    sortTextView.setVisibility(View.GONE);
                }
                break;
            //Touch move & down
            default:
                if (oldChoose != clickItem) {
                    if (clickItem >= 0 && clickItem < sortTextList.size()) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(sortTextList.get(clickItem));
                        }
                        if (sortTextView != null) {
                            sortTextView.setText(sortTextList.get(clickItem));
                            sortTextView.setVisibility(View.VISIBLE);
                        }
                        Choose = clickItem;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

}
