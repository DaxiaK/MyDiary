package com.kiminonawa.mydiary.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;


/**
 * Created by daxia on 2016/8/27.
 */
public class ReleaseNoteDialogFragment extends DialogFragment implements View.OnClickListener {


    /**
     * UI
     */
    private RelativeLayout RL_release_note;
    private TextView TV_release_note_text;
    private CheckedTextView CTV_release_note_knew;
    private MyDiaryButton But_release_note_ok;


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
        View rootView = inflater.inflate(R.layout.dialog_fragment_release_note, container);

        RL_release_note = (RelativeLayout) rootView.findViewById(R.id.RL_release_note);
        RL_release_note.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        TV_release_note_text = (TextView) rootView.findViewById(R.id.TV_release_note_text);
        TV_release_note_text.setText(getString(R.string.release_note));

        CTV_release_note_knew = (CheckedTextView) rootView.findViewById(R.id.CTV_release_note_knew);
        CTV_release_note_knew.setOnClickListener(this);

        But_release_note_ok = (MyDiaryButton) rootView.findViewById(R.id.But_release_note_ok);
        But_release_note_ok.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CTV_release_note_knew:
                CTV_release_note_knew.toggle();
                break;
            case R.id.But_release_note_ok:
                SPFManager.setReleaseNoteClose(getActivity(),!CTV_release_note_knew.isChecked());
                dismiss();
                break;
        }
    }


}
