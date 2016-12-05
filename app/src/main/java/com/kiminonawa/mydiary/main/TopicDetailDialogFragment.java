package com.kiminonawa.mydiary.main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.setting.ColorPickerFragment;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.PermissionHelper;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.kiminonawa.mydiary.shared.PermissionHelper.REQUEST_WRITE_ES_PERMISSION;


/**
 * Created by daxia on 2016/8/27.
 */
public class TopicDetailDialogFragment extends DialogFragment implements View.OnClickListener,
        ColorPickerFragment.colorPickerCallback, TopicDeleteDialogFragment.DeleteCallback {


    public interface TopicCreatedCallback {
        void TopicCreated();

        void TopicDeleted(int position);

        void TopicUpdated(int position, String newTopicTitle, int color, boolean addNewBg, String newBgFileName);
    }


    /**
     * Callback
     */
    private TopicCreatedCallback callback;
    /**
     * Edit
     */
    private boolean isEditMode;
    private int position;
    private String title;
    private int topicType;
    private int topicColorCode;
    private boolean addNewBg = false;
    private String newTopicBgFileName = "";
    /**
     * File
     */
    private final static int SELECT_TOPIC_BG = 0;
    /**
     * UI
     */
    private LinearLayout LL_topic_dialog_content;
    private EditText EDT_topic_create_title;
    private ImageView IV_topic_color;
    private MyDiaryButton But_topic_detail_change_bg, But_topic_detail_default_bg;
    private Spinner SP_topic_create_type;
    private MyDiaryButton But_topic_create_ok, But_topic_create_cancel, But_topic_create_delete;


    public static TopicDetailDialogFragment newInstance(boolean isEditMode, int position, String title, int topicType, int topicColorCode) {
        Bundle args = new Bundle();
        TopicDetailDialogFragment fragment = new TopicDetailDialogFragment();
        args.putBoolean("isEditMode", isEditMode);
        args.putInt("position", position);
        args.putString("title", title);
        args.putInt("topicType", topicType);
        args.putInt("topicColorCode", topicColorCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isEditMode = getArguments().getBoolean("isEditMode", false);
        position = getArguments().getInt("position", -1);
        title = getArguments().getString("title", "");
        topicType = getArguments().getInt("topicType", -1);
        topicColorCode = getArguments().getInt("topicColorCode", Color.BLACK);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        //This position is wrong.
        if (isEditMode && (position == -1 || topicType == -1)) {
            dismiss();
        }

        View rootView = inflater.inflate(R.layout.dialog_fragment_topic_detail, container);
        LL_topic_dialog_content = (LinearLayout) rootView.findViewById(R.id.LL_topic_dialog_content);
        LL_topic_dialog_content.setBackgroundColor(
                ThemeManager.getInstance().getThemeMainColor(getContext()));

        EDT_topic_create_title = (EditText) rootView.findViewById(R.id.EDT_topic_create_title);
        IV_topic_color = (ImageView) rootView.findViewById(R.id.IV_topic_color);
        IV_topic_color.setOnClickListener(this);
        But_topic_detail_change_bg = (MyDiaryButton) rootView.findViewById(R.id.But_topic_detail_change_bg);
        But_topic_detail_default_bg = (MyDiaryButton) rootView.findViewById(R.id.But_topic_detail_default_bg);
        SP_topic_create_type = (Spinner) rootView.findViewById(R.id.SP_topic_create_type);

        But_topic_create_ok = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_ok);
        But_topic_create_ok.setOnClickListener(this);
        But_topic_create_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_cancel);
        But_topic_create_cancel.setOnClickListener(this);
        if (isEditMode) {

            But_topic_detail_change_bg.setVisibility(View.VISIBLE);
            But_topic_detail_default_bg.setVisibility(View.VISIBLE);
            But_topic_detail_change_bg.setOnClickListener(this);
            But_topic_detail_default_bg.setOnClickListener(this);

            EDT_topic_create_title.setText(title);
            But_topic_create_delete = (MyDiaryButton) rootView.findViewById(R.id.But_topic_create_delete);
            But_topic_create_delete.setVisibility(View.VISIBLE);
            But_topic_create_delete.setOnClickListener(this);
        } else {
            SP_topic_create_type.setVisibility(View.VISIBLE);
            initTopicTypeSpinner();
        }
        setTextColor(topicColorCode);
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == PermissionHelper.REQUEST_WRITE_ES_PERMISSION) {
            if (grantResults.length > 0
                    && PermissionHelper.checkAllPermissionResult(grantResults)) {
                FileManager.startBrowseImageFile(this, SELECT_TOPIC_BG);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.diary_location_permission_title))
                        .setMessage(getString(R.string.diary_photo_permission_content))
                        .setPositiveButton(getString(R.string.dialog_button_ok), null);
                builder.show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_TOPIC_BG) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    int topicBgWidth = ThemeManager.getInstance().getTopicBgWidth();
                    int topicBgHeight;

                    if (topicType == ITopic.TYPE_DIARY) {
                        topicBgHeight = ThemeManager.getInstance().getTopicBgHeight();
                    } else {
                        topicBgHeight = ThemeManager.getInstance().getTopicBgWithoutEditBarHeight();
                    }
                    FileManager tempFileManager = new FileManager(getContext(), FileManager.TEMP_DIR);
                    UCrop.of(data.getData(), Uri.fromFile(new File(tempFileManager.getDiaryDir() + "/" + FileManager.createRandomFileName())))
                            .withMaxResultSize(topicBgWidth, topicBgHeight)
                            .withAspectRatio(topicBgWidth, topicBgHeight)
                            .start(getActivity(), this);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_photo_intent_error), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri resultUri = UCrop.getOutput(data);
                    newTopicBgFileName = FileManager.getFileNameByUri(getActivity(), resultUri);
                    addNewBg = true;
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_crop_profile_banner_fail), Toast.LENGTH_LONG).show();
                    //sample error
                    // final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    private void setTextColor(int colorCode) {
        IV_topic_color.setImageDrawable(new ColorDrawable(colorCode));
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
        dbManager.insertTopic(EDT_topic_create_title.getText().toString(),
                SP_topic_create_type.getSelectedItemPosition(), topicColorCode);
        dbManager.closeDB();
    }


    @Override
    public void onColorChange(int colorCode, int viewId) {
        topicColorCode = colorCode;
        setTextColor(colorCode);
    }

    @Override
    public void onTopicDelete() {
        callback.TopicDeleted(position);
        dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_topic_color:
                ColorPickerFragment secColorPickerFragment =
                        ColorPickerFragment.newInstance(topicColorCode);
                secColorPickerFragment.setCallBack(this, R.id.IV_topic_color);
                secColorPickerFragment.show(getFragmentManager(), "topicTextColorPickerFragment");
                break;

            case R.id.But_topic_detail_change_bg:
                if (PermissionHelper.checkPermission(this, REQUEST_WRITE_ES_PERMISSION)) {
                    FileManager.startBrowseImageFile(this, SELECT_TOPIC_BG);
                }
                break;
            case R.id.But_topic_detail_default_bg:
                addNewBg = false;
                newTopicBgFileName = "";
                break;
            case R.id.But_topic_create_delete:
                TopicDeleteDialogFragment topicDeleteDialogFragment = TopicDeleteDialogFragment.newInstance(title);
                topicDeleteDialogFragment.setCallBack(this);
                topicDeleteDialogFragment.show(getFragmentManager(), "topicDeleteDialogFragment");
                break;
            case R.id.But_topic_create_ok:
                if (isEditMode) {
                    if (EDT_topic_create_title.getText().toString().length() > 0) {
                        callback.TopicUpdated(position, EDT_topic_create_title.getText().toString(),
                                topicColorCode, addNewBg, newTopicBgFileName);
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_topic_empty), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (EDT_topic_create_title.getText().toString().length() > 0) {
                        createTopic();
                        callback.TopicCreated();
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_topic_empty), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.But_topic_create_cancel:
                dismiss();
                break;
        }
    }


}
