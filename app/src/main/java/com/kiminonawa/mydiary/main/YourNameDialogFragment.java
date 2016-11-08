package com.kiminonawa.mydiary.main;

import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ThemeManager;


/**
 * Created by daxia on 2016/8/27.
 */
public class YourNameDialogFragment extends DialogFragment implements View.OnClickListener {


    public interface YourNameCallback {
        void updateName();
    }


    /**
     * Callback
     */
    private YourNameCallback callback;
    /**
     * UI
     */
    private EditText EDT_your_name_name;
    private Button But_your_name_ok, But_your_name_cancel;

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
        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.dialog_fragment_your_name, container);
        EDT_your_name_name = (EditText) rootView.findViewById(R.id.EDT_your_name_name);
        EDT_your_name_name.getBackground().mutate().setColorFilter(ThemeManager.getInstance().getThemeMainColor(getActivity()),
                PorterDuff.Mode.SRC_ATOP);

        But_your_name_ok = (Button) rootView.findViewById(R.id.But_your_name_ok);
        But_your_name_ok.setOnClickListener(this);
        But_your_name_cancel = (Button) rootView.findViewById(R.id.But_your_name_cancel);
        But_your_name_cancel.setOnClickListener(this);
        return rootView;
    }


    public void setCallBack(YourNameCallback callback) {
        this.callback = callback;
    }

    private void saveYourName() {
        SPFManager.setYourName(getActivity(), EDT_your_name_name.getText().toString());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_your_name_ok:
                saveYourName();
                callback.updateName();
                dismiss();
                break;
            case R.id.But_your_name_cancel:
                dismiss();
                break;
        }
    }


}
