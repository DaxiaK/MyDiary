package com.kiminonawa.mydiary.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.gui.CommonDialogFragment;


/**
 * Created by daxia on 2016/8/27.
 */
public class OOBEFirstTimeDialogFragment extends CommonDialogFragment implements View.OnClickListener {

    /**
     * Callback
     */
    public interface FirstTimeDialogCallback {
        void onAdd();
    }

    private FirstTimeDialogCallback callback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (FirstTimeDialogCallback) context;
        } catch (ClassCastException e) {
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        super.onViewCreated(view, savedInstanceState);
        this.TV_common_content.setText(getString(R.string.oobe_firsttime_add_title));
    }

    @Override
    protected void okButtonEvent() {
        if (callback != null) {
            callback.onAdd();
        }
        //Make First time tag is false
        SPFManager.setOOBEFirstTime(getActivity(),false);
        dismiss();
    }

    @Override
    protected void cancelButtonEvent() {
        //Make First time tag is null
        SPFManager.setOOBEFirstTime(getActivity(),false);
        dismiss();
    }
}
