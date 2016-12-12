package com.kiminonawa.mydiary.entries.entries;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.BackDialogFragment;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.entries.diary.CopyPhotoTask;
import com.kiminonawa.mydiary.entries.diary.DiaryInfoHelper;
import com.kiminonawa.mydiary.entries.diary.DiaryPhotoBottomSheet;
import com.kiminonawa.mydiary.entries.diary.ImageArrayAdapter;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.entries.diary.item.DiaryPhoto;
import com.kiminonawa.mydiary.entries.diary.item.DiaryText;
import com.kiminonawa.mydiary.entries.diary.item.DiaryTextTag;
import com.kiminonawa.mydiary.entries.diary.item.IDairyRow;
import com.kiminonawa.mydiary.entries.diary.picker.DatePickerFragment;
import com.kiminonawa.mydiary.entries.diary.picker.TimePickerFragment;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.PermissionHelper;
import com.kiminonawa.mydiary.shared.ScreenHelper;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.TimeTools;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.kiminonawa.mydiary.shared.PermissionHelper.REQUEST_CAMERA_AND_WRITE_ES_PERMISSION;

/**
 * Created by daxia on 2016/10/27.
 */

public class DiaryViewerDialogFragment extends DialogFragment implements View.OnClickListener,
        DiaryDeleteDialogFragment.DeleteCallback, CopyDiaryToEditCacheTask.EditTaskCallBack,
        DiaryPhotoBottomSheet.PhotoCallBack, CopyPhotoTask.CopyPhotoCallBack,
        UpdateDiaryTask.UpdateDiaryCallBack, BackDialogFragment.BackDialogCallback,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    /**
     * Callback
     */
    public interface DiaryViewerCallback {
        void deleteDiary();

        void updateDiary();
    }

    private DiaryViewerCallback callback;

    private static final String TAG = "DiaryViewer";

    /**
     * UI
     */
    private RelativeLayout RL_diary_info, RL_diary_edit_bar;
    private ProgressBar PB_diary_item_content_hint;
    private LinearLayout LL_diary_time_information;

    private TextView TV_diary_month, TV_diary_date, TV_diary_day, TV_diary_time, TV_diary_location;
    private ImageView IV_diary_weather, IV_diary_mood;
    private Spinner SP_diary_weather, SP_diary_mood;

    private EditText EDT_diary_title;
    private LinearLayout LL_diary_item_content;
    private ImageView IV_diary_close_dialog, IV_diary_location, IV_diary_photo,
            IV_diary_delete, IV_diary_clear, IV_diary_save;
    private boolean isEditMode;

    /**
     * diary content & info
     */
    private long diaryId;
    private DiaryItemHelper diaryItemHelper;
    private FileManager diaryFileManager;

    /**
     * Edit Mode
     */
    private CopyDiaryToEditCacheTask mTask;
    private Handler delayHandler;
    private boolean initHandlerOrTaskIsRunning = false;

    private Calendar calendar;
    private TimeTools timeTools;
    private SimpleDateFormat sdf;

    /**
     * Permission
     */
    private boolean firstAllowCameraPermission = false;

    //TODO Make this dialog's background has radius.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static DiaryViewerDialogFragment newInstance(long diaryId, boolean isEditMode) {
        Bundle args = new Bundle();
        DiaryViewerDialogFragment fragment = new DiaryViewerDialogFragment();
        args.putLong("diaryId", diaryId);
        args.putBoolean("isEditMode", isEditMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (isEditMode) {
                    BackDialogFragment backDialogFragment = new BackDialogFragment();
                    backDialogFragment.setCallBack(DiaryViewerDialogFragment.this);
                    backDialogFragment.show(getFragmentManager(), "backDialogFragment");
                } else {
                    super.onBackPressed();
                }
            }
        };
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        isEditMode = getArguments().getBoolean("isEditMode", false);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        View rootView = inflater.inflate(R.layout.fragment_diary, container);

        RL_diary_info = (RelativeLayout) rootView.findViewById(R.id.RL_diary_info);
        RL_diary_edit_bar = (RelativeLayout) rootView.findViewById(R.id.RL_diary_edit_bar);
        RL_diary_info.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
        RL_diary_edit_bar.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        PB_diary_item_content_hint = (ProgressBar) rootView.findViewById(R.id.PB_diary_item_content_hint);

        EDT_diary_title = (EditText) rootView.findViewById(R.id.EDT_diary_title);
        //For hidden hint
        EDT_diary_title.setText(" ");
        EDT_diary_title.getBackground().mutate().setColorFilter(ThemeManager.getInstance().getThemeMainColor(getActivity()),
                PorterDuff.Mode.SRC_ATOP);

        TV_diary_month = (TextView) rootView.findViewById(R.id.TV_diary_month);
        TV_diary_date = (TextView) rootView.findViewById(R.id.TV_diary_date);
        TV_diary_day = (TextView) rootView.findViewById(R.id.TV_diary_day);
        TV_diary_time = (TextView) rootView.findViewById(R.id.TV_diary_time);
        TV_diary_location = (TextView) rootView.findViewById(R.id.TV_diary_location);

        LL_diary_item_content = (LinearLayout) rootView.findViewById(R.id.LL_diary_item_content);

        IV_diary_close_dialog = (ImageView) rootView.findViewById(R.id.IV_diary_close_dialog);
        IV_diary_close_dialog.setVisibility(View.VISIBLE);
        IV_diary_close_dialog.setOnClickListener(this);

        IV_diary_location = (ImageView) rootView.findViewById(R.id.IV_diary_location);

        IV_diary_photo = (ImageView) rootView.findViewById(R.id.IV_diary_photo);
        IV_diary_delete = (ImageView) rootView.findViewById(R.id.IV_diary_delete);
        IV_diary_clear = (ImageView) rootView.findViewById(R.id.IV_diary_clear);
        IV_diary_save = (ImageView) rootView.findViewById(R.id.IV_diary_save);

        initView(rootView);
        diaryItemHelper = new DiaryItemHelper(LL_diary_item_content);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        diaryId = getArguments().getLong("diaryId", -1L);
        if (diaryId != -1) {
            if (isEditMode) {
                diaryFileManager = new FileManager(getActivity(), FileManager.DIARY_EDIT_CACHE_DIR);
                diaryFileManager.clearDiaryDir();
                PB_diary_item_content_hint.setVisibility(View.VISIBLE);
                mTask = new CopyDiaryToEditCacheTask(getActivity(), diaryFileManager, this);
                //Make ths ProgressBar show 1s+.
                delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Copy the file into editCash
                        mTask.execute(((DiaryActivity) getActivity()).getTopicId(), diaryId);
                    }
                }, 1000);
                initHandlerOrTaskIsRunning = true;
            } else {
                diaryFileManager = new FileManager(getActivity(), ((DiaryActivity) getActivity()).getTopicId(), diaryId);
                initData();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == PermissionHelper.REQUEST_CAMERA_AND_WRITE_ES_PERMISSION) {
            if (grantResults.length > 0
                    && PermissionHelper.checkAllPermissionResult(grantResults)) {
                firstAllowCameraPermission = true;
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
    public void onResume() {
        super.onResume();
        //For PermissionsResult
        if (firstAllowCameraPermission) {
            openPhotoBottomSheet();
            firstAllowCameraPermission = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //Modify dialog size
        Dialog dialog = getDialog();
        if (dialog != null) {
            int dialogHeight = ScreenHelper.getScreenHeight(getActivity()) -
                    ScreenHelper.dpToPixel(getActivity().getResources(), 2 * 30);
            int dialogWidth = ScreenHelper.getScreenWidth(getActivity()) -
                    ScreenHelper.dpToPixel(getActivity().getResources(), 2 * 15);
            dialog.getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (initHandlerOrTaskIsRunning) {
            if (delayHandler != null) {
                delayHandler.removeCallbacksAndMessages(null);
            }
            if (mTask != null) {
                mTask.cancel(true);
            }
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    private void initData() {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        Cursor diaryInfoCursor = dbManager.selectDiaryInfoByDiaryId(diaryId);
        EDT_diary_title.setText(diaryInfoCursor.getString(2));
        //load Time
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(diaryInfoCursor.getLong(1));
        timeTools = TimeTools.getInstance(getActivity().getApplicationContext());
        sdf = new SimpleDateFormat("HH:mm");
        setDiaryTime();
        if (isEditMode) {
            //Allow to edit diary
            LL_diary_time_information.setOnClickListener(this);
        }
        //load location
        String locationName = diaryInfoCursor.getString(7);
        if (locationName != null && !"".equals(locationName)) {
            TV_diary_location.setText(locationName);
            IV_diary_location.setImageResource(R.drawable.ic_location_on_white_24dp);
        } else {
            TV_diary_location.setText(getActivity().getString(R.string.diary_no_location));
            IV_diary_location.setImageResource(R.drawable.ic_location_off_white_24dp);
        }
        setIcon(diaryInfoCursor.getInt(3), diaryInfoCursor.getInt(4));
        diaryInfoCursor.close();
        //Get diary detail
        loadDiaryItemContent(dbManager);
        dbManager.closeDB();
    }

    private void initView(View rootView) {
        if (isEditMode) {

            LL_diary_time_information = (LinearLayout) rootView.findViewById(R.id.LL_diary_time_information);
            SP_diary_mood = (Spinner) rootView.findViewById(R.id.SP_diary_mood);
            SP_diary_mood.setVisibility(View.VISIBLE);
            SP_diary_weather = (Spinner) rootView.findViewById(R.id.SP_diary_weather);
            SP_diary_weather.setVisibility(View.VISIBLE);

            initMoodSpinner();
            initWeatherSpinner();

            IV_diary_delete.setVisibility(View.GONE);
            IV_diary_clear.setVisibility(View.GONE);
        } else {
            IV_diary_weather = (ImageView) rootView.findViewById(R.id.IV_diary_weather);
            IV_diary_weather.setVisibility(View.VISIBLE);
            IV_diary_mood = (ImageView) rootView.findViewById(R.id.IV_diary_mood);
            IV_diary_mood.setVisibility(View.VISIBLE);

            IV_diary_delete.setOnClickListener(this);
            IV_diary_clear.setVisibility(View.GONE);
            IV_diary_save.setVisibility(View.GONE);

            IV_diary_photo.setColorFilter(R.color.button_disable_color);
            EDT_diary_title.setFocusable(false);
            EDT_diary_title.setFocusableInTouchMode(false);
            EDT_diary_title.setClickable(false);
            EDT_diary_title.setEnabled(false);
        }
    }


    private void initMoodSpinner() {
        ImageArrayAdapter moodArrayAdapter = new ImageArrayAdapter(getActivity(), DiaryInfoHelper.getMoodArray());
        SP_diary_mood.setAdapter(moodArrayAdapter);
    }

    private void initWeatherSpinner() {
        ImageArrayAdapter weatherArrayAdapter = new ImageArrayAdapter(getActivity(), DiaryInfoHelper.getWeatherArray());
        SP_diary_weather.setAdapter(weatherArrayAdapter);
    }

    private void loadDiaryItemContent(DBManager dbManager) {
        Cursor diaryContentCursor = dbManager.selectDiaryContentByDiaryId(diaryId);
        for (int i = 0; i < diaryContentCursor.getCount(); i++) {
            IDairyRow diaryItem = null;
            String content = "";
            if (diaryContentCursor.getInt(1) == IDairyRow.TYPE_PHOTO) {
                diaryItem = new DiaryPhoto(getActivity());
                content = diaryFileManager.getDiaryDir().getAbsolutePath() + "/" + diaryContentCursor.getString(3);
                if (isEditMode) {
                    diaryItem.setEditMode(true);
                    ((DiaryPhoto) diaryItem).setDeleteClickListener(diaryContentCursor.getInt(2), this);
                    //For get the right file name
                    ((DiaryPhoto) diaryItem).setPhotoFileName(diaryContentCursor.getString(3));
                } else {
                    diaryItem.setEditMode(false);
                }
            } else if (diaryContentCursor.getInt(1) == IDairyRow.TYPE_TEXT) {
                diaryItem = new DiaryText(getActivity());
                content = diaryContentCursor.getString(3);
                if (!isEditMode) {
                    diaryItem.setEditMode(false);
                }
            }
            diaryItem.setContent(content);
            diaryItem.setPosition(i);
            diaryItemHelper.createItem(diaryItem);
            diaryContentCursor.moveToNext();
        }
        diaryContentCursor.close();
    }


    private void setDiaryTime() {
        TV_diary_month.setText(timeTools.getMonthsFullName()[calendar.get(Calendar.MONTH)]);
        TV_diary_date.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        TV_diary_day.setText(timeTools.getDaysFullName()[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        TV_diary_time.setText(sdf.format(calendar.getTime()));
    }

    private void setIcon(int mood, int weather) {
        if (isEditMode) {
            SP_diary_mood.setSelection(mood);
            SP_diary_weather.setSelection(weather);
        } else {
            IV_diary_mood.setImageResource(DiaryInfoHelper.getMoodResourceId(mood));
            IV_diary_weather.setImageResource(DiaryInfoHelper.getWeatherResourceId(weather));
        }
    }

    private void loadFileFromTemp(String fileName) {
        try {
            String tempFileSrc = diaryFileManager.getDiaryDir().getAbsolutePath() + "/" + fileName;
            Bitmap resizeBmp = BitmapFactory.decodeFile(tempFileSrc);
            if (resizeBmp != null) {
                DiaryPhoto diaryPhoto = new DiaryPhoto(getActivity());
                diaryPhoto.setPhoto(resizeBmp, fileName);
                DiaryTextTag tag = checkoutOldDiaryContent();
                //Check edittext is focused
                if (tag != null) {
                    //Add new edittext
                    DiaryText diaryText = new DiaryText(getActivity());
                    diaryText.setPosition(tag.getPositionTag());
                    diaryText.setContent(tag.getNextEditTextStr());
                    diaryItemHelper.createItem(diaryText, tag.getPositionTag() + 1);
                    diaryText.getView().requestFocus();
                    //Add photo
                    diaryPhoto.setDeleteClickListener(tag.getPositionTag() + 1, this);
                    diaryItemHelper.createItem(diaryPhoto, tag.getPositionTag() + 1);
                } else {
                    //Add photo
                    diaryPhoto.setDeleteClickListener(diaryItemHelper.getItemSize(), this);
                    diaryItemHelper.createItem(diaryPhoto);
                    //Add new edittext
                    DiaryText diaryText = new DiaryText(getActivity());
                    diaryText.setPosition(diaryItemHelper.getItemSize());
                    diaryItemHelper.createItem(diaryText);
                    diaryText.getView().requestFocus();
                }
            } else {
                throw new FileNotFoundException(tempFileSrc + "not found or bitmap is null");
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(getActivity(), getString(R.string.toast_photo_path_error), Toast.LENGTH_LONG).show();
        } finally {
            diaryItemHelper.resortPosition();
        }
    }

    private DiaryTextTag checkoutOldDiaryContent() {
        View focusView = getDialog().getCurrentFocus();
        DiaryTextTag tag = null;
        if (focusView instanceof EditText && focusView.getTag() != null &&
                focusView.getTag() instanceof DiaryTextTag) {
            EditText currentEditText = (EditText) focusView;
            tag = (DiaryTextTag) focusView.getTag();
            if (currentEditText.getText().toString().length() > 0) {
                int index = currentEditText.getSelectionStart();
                String nextEditTextStr = currentEditText.getText().toString()
                        .substring(index, currentEditText.getText().toString().length());
                currentEditText.getText().delete(index, currentEditText.getText().toString().length());
                tag.setNextEditTextStr(nextEditTextStr);
            }
        }
        return tag;
    }


    public void setCallBack(DiaryViewerCallback callback) {
        this.callback = callback;
    }


    private void updateDiary() {
        new UpdateDiaryTask(getActivity(), calendar.getTimeInMillis(), EDT_diary_title.getText().toString(),
                SP_diary_mood.getSelectedItemPosition(), SP_diary_weather.getSelectedItemPosition(),
                //Check  attachment
                diaryItemHelper.getNowPhotoCount() > 0 ? true : false,
                diaryItemHelper, diaryFileManager, this).execute(((DiaryActivity) getActivity()).getTopicId(), diaryId);

    }

    private void openPhotoBottomSheet() {
        DiaryPhotoBottomSheet diaryPhotoBottomSheet = DiaryPhotoBottomSheet.newInstance(true);
        diaryPhotoBottomSheet.setTargetFragment(this,0);
        diaryPhotoBottomSheet.show(getFragmentManager(), "diaryPhotoBottomSheet");
    }

    @Override
    public void onDiaryUpdated() {
        this.dismissAllowingStateLoss();
        callback.updateDiary();
    }

    @Override
    public void selectPhoto(Uri uri) {
        if (FileManager.isImage(
                FileManager.getFileNameByUri(getActivity(), uri))) {
            //1.Copy bitmap to temp for rotating & resize
            //2.Then Load bitmap call back ;
            new CopyPhotoTask(getActivity(), uri,
                    DiaryItemHelper.getVisibleWidth(), DiaryItemHelper.getVisibleHeight(),
                    diaryFileManager, this).execute();
        } else {
            Toast.makeText(getActivity(), getString(R.string.toast_not_image), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void addPhoto(String fileName) {
        //1.get saved file for rotating & resize from temp
        //2.Then , Load bitmap in call back ;
        new CopyPhotoTask(getActivity(), fileName,
                DiaryItemHelper.getVisibleWidth(), DiaryItemHelper.getVisibleHeight(),
                diaryFileManager, this).execute();
    }

    @Override
    public void onCopyCompiled(String fileName) {
        loadFileFromTemp(fileName);
    }


    @Override
    public void onDiaryDelete() {
        callback.deleteDiary();
        dismiss();
    }

    @Override
    public void onBack() {
        dismiss();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //Since JellyBean, the onDateSet() method of the DatePicker class is called twice
        if (view.isShown()) {
            calendar.set(year, monthOfYear, dayOfMonth);
            setDiaryTime();
            TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(calendar.getTimeInMillis());
            timePickerFragment.setOnTimeSetListener(this);
            timePickerFragment.show(getFragmentManager(), "timePickerFragment");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Since JellyBean, the onTimeSet() method of the TimePicker class is called twice
        if (view.isShown()) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            setDiaryTime();
        }
    }


    @Override
    public void onCopyToEditCacheCompiled(int result) {
        if (result == CopyDiaryToEditCacheTask.RESULT_COPY_SUCCESSFUL) {
            PB_diary_item_content_hint.setVisibility(View.GONE);
            initData();
            //Open the click listener
            IV_diary_clear.setOnClickListener(this);
            IV_diary_save.setOnClickListener(this);
            IV_diary_photo.setOnClickListener(this);
        } else {
            dismissAllowingStateLoss();
        }
        initHandlerOrTaskIsRunning = false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.LL_diary_time_information:
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(calendar.getTimeInMillis());
                datePickerFragment.setOnDateSetListener(this);
                datePickerFragment.show(getFragmentManager(), "datePickerFragment");
                break;
            case R.id.IV_diary_photo_delete:
                int position = (int) v.getTag();
                diaryItemHelper.remove(position);
                LL_diary_item_content.removeViewAt(position);
                diaryItemHelper.mergerAdjacentText(position);
                diaryItemHelper.resortPosition();
                break;
            case R.id.IV_diary_photo:
                if (FileManager.getSDCardFreeSize() > FileManager.MIN_FREE_SPACE) {
                    if (PermissionHelper.checkPermission(this, REQUEST_CAMERA_AND_WRITE_ES_PERMISSION)) {
                        if (diaryItemHelper.getNowPhotoCount() < DiaryItemHelper.MAX_PHOTO_COUNT) {
                            openPhotoBottomSheet();
                        } else {
                            Toast.makeText(getActivity(),
                                    String.format(getResources().getString(R.string.toast_max_photo), DiaryItemHelper.MAX_PHOTO_COUNT),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    //Insufficient
                    Toast.makeText(getActivity(), getString(R.string.toast_space_insufficient), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.IV_diary_close_dialog:
                dismiss();
                break;
            case R.id.IV_diary_delete:
                DiaryDeleteDialogFragment diaryDeleteDialogFragment =
                        DiaryDeleteDialogFragment.newInstance(((DiaryActivity) getActivity()).getTopicId(), diaryId);
                diaryDeleteDialogFragment.setCallBack(this);
                diaryDeleteDialogFragment.show(getFragmentManager(), "diaryDeleteDialogFragment");
                break;
            case R.id.IV_diary_clear:
                dismiss();
                break;
            case R.id.IV_diary_save:
                if (diaryItemHelper.getItemSize() > 0) {
                    updateDiary();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_diary_empty), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
