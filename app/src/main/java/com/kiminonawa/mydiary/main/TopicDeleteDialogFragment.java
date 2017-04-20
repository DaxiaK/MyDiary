package com.kiminonawa.mydiary.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared. gui.CommonDialogFragment;

/**
 * Created by daxia on 2016/11/14.
 */

public class TopicDeleteDialogFragment extends CommonDialogFragment {

    private DeleteCallback callback;

    /**
     * Callback
     */
    public interface DeleteCallback {
        void onTopicDelete(int position);
    }

    private int position;
    private String topicTitle;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DeleteCallback) context;
        } catch (ClassCastException e) {
        }
    }

    public static TopicDeleteDialogFragment newInstance(int position, String topicTitle) {
        Bundle args = new Bundle();
        TopicDeleteDialogFragment fragment = new TopicDeleteDialogFragment();
        args.putInt("position", position);
        args.putString("topicTitle", topicTitle);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        super.onViewCreated(view, savedInstanceState);
        position = getArguments().getInt("position", -1);
        if (position == -1) {
            dismiss();
        }
        topicTitle = getArguments().getString("topicTitle", "");
        this.TV_common_content.setText(
                String.format(getResources().getString(R.string.topic_dialog_delete_content), topicTitle));

    }

    @Override
    protected void okButtonEvent() {
        this.callback.onTopicDelete(position);
        dismiss();
    }

    @Override
    protected void cancelButtonEvent() {
        dismiss();
    }
}
