package com.kiminonawa.mydiary.main;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.shared.ThemeManager;


/**
 * Created by daxia on 2016/11/24.
 */

public class NoEntriesDialogFragment extends DialogFragment implements View.OnClickListener {


    /**
     * UI
     */

    private TextView TV_no_entries_create;

    public static NoEntriesDialogFragment newInstance(long topic, String diaryTitle) {
        Bundle args = new Bundle();
        NoEntriesDialogFragment fragment = new NoEntriesDialogFragment();
        args.putLong("topicId", topic);
        args.putString("diaryTitle", diaryTitle);
        args.putBoolean("has_entries", false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View rootView = inflater.inflate(R.layout.dialog_fragment_no_entries, container);

        TV_no_entries_create = (TextView) rootView.findViewById(R.id.TV_no_entries_create);
        TV_no_entries_create.setOnClickListener(this);
        SpannableString content = new SpannableString(TV_no_entries_create.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        TV_no_entries_create.setText(content);
        TV_no_entries_create.setTextColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.TV_no_entries_create:
                Intent goEntriesPageIntent = new Intent(getActivity(), DiaryActivity.class);
                goEntriesPageIntent.putExtras(getArguments());
                getActivity().startActivity(goEntriesPageIntent);
                dismiss();
                break;
        }
    }
}
