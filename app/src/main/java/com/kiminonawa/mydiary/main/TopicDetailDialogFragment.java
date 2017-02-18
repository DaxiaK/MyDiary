package com.kiminonawa.mydiary.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.main.topic.ITopic;
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
        ColorPickerFragment.colorPickerCallback {


    public interface TopicCreatedCallback {
        void TopicCreated(String topicTitle, int type, int color);

        void TopicUpdated(int position, String newTopicTitle, int color, int topicBgStatus, String newBgFileName);
    }


    /**
     * Callback
     */
    private TopicCreatedCallback callback;
    /**
     * Edit
     */
    //topicBgStatus
    public static final int TOPIC_BG_NORMAL = 0;
    public static final int TOPIC_BG_ADD_PHOTO = 1;
    public static final int TOPIC_BG_REVERT_DEFAULT = 3;

    private boolean isEditMode;
    private int position;
    private String title;
    private long topicId;
    private int topicType;
    private int topicColorCode;
    private int topicBgStatus = TOPIC_BG_NORMAL;
    private String newTopicBgFileName = "";
    /**
     * File
     */
    private final static int SELECT_TOPIC_BG = 0;
    /**
     * UI
     */
    private LinearLayout LL_topic_detail_content;
    private EditText EDT_topic_detail_title;
    private LinearLayout LL_topic_detail_default_bg;
    private RelativeLayout RL_topic_detail_topic_bg;
    private ImageView IV_topic_color, IV_topic_detail_topic_bg;
    private MyDiaryButton But_topic_detail_default_bg;
    private Spinner SP_topic_detail_type;
    private MyDiaryButton But_topic_detail_ok, But_topic_detail_cancel;


    public static TopicDetailDialogFragment newInstance(boolean isEditMode, int position, long topicId,
                                                        String title, int topicType, int topicColorCode) {
        Bundle args = new Bundle();
        TopicDetailDialogFragment fragment = new TopicDetailDialogFragment();
        args.putBoolean("isEditMode", isEditMode);
        args.putInt("position", position);
        args.putString("title", title);
        args.putLong("topicId", topicId);
        args.putInt("topicType", topicType);
        args.putInt("topicColorCode", topicColorCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (TopicCreatedCallback) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isEditMode = getArguments().getBoolean("isEditMode", false);
        position = getArguments().getInt("position", -1);
        title = getArguments().getString("title", "");
        topicId = getArguments().getLong("topicId", -1);
        topicType = getArguments().getInt("topicType", -1);
        topicColorCode = getArguments().getInt("topicColorCode", Color.BLACK);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        //This position is wrong.
        if (isEditMode && (position == -1 || topicId == -1 || topicType == -1)) {
            dismiss();
        }

        View rootView = inflater.inflate(R.layout.dialog_fragment_topic_detail, container);
        LL_topic_detail_content = (LinearLayout) rootView.findViewById(R.id.LL_topic_detail_content);
        LL_topic_detail_content.setBackgroundColor(
                ThemeManager.getInstance().getThemeMainColor(getContext()));

        EDT_topic_detail_title = (EditText) rootView.findViewById(R.id.EDT_topic_detail_title);
        IV_topic_color = (ImageView) rootView.findViewById(R.id.IV_topic_color);
        IV_topic_color.setOnClickListener(this);
        setTextColor(topicColorCode);

        But_topic_detail_ok = (MyDiaryButton) rootView.findViewById(R.id.But_topic_detail_ok);
        But_topic_detail_ok.setOnClickListener(this);
        But_topic_detail_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_topic_detail_cancel);
        But_topic_detail_cancel.setOnClickListener(this);

        if (isEditMode) {
            RL_topic_detail_topic_bg = (RelativeLayout) rootView.findViewById(R.id.RL_topic_detail_topic_bg);
            RL_topic_detail_topic_bg.setVisibility(View.VISIBLE);

            LL_topic_detail_default_bg = (LinearLayout) rootView.findViewById(R.id.LL_topic_detail_default_bg);
            LL_topic_detail_default_bg.setVisibility(View.VISIBLE);

            IV_topic_detail_topic_bg = (ImageView) rootView.findViewById(R.id.IV_topic_detail_topic_bg);
            IV_topic_detail_topic_bg.setImageDrawable(ThemeManager.getInstance().getTopicBgDrawable(getContext(), topicId, topicType));
            IV_topic_detail_topic_bg.setOnClickListener(this);

            But_topic_detail_default_bg = (MyDiaryButton) rootView.findViewById(R.id.But_topic_detail_default_bg);
            But_topic_detail_default_bg.setVisibility(View.VISIBLE);
            But_topic_detail_default_bg.setOnClickListener(this);
            //Check current topic bg is default or not.
            But_topic_detail_default_bg.setEnabled(isTopicHaveCustomBg() ? true : false);

            EDT_topic_detail_title.setText(title);
        } else {
            SP_topic_detail_type = (Spinner) rootView.findViewById(R.id.SP_topic_detail_type);
            SP_topic_detail_type.setVisibility(View.VISIBLE);
            initTopicTypeSpinner();
        }
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
                PermissionHelper.showAddPhotoDialog(getActivity());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_TOPIC_BG) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    int topicBgWidth = ThemeManager.getInstance().getTopicBgWidth(getActivity());
                    int topicBgHeight;

                    if (topicType == ITopic.TYPE_DIARY) {
                        topicBgHeight = ThemeManager.getInstance().getTopicBgHeight(getActivity());
                    } else {
                        topicBgHeight = ThemeManager.getInstance().getTopicBgWithoutEditBarHeight(getActivity());
                    }
                    FileManager tempFileManager = new FileManager(getContext(), FileManager.TEMP_DIR);
                    //Clear the old photo file
                    tempFileManager.clearDir();
                    UCrop.Options options = new UCrop.Options();
                    options.setToolbarColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
                    options.setStatusBarColor(ThemeManager.getInstance().getThemeDarkColor(getActivity()));
                    UCrop.of(data.getData(), Uri.fromFile(new File(tempFileManager.getDir() + "/" + FileManager.createRandomFileName())))
                            .withMaxResultSize(topicBgWidth, topicBgHeight)
                            .withAspectRatio(topicBgWidth, topicBgHeight)
                            .withOptions(options)
                            .start(getActivity(), this);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_photo_intent_error), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri resultUri = UCrop.getOutput(data);
                    IV_topic_detail_topic_bg.setImageBitmap(BitmapFactory.decodeFile(resultUri.getPath()));
                    newTopicBgFileName = FileManager.getFileNameByUri(getActivity(), resultUri);
                    But_topic_detail_default_bg.setEnabled(true);
                    topicBgStatus = TOPIC_BG_ADD_PHOTO;
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_crop_profile_banner_fail), Toast.LENGTH_LONG).show();
                    //sample error
                    // final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    private boolean isTopicHaveCustomBg() {
        File topicBgFile = ThemeManager.getInstance().getTopicBgSavePathFile(
                getActivity(), topicId, topicType);
        if (topicBgFile.exists()) {
            return true;
        }
        return false;
    }

    private void setTextColor(int colorCode) {
        IV_topic_color.setImageDrawable(new ColorDrawable(colorCode));
    }

    private void initTopicTypeSpinner() {
        ArrayAdapter topicTypeAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_simple_text,
                getResources().getStringArray(R.array.topic_type));
        SP_topic_detail_type.setAdapter(topicTypeAdapter);
        SP_topic_detail_type.setSelection(1);
    }


    @Override
    public void onColorChange(int colorCode, int viewId) {
        topicColorCode = colorCode;
        setTextColor(colorCode);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_topic_color:
                ColorPickerFragment secColorPickerFragment =
                        ColorPickerFragment.newInstance(topicColorCode, R.id.IV_topic_color);
                secColorPickerFragment.setTargetFragment(this, 0);
                secColorPickerFragment.show(getFragmentManager(), "topicTextColorPickerFragment");
                break;

            case R.id.IV_topic_detail_topic_bg:
                if (PermissionHelper.checkPermission(this, REQUEST_WRITE_ES_PERMISSION)) {
                    FileManager.startBrowseImageFile(this, SELECT_TOPIC_BG);
                }
                break;
            case R.id.But_topic_detail_default_bg:
                topicBgStatus = TOPIC_BG_REVERT_DEFAULT;
                newTopicBgFileName = "";
                IV_topic_detail_topic_bg.setImageDrawable(
                        ThemeManager.getInstance().getTopicBgDefaultDrawable(getActivity(), topicType));
                But_topic_detail_default_bg.setEnabled(false);
                break;
            case R.id.But_topic_detail_ok:
                if (isEditMode) {
                    if (EDT_topic_detail_title.getText().toString().length() > 0) {
                        callback.TopicUpdated(position, EDT_topic_detail_title.getText().toString(),
                                topicColorCode, topicBgStatus, newTopicBgFileName);
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_topic_empty), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (EDT_topic_detail_title.getText().toString().length() > 0) {
                        callback.TopicCreated(EDT_topic_detail_title.getText().toString(),
                                SP_topic_detail_type.getSelectedItemPosition(), topicColorCode);
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_topic_empty), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.But_topic_detail_cancel:
                dismiss();
                break;
        }
    }


}
