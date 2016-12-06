package com.kiminonawa.mydiary.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.gui.CommonDialogFragment;

/**
 * Created by daxia on 2016/11/14.
 */

public class TopicDeleteDialogFragment extends CommonDialogFragment {

    private DeleteCallback callback;

    /**
     * Callback
     */
    public interface DeleteCallback {
        void onTopicDelete();
    }

    private String topicTitle;


    public static TopicDeleteDialogFragment newInstance(String topicTitle) {
        Bundle args = new Bundle();
        TopicDeleteDialogFragment fragment = new TopicDeleteDialogFragment();
        args.putString("topicTitle", topicTitle);
        fragment.setArguments(args);
        return fragment;
    }


    public void setCallBack(DeleteCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        super.onViewCreated(view, savedInstanceState);
        topicTitle = getArguments().getString("topicTitle", "");
        this.TV_common_content.setText(
                String.format(getResources().getString(R.string.topic_dialog_delete_content), topicTitle));
    }

    @Override
    protected void okButtonEvent() {
        this.callback.onTopicDelete();
        dismiss();
    }

    @Override
    protected void cancelButtonEvent() {
        dismiss();
    }
}
