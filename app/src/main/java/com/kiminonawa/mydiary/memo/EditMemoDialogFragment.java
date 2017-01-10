package com.kiminonawa.mydiary.memo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;

/**
 * Created by daxia on 2016/10/27.
 */

public class EditMemoDialogFragment extends DialogFragment implements View.OnClickListener {


    /**
     * Callback
     */
    public interface MemoCallback {
        void addMemo(String memoContent);

        void updateMemo();
    }

    private MemoCallback callback;
    /**
     * UI
     */
    private MyDiaryButton But_edit_memo_ok, But_edit_memo_cancel;
    private EditText EDT_edit_memo_content;

    /**
     * Info
     */
    private long topicId = -1;
    //default = -1 , it means add memo.
    private long memoId = -1;
    private boolean isAdd = true;
    private String memoContent = "";

    //TODO Make this dialog's background has radius.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static EditMemoDialogFragment newInstance(long topicId, long memoId, boolean isAdd, String memoContent) {
        Bundle args = new Bundle();
        EditMemoDialogFragment fragment = new EditMemoDialogFragment();
        args.putLong("topicId", topicId);
        args.putLong("memoId", memoId);
        args.putBoolean("isAdd", isAdd);
        args.putString("memoContent", memoContent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (MemoCallback) context;
        } catch (ClassCastException e) {
        }
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
        View rootView = inflater.inflate(R.layout.dialog_fragment_edit_memo, container);
        EDT_edit_memo_content = (EditText) rootView.findViewById(R.id.EDT_edit_memo_content);
        But_edit_memo_ok = (MyDiaryButton) rootView.findViewById(R.id.But_edit_memo_ok);
        But_edit_memo_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_edit_memo_cancel);

        EDT_edit_memo_content.getBackground().mutate().setColorFilter(
                ThemeManager.getInstance().getThemeMainColor(getActivity()), PorterDuff.Mode.SRC_ATOP);
        EDT_edit_memo_content.setTextColor(ThemeManager.getInstance().getThemeDarkColor(getActivity()));
        But_edit_memo_ok.setOnClickListener(EditMemoDialogFragment.this);
        But_edit_memo_cancel.setOnClickListener(EditMemoDialogFragment.this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topicId = getArguments().getLong("topicId", -1L);
        memoId = getArguments().getLong("memoId", -1L);
        isAdd = getArguments().getBoolean("isAdd", true);
        memoContent = getArguments().getString("memoContent", "");
        EDT_edit_memo_content.setText(memoContent);
        //For show keyboard
        EDT_edit_memo_content.requestFocus();
        getDialog().getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }


    private void addMemo() {
        if (topicId != -1) {
            callback.addMemo(EDT_edit_memo_content.getText().toString());
        }
    }

    private void updateMemo() {
        if (memoId != -1) {
            DBManager dbManager = new DBManager(getActivity());
            dbManager.opeDB();
            dbManager.updateMemoContent(memoId, EDT_edit_memo_content.getText().toString());
            dbManager.closeDB();
        }
    }

    private void okButtonEvent() {

        if (EDT_edit_memo_content.getText().toString().length() > 0) {
            if (isAdd) {
                addMemo();
            } else {
                updateMemo();
                callback.updateMemo();
            }
            dismiss();
        } else {
            Toast.makeText(getActivity(), getString(R.string.toast_memo_empty), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.But_edit_memo_ok:
                okButtonEvent();
                break;
            case R.id.But_edit_memo_cancel:
                dismiss();
                break;
        }
    }
}
