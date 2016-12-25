package com.kiminonawa.mydiary.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.kiminonawa.mydiary.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

/**
 * Created by daxia on 2016/11/25.
 */

public class ColorPickerFragment extends DialogFragment implements View.OnClickListener {


    public interface colorPickerCallback {
        void onColorChange(int colorCode, int viewId);
    }

    private int oldColor;
    private int viewId;

    private ColorPicker picker;
    private SVBar svBar;
    private Button But_setting_change_color, But_setting_cancel;

    private colorPickerCallback callback;


    public static ColorPickerFragment newInstance(int oldColor, int viewId) {
        Bundle args = new Bundle();
        ColorPickerFragment fragment = new ColorPickerFragment();
        args.putInt("oldColor", oldColor);
        args.putInt("viewId", viewId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        oldColor = getArguments().getInt("oldColor", 0);
        viewId = getArguments().getInt("viewId", View.NO_ID);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        if (viewId == View.NO_ID) {
            dismiss();
        }
        View rootView = inflater.inflate(R.layout.dialog_fragment_color_picker, container);
        picker = (ColorPicker) rootView.findViewById(R.id.picker);
        svBar = (SVBar) rootView.findViewById(R.id.svbar);
        But_setting_change_color = (Button) rootView.findViewById(R.id.But_setting_change_color);
        But_setting_cancel = (Button) rootView.findViewById(R.id.But_setting_cancel);

        picker.addSVBar(svBar);
        picker.setOldCenterColor(oldColor);

        But_setting_change_color.setOnClickListener(this);
        But_setting_cancel.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (callback == null) {
            callback = (colorPickerCallback) getTargetFragment();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.But_setting_change_color:
                callback.onColorChange(picker.getColor(), viewId);
                dismiss();
                break;
            case R.id.But_setting_cancel:
                dismiss();
                break;
        }


    }

}