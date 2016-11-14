package com.kiminonawa.mydiary.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;


/**
 * Created by daxia on 2016/8/27.
 */
public class CreateTopicDialogFragment extends DialogFragment implements View.OnClickListener {


    public interface TopicCreatedCallback {
        void TopicCreated();
    }


    /**
     * Callback
     */
    private TopicCreatedCallback callback;
    /**
     * UI
     */
    private EditText EDT_topic_create_name;
    private MyDiaryButton But_topic_create_ok, But_topic_create_cancel;
    private Spinner SP_topic_create_type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        View rootView = inflater.inflate(R.layout.dialog_fragment_create_topic, container);
        EDT_topic_create_name = (EditText) rootView.findViewById(R.id.EDT_topic_create_name);
        SP_topic_create_type = (Spinner) rootView.findViewById(R.id.SP_topic_create_type);

        But_topic_create_ok = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_ok);
        But_topic_create_ok.setOnClickListener(this);
        But_topic_create_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_cancel);
        But_topic_create_cancel.setOnClickListener(this);
        initTopicTypeSpinner();
        return rootView;
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
        dbManager.insertTopic(EDT_topic_create_name.getText().toString(), SP_topic_create_type.getSelectedItemPosition());
        dbManager.closeDB();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_topic_create_ok:
                createTopic();
                callback.TopicCreated();
                dismiss();
                break;
            case R.id.But_topic_create_cancel:
                dismiss();
                break;
        }
    }


}
