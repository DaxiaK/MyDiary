package com.kiminonawa.mydiary.shared.gui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ThemeManager;

/**
 * Created by daxia on 2016/10/27.
 */

public abstract class CommonDialogFragment extends DialogFragment implements View.OnClickListener {


    /**
     * UI
     */
    protected MyDiaryButton But_common_ok, But_common_cancel;

    protected RelativeLayout RL_common_view;
    protected TextView TV_common_content;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_common, container);
        RL_common_view = (RelativeLayout) rootView.findViewById(R.id.RL_common_view);

        RL_common_view.setBackgroundColor(
                ThemeManager.getInstance().getThemeMainColor(getActivity()));


        TV_common_content = (TextView) rootView.findViewById(R.id.TV_common_content);
        But_common_ok = (MyDiaryButton) rootView.findViewById(R.id.But_common_ok);
        But_common_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_common_cancel);

        But_common_ok.setOnClickListener(this);
        But_common_cancel.setOnClickListener(this);
        return rootView;
    }


    protected abstract void okButtonEvent();

    protected abstract void cancelButtonEvent();

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.But_common_ok:
                okButtonEvent();
                break;
            case R.id.But_common_cancel:
                cancelButtonEvent();
                break;
        }
    }
}
