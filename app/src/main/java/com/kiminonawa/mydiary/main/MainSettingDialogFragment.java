package com.kiminonawa.mydiary.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ThemeManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by daxia on 2016/11/24.
 */

public class MainSettingDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {


    /**
     * UI
     */

    private RelativeLayout RL_main_setting_dialog;
    private ImageView IV_main_setting_change_theme, IV_main_setting_add_topic;
    private ThemeManager themeManager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        themeManager = ThemeManager.getInstance();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.bottom_sheet_main_setting, container);

        RL_main_setting_dialog = (RelativeLayout) rootView.findViewById(R.id.RL_main_setting_dialog);
        RL_main_setting_dialog.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        IV_main_setting_change_theme = (ImageView) rootView.findViewById(R.id.IV_main_setting_change_theme);
        IV_main_setting_change_theme.setOnClickListener(this);
        IV_main_setting_add_topic = (ImageView) rootView.findViewById(R.id.IV_main_setting_add_topic);
        IV_main_setting_add_topic.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_main_setting_add_topic:
                CreateTopicDialogFragment createTopicDialogFragment = new CreateTopicDialogFragment();
                createTopicDialogFragment.setCallBack((MainActivity) getActivity());
                createTopicDialogFragment.show(getFragmentManager(), "createTopicDialogFragment");
                break;
            case R.id.IV_main_setting_change_theme:
                ThemeManager.getInstance().toggleTheme(getActivity());
                //Send Toast
                Toast.makeText(getActivity(), getString(R.string.toast_change_theme), Toast.LENGTH_SHORT).show();
                //Restart App
                Intent i = getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
                getActivity().finish();
                startActivity(i);
                break;
        }
    }
}
