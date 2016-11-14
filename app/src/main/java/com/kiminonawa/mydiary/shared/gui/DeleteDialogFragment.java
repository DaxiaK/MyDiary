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

public abstract class DeleteDialogFragment extends DialogFragment implements View.OnClickListener {


    /**
     * Callback
     */
    public interface DeleteCallback {
        void delete();
    }

    protected DeleteCallback callback;
    /**
     * UI
     */
    protected MyDiaryButton But_delete_ok, But_delete_cancel;

    protected RelativeLayout RL_delete_view;
    protected TextView TV_delete_content;

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
        this.getDialog().setCanceledOnTouchOutside(false);
        View rootView = inflater.inflate(R.layout.dialog_fragment_delete, container);
        RL_delete_view = (RelativeLayout) rootView.findViewById(R.id.RL_delete_view);

        RL_delete_view.setBackgroundColor(
                ThemeManager.getInstance().getThemeMainColor(getActivity()));


        TV_delete_content = (TextView) rootView.findViewById(R.id.TV_delete_content);
        But_delete_ok = (MyDiaryButton) rootView.findViewById(R.id.But_delete_ok);
        But_delete_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_delete_cancel);

        But_delete_ok.setOnClickListener(DeleteDialogFragment.this);
        But_delete_cancel.setOnClickListener(DeleteDialogFragment.this);
        return rootView;
    }


    public void setCallBack(DeleteCallback callback) {
        this.callback = callback;
    }


    protected abstract void okButtonEvent();

    protected abstract void cancelButtonEvent();

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.But_delete_ok:
                okButtonEvent();
                break;
            case R.id.But_delete_cancel:
                cancelButtonEvent();
                break;
        }
    }
}
