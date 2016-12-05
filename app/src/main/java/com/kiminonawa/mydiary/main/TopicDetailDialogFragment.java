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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.setting.ColorPickerFragment;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;


/**
 * Created by daxia on 2016/8/27.
 */
public class TopicDetailDialogFragment extends DialogFragment implements View.OnClickListener,
        ColorPickerFragment.colorPickerCallback, TopicDeleteDialogFragment.DeleteCallback {


    public interface TopicCreatedCallback {
        void TopicCreated();

        void TopicDeleted(int position);

        void TopicUpdated(int position, String name, int color);
    }


    /**
     * Callback
     */
    private TopicCreatedCallback callback;
    /**
     * Edit
     */
    private boolean isEditMode;
    private int position;
    private String title;
    private int topicColorCode;
    /**
     * UI
     */
    private LinearLayout LL_topic_dialog_content;
    private EditText EDT_topic_create_title;
    private ImageView IV_topic_color;
    private MyDiaryButton But_topic_create_ok, But_topic_create_cancel, But_topic_create_delete;
    private Spinner SP_topic_create_type;


    public static TopicDetailDialogFragment newInstance(boolean isEditMode, int position, String title, int topicColorCode) {
        Bundle args = new Bundle();
        TopicDetailDialogFragment fragment = new TopicDetailDialogFragment();
        args.putBoolean("isEditMode", isEditMode);
        args.putInt("position", position);
        args.putString("title", title);
        args.putInt("topicColorCode", topicColorCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isEditMode = getArguments().getBoolean("isEditMode", false);
        position = getArguments().getInt("position", -1);
        title = getArguments().getString("title", "");
        topicColorCode = getArguments().getInt("topicColorCode", Color.BLACK);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        //This position is wrong.
        if (isEditMode && position == -1) {
            dismiss();
        }

        View rootView = inflater.inflate(R.layout.dialog_fragment_topic_detail, container);
        LL_topic_dialog_content = (LinearLayout) rootView.findViewById(R.id.LL_topic_dialog_content);
        LL_topic_dialog_content.setBackgroundColor(
                ThemeManager.getInstance().getThemeMainColor(getContext()));

        EDT_topic_create_title = (EditText) rootView.findViewById(R.id.EDT_topic_create_title);
        IV_topic_color = (ImageView) rootView.findViewById(R.id.IV_topic_color);
        IV_topic_color.setOnClickListener(this);
        SP_topic_create_type = (Spinner) rootView.findViewById(R.id.SP_topic_create_type);

        But_topic_create_ok = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_ok);
        But_topic_create_ok.setOnClickListener(this);
        But_topic_create_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_cancel);
        But_topic_create_cancel.setOnClickListener(this);
        if (isEditMode) {
            EDT_topic_create_title.setText(title);
            SP_topic_create_type.setVisibility(View.GONE);
            But_topic_create_delete = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_delete);
            But_topic_create_delete.setVisibility(View.VISIBLE);
            But_topic_create_delete.setOnClickListener(this);
        } else {
            initTopicTypeSpinner();
        }
        setTextColor(topicColorCode);
        return rootView;
    }

    private void setTextColor(int colorCode) {
        IV_topic_color.setImageDrawable(new ColorDrawable(colorCode));
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
        dbManager.insertTopic(EDT_topic_create_title.getText().toString(),
                SP_topic_create_type.getSelectedItemPosition(), topicColorCode);
        dbManager.closeDB();
    }


    @Override
    public void onColorChange(int colorCode, int viewId) {
        topicColorCode = colorCode;
        setTextColor(colorCode);
    }

    @Override
    public void onTopicDelete() {
        callback.TopicDeleted(position);
        dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_topic_color:
                ColorPickerFragment secColorPickerFragment =
                        ColorPickerFragment.newInstance(topicColorCode);
                secColorPickerFragment.setCallBack(this, R.id.IV_topic_color);
                secColorPickerFragment.show(getFragmentManager(), "topicTextColorPickerFragment");
                break;
            case R.id.But_topic_create_delete:
                TopicDeleteDialogFragment topicDeleteDialogFragment = TopicDeleteDialogFragment.newInstance(title);
                topicDeleteDialogFragment.setCallBack(this);
                topicDeleteDialogFragment.show(getFragmentManager(), "topicDeleteDialogFragment");
                break;
            case R.id.But_topic_create_ok:
                if (isEditMode) {
                    if (EDT_topic_create_title.getText().toString().length() > 0) {
                        callback.TopicUpdated(position, EDT_topic_create_title.getText().toString(), topicColorCode);
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_topic_empty), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (EDT_topic_create_title.getText().toString().length() > 0) {
                        createTopic();
                        callback.TopicCreated();
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_topic_empty), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.But_topic_create_cancel:
                dismiss();
                break;
        }
    }


}
