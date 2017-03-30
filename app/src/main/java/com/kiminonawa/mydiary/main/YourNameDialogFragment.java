package com.kiminonawa.mydiary.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.PermissionHelper;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ScreenHelper;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.ViewTools;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.kiminonawa.mydiary.shared.PermissionHelper.REQUEST_WRITE_ES_PERMISSION;


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
     * File
     */
    private FileManager tempFileManager;
    private final static int SELECT_PROFILE_PICTURE_BG = 0;
    /**
     * Profile picture
     */
    private String profilePictureFileName = "";
    private boolean isAddNewProfilePicture = false;

    /**
     * UI
     */
    private LinearLayout LL_your_name_content;
    private ImageView IV_your_name_profile_picture, IV_your_name_profile_picture_cancel;
    private EditText EDT_your_name_name;
    private MyDiaryButton But_your_name_ok, But_your_name_cancel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (YourNameCallback) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


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
        LL_your_name_content = (LinearLayout) rootView.findViewById(R.id.LL_your_name_content);
        LL_your_name_content.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        IV_your_name_profile_picture = (ImageView) rootView.findViewById(R.id.IV_your_name_profile_picture);
        IV_your_name_profile_picture.setOnClickListener(this);
        IV_your_name_profile_picture_cancel = (ImageView) rootView.findViewById(R.id.IV_your_name_profile_picture_cancel);
        IV_your_name_profile_picture_cancel.setOnClickListener(this);

        EDT_your_name_name = (EditText) rootView.findViewById(R.id.EDT_your_name_name);
        EDT_your_name_name.setText(SPFManager.getYourName(getActivity()));

        But_your_name_ok = (MyDiaryButton) rootView.findViewById(R.id.But_your_name_ok);
        But_your_name_ok.setOnClickListener(this);
        But_your_name_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_your_name_cancel);
        But_your_name_cancel.setOnClickListener(this);

        loadProfilePicture();
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == PermissionHelper.REQUEST_WRITE_ES_PERMISSION) {
            if (grantResults.length > 0
                    && PermissionHelper.checkAllPermissionResult(grantResults)) {
                FileManager.startBrowseImageFile(this, SELECT_PROFILE_PICTURE_BG);
            } else {
                PermissionHelper.showAddPhotoDialog(getActivity());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PROFILE_PICTURE_BG) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {

                    //Create fileManager for get temp folder
                    tempFileManager = new FileManager(getActivity(), FileManager.TEMP_DIR);
                    tempFileManager.clearDir();
                    //Compute the bg size
                    int photoSize = ScreenHelper.dpToPixel(getResources(), 50);
                    UCrop.Options options = new UCrop.Options();
                    options.setToolbarColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
                    options.setStatusBarColor(ThemeManager.getInstance().getThemeDarkColor(getActivity()));
                    UCrop.of(data.getData(), Uri.fromFile(
                            new File(tempFileManager.getDir() + "/" + FileManager.createRandomFileName())))
                            .withMaxResultSize(photoSize, photoSize)
                            .withAspectRatio(1, 1)
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
                    IV_your_name_profile_picture.setImageBitmap(BitmapFactory.decodeFile(resultUri.getPath()));
                    profilePictureFileName = FileManager.getFileNameByUri(getActivity(), resultUri);
                    isAddNewProfilePicture = true;
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_crop_profile_picture_fail), Toast.LENGTH_LONG).show();
                    //sample error
                    // final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    private void loadProfilePicture() {
        IV_your_name_profile_picture.setImageDrawable(ThemeManager.getInstance().getProfilePictureDrawable(getActivity()));
    }


    private void saveYourName() {
        //Save name
        SPFManager.setYourName(getActivity(), EDT_your_name_name.getText().toString());
        //Save profile picture
        if (isAddNewProfilePicture) {
            //Remove the old file
            FileManager bgFM = new FileManager(getActivity(), FileManager.SETTING_DIR);
            File oldProfilePictureFile = new File(bgFM.getDirAbsolutePath()
                    + "/" + ThemeManager.CUSTOM_PROFILE_PICTURE_FILENAME);
            if (oldProfilePictureFile.exists()) {
                oldProfilePictureFile.delete();
            }
            if (!"".equals(profilePictureFileName)) {
                try {
                    //Copy the profile into setting dir
                    FileManager.copy(
                            new File(tempFileManager.getDirAbsolutePath() + "/" + profilePictureFileName),
                            oldProfilePictureFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.toast_save_profile_picture_fail), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_your_name_profile_picture:
                if (PermissionHelper.checkPermission(this, REQUEST_WRITE_ES_PERMISSION)) {
                    FileManager.startBrowseImageFile(this, SELECT_PROFILE_PICTURE_BG);
                }
                break;
            case R.id.IV_your_name_profile_picture_cancel:
                isAddNewProfilePicture = true;
                profilePictureFileName = "";
                IV_your_name_profile_picture.setImageDrawable(
                        ViewTools.getDrawable(getActivity(), R.drawable.ic_person_picture_default));
                break;
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
