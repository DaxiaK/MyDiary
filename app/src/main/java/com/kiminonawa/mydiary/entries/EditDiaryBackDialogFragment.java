package com.kiminonawa.mydiary.entries;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.gui.CommonDialogFragment;

/**
 * Created by daxia on 2016/11/14.
 */

public class EditDiaryBackDialogFragment extends CommonDialogFragment {


    /**
     * Callback
     */
    public interface BackDialogCallback {
        void onBack();
    }

    private BackDialogCallback callback;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        callback = (BackDialogCallback) getTargetFragment();
        this.getDialog().setCanceledOnTouchOutside(true);
        super.onViewCreated(view, savedInstanceState);
        this.TV_common_content.setText(getString(R.string.diary_back_message));
    }

    @Override
    protected void okButtonEvent() {
        callback.onBack();
        dismiss();
    }

    @Override
    protected void cancelButtonEvent() {
        dismiss();
    }
}
