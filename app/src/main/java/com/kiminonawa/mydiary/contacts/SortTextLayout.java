package com.kiminonawa.mydiary.contacts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by daxia on 2016/11/9.
 */

public class SortTextLayout extends LinearLayout {
    private Context mContext;
    private CharacterClickListener mListener;

    public SortTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(VERTICAL);
        initSortText();
    }

    private void initSortText() {
        for (char i = 'A'; i <= 'Z'; i++) {
            final String character = i + "";
            TextView tv = buildTextLayout(character);
            addView(tv);
        }
        addView(buildTextLayout("#"));
    }

    private TextView buildTextLayout(final String character) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);

        TextView sorttextView = new TextView(mContext);
        sorttextView.setLayoutParams(layoutParams);
        sorttextView.setGravity(Gravity.CENTER);
        sorttextView.setClickable(true);

        sorttextView.setText(character);

        sorttextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.clickCharacter(character);
                }
            }
        });
        return sorttextView;
    }


    public void setCharacterListener(CharacterClickListener listener) {
        mListener = listener;
    }

    public interface CharacterClickListener {
        void clickCharacter(String character);

        void clickArrow();
    }
}
