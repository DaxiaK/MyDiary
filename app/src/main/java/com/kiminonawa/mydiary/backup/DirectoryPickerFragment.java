package com.kiminonawa.mydiary.backup;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;

public class DirectoryPickerFragment extends FilePickerFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        //Set toolbar
        Toolbar toolbar = (Toolbar) rootView.findViewById(com.nononsenseapps.filepicker.R.id.nnf_picker_toolbar);
        toolbar.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
        //set RecyclerView
        recyclerView.setBackgroundColor(Color.WHITE);

        //set Button
        ((Button) rootView.findViewById(com.nononsenseapps.filepicker.R.id.nnf_button_cancel))
                .setText(getResources().getString(R.string.dialog_button_cancel));
        ((Button) rootView.findViewById(com.nononsenseapps.filepicker.R.id.nnf_button_ok))
                .setText(getResources().getString(R.string.dialog_button_ok));

        return rootView;
    }

    /**
     * For consistency, the top level the back button checks against should be the start path.
     * But it will fall back on /.
     */
    public File getBackTop() {
        return getPath(getArguments().getString(KEY_START_PATH, "/"));
    }

    /**
     * @return true if the current path is the startpath or /
     */
    public boolean isBackTop() {
        return 0 == compareFiles(mCurrentPath, getBackTop()) ||
                0 == compareFiles(mCurrentPath, new File("/"));
    }

    /**
     * Go up on level, same as pressing on "..".
     */
    public void goUp() {
        mCurrentPath = getParent(mCurrentPath);
        mCheckedItems.clear();
        mCheckedVisibleViewHolders.clear();
        refresh(mCurrentPath);
    }
}