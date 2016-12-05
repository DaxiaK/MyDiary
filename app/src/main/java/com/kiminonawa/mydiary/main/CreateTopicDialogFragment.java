package com.kiminonawa.mydiary.main;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.setting.ColorPickerFragment;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;

import static android.R.attr.textColor;


/**
 * Created by daxia on 2016/8/27.
 */
public class CreateTopicDialogFragment extends DialogFragment implements View.OnClickListener,
        ColorPickerFragment.colorPickerCallback {


    public interface TopicCreatedCallback {
        void TopicCreated();

        void TopicDeleted(int position);

        void TopicUpdated();
    }


    /**
     * Callback
     */
    private TopicCreatedCallback callback;
    /**
     * Edit
     */
    private boolean isEdit;
    private int position;
    /**
     * TextColor
     */
    private int textColorCode;
    /**
     * UI
     */
    private EditText EDT_topic_create_name;
    private ImageView IV_topic_textcolor;
    private MyDiaryButton But_topic_create_ok, But_topic_create_cancel, But_topic_create_delete;
    private Spinner SP_topic_create_type;


    public static CreateTopicDialogFragment newInstance(boolean isEdit, int position, int textColorCode) {
        Bundle args = new Bundle();
        CreateTopicDialogFragment fragment = new CreateTopicDialogFragment();
        args.putBoolean("isEdit", isEdit);
        args.putInt("position", position);
        args.putInt("textColorCode", textColorCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isEdit = getArguments().getBoolean("isEdit", false);
        position = getArguments().getInt("position", -1);
        textColorCode = getArguments().getInt("textColorCode", Color.BLACK);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        //This position is wrong.
        if (isEdit && position == -1) {
            dismiss();
        }

        View rootView = inflater.inflate(R.layout.dialog_fragment_create_topic, container);
        EDT_topic_create_name = (EditText) rootView.findViewById(R.id.EDT_topic_create_name);
        IV_topic_textcolor = (ImageView) rootView.findViewById(R.id.IV_topic_textcolor);
        IV_topic_textcolor.setOnClickListener(this);
        SP_topic_create_type = (Spinner) rootView.findViewById(R.id.SP_topic_create_type);

        But_topic_create_ok = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_ok);
        But_topic_create_ok.setOnClickListener(this);
        But_topic_create_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_cancel);
        But_topic_create_cancel.setOnClickListener(this);
        if (isEdit) {
            SP_topic_create_type.setVisibility(View.GONE);

            But_topic_create_delete = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_delete);
            But_topic_create_delete.setVisibility(View.VISIBLE);
            But_topic_create_delete.setOnClickListener(this);
        } else {
            initTopicTypeSpinner();
        }
        setTextColor(textColorCode);
        return rootView;
    }

    private void setTextColor(int colorCode) {
        IV_topic_textcolor.setImageDrawable(new ColorDrawable(colorCode));
    }

    private void initTopicTypeSpinner() {
        ArrayAdapter topicTypeAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_simple_text,
                getResources().getStringArray(R.array.topic_type));
        SP_topic_create_type.setAdapter(topicTypeAdapter);
        SP_topic_create_type.setSelection(1);
    }

    public void setCallBack(TopicCreatedCallback callback) {
        this.callback = callback;
    }

    private void createTopic() {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        dbManager.insertTopic(EDT_topic_create_name.getText().toString(),
                SP_topic_create_type.getSelectedItemPosition(), textColorCode);
        dbManager.closeDB();
    }

    @Override
    public void onColorChange(int colorCode, int viewId) {
        textColorCode = colorCode;
        setTextColor(colorCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_topic_textcolor:
                ColorPickerFragment secColorPickerFragment =
                        ColorPickerFragment.newInstance(textColor);
                secColorPickerFragment.setCallBack(this, R.id.IV_topic_textcolor);
                secColorPickerFragment.show(getFragmentManager(), "topicTextColorPickerFragment");
                break;
            case R.id.But_topic_create_delete:
                callback.TopicDeleted(position);
                break;
            case R.id.But_topic_create_ok:
                if (EDT_topic_create_name.getText().toString().length() > 0) {
                    createTopic();
                    callback.TopicCreated();
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_topic_empty), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.But_topic_create_cancel:
                dismiss();
                break;
        }
    }


}
