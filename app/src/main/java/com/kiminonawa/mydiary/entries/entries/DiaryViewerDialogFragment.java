package com.kiminonawa.mydiary.entries.entries;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.entries.EditDiaryBackDialogFragment;
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
import com.kiminonawa.mydiary.entries.photo.PhotoDetailViewerActivity;
import com.kiminonawa.mydiary.entries.photo.PhotoOverviewActivity;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.PermissionHelper;
import com.kiminonawa.mydiary.shared.ScreenHelper;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.TimeTools;
import com.kiminonawa.mydiary.shared.ViewTools;
import com.kiminonawa.mydiary.shared.statusbar.ChinaPhoneHelper;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.kiminonawa.mydiary.shared.PermissionHelper.REQUEST_CAMERA_AND_WRITE_ES_PERMISSION;

/**
 * Created by daxia on 2016/10/27.
 */

public class DiaryViewerDialogFragment extends DialogFragment implements View.OnClickListener,
        DiaryDeleteDialogFragment.DeleteCallback, CopyDiaryToEditCacheTask.EditTaskCallBack,
        DiaryPhotoBottomSheet.PhotoCallBack, CopyPhotoTask.CopyPhotoCallBack,
        UpdateDiaryTask.UpdateDiaryCallBack, EditDiaryBackDialogFragment.BackDialogCallback,
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
    private ScrollView ScrollView_diary_content;
    private RelativeLayout RL_diary_info;
    private ProgressBar PB_diary_item_content_hint;
    private LinearLayout LL_diary_time_information;

    private TextView TV_diary_month, TV_diary_date, TV_diary_day, TV_diary_time;

    private ImageView IV_diary_weather;
    private ImageView IV_diary_location_name_icon;
    private TextView TV_diary_weather, TV_diary_location;
    private RelativeLayout RL_diary_weather, RL_diary_mood;
    private Spinner SP_diary_weather, SP_diary_mood;

    private TextView TV_diary_title_content;
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
    private Handler loadDiaryHandler;
    private boolean initHandlerOrTaskIsRunning = false;

    private Calendar calendar;
    private TimeTools timeTools;
    private SimpleDateFormat sdf;

    /**
     * Diary Photo viewer
     */
    private ArrayList<Uri> diaryPhotoFileList;

    /**
     * Google Place API
     */
    private int PLACE_PICKER_REQUEST = 1;
    /**
     * Location
     */
    private DiaryViewerHandler diaryViewerHandler;
    private Location diaryLocations = null;
    private LocationManager locationManager;
    private boolean haveLocation;
    private String noLocation;
    private ProgressDialog progressDialog;


    /**
     * Permission
     */
    private boolean firstAllowLocationPermission = false;
    private boolean firstAllowCameraPermission = false;


    public static DiaryViewerDialogFragment newInstance(long diaryId, boolean isEditMode) {
        Bundle args = new Bundle();
        DiaryViewerDialogFragment fragment = new DiaryViewerDialogFragment();
        args.putLong("diaryId", diaryId);
        args.putBoolean("isEditMode", isEditMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isEditMode = getArguments().getBoolean("isEditMode", false);
        if (isEditMode) {
            Toast.makeText(getActivity(),
                    getString(R.string.toast_diary_long_click_edit), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (isEditMode) {
                    EditDiaryBackDialogFragment backDialogFragment = new EditDiaryBackDialogFragment();
                    backDialogFragment.setTargetFragment(DiaryViewerDialogFragment.this, 0);
                    backDialogFragment.show(getFragmentManager(), "backDialogFragment");
                } else {
                    super.onBackPressed();
                }
            }
        };
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //Set background is transparent , for dialog radius
        dialog.getWindow().getDecorView().getBackground().setAlpha(0);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        View rootView = inflater.inflate(R.layout.fragment_diary, container);
        ScrollView_diary_content = (ScrollView) rootView.findViewById(R.id.ScrollView_diary_content);
        ViewTools.setScrollBarColor(getActivity(), ScrollView_diary_content);

        RL_diary_info = (RelativeLayout) rootView.findViewById(R.id.RL_diary_info);
        RL_diary_info.setBackground(ThemeManager.getInstance().createDiaryViewerInfoBg(getActivity()));

        LinearLayout LL_diary_edit_bar = (LinearLayout) rootView.findViewById(R.id.LL_diary_edit_bar);
        LL_diary_edit_bar.setBackground(ThemeManager.getInstance().createDiaryViewerEditBarBg(getActivity()));

        PB_diary_item_content_hint = (ProgressBar) rootView.findViewById(R.id.PB_diary_item_content_hint);

        EDT_diary_title = (EditText) rootView.findViewById(R.id.EDT_diary_title);

        TV_diary_month = (TextView) rootView.findViewById(R.id.TV_diary_month);
        TV_diary_date = (TextView) rootView.findViewById(R.id.TV_diary_date);
        TV_diary_day = (TextView) rootView.findViewById(R.id.TV_diary_day);
        TV_diary_time = (TextView) rootView.findViewById(R.id.TV_diary_time);

        IV_diary_location_name_icon = (ImageView) rootView.findViewById(R.id.IV_diary_location_name_icon);
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
        noLocation = getString(R.string.diary_no_location);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callback = (DiaryViewerCallback) getTargetFragment();
        diaryId = getArguments().getLong("diaryId", -1L);
        //Init the object
        if (diaryId != -1) {
            if (isEditMode) {
                diaryViewerHandler = new DiaryViewerHandler(this);
                diaryFileManager = new FileManager(getActivity(), FileManager.DIARY_EDIT_CACHE_DIR);
                diaryFileManager.clearDir();
                PB_diary_item_content_hint.setVisibility(View.VISIBLE);
                mTask = new CopyDiaryToEditCacheTask(getActivity(), diaryFileManager, this);
                //Make ths ProgressBar show 0.7s+.
                loadDiaryHandler = new Handler();
                initHandlerOrTaskIsRunning = true;
                loadDiaryHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Copy the file into editCash
                        mTask.execute(((DiaryActivity) getActivity()).getTopicId(), diaryId);
                    }
                }, 700);
            } else {
                diaryFileManager = new FileManager(getActivity(), ((DiaryActivity) getActivity()).getTopicId(), diaryId);
                diaryPhotoFileList = new ArrayList<>();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(), data);
                if (place.getName() != null || !place.getName().equals("")) {
                    //try to spilt the string if it is a local
                    TV_diary_location.setText(place.getName());
                    haveLocation = true;
                } else {
                    haveLocation = false;
                }
                initLocationIcon();
            }
            progressDialog.dismiss();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //For PermissionsResult
        if (firstAllowLocationPermission) {
            openGooglePlacePicker();
            firstAllowLocationPermission = false;
        }
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
            int dialogHeight;
            if (ChinaPhoneHelper.getDeviceStatusBarType() == ChinaPhoneHelper.OTHER) {
                dialogHeight = ScreenHelper.getScreenHeight(getActivity())
                        - ScreenHelper.getStatusBarHeight(getActivity())
                        - ScreenHelper.dpToPixel(getActivity().getResources(), 2 * 10);
            } else {
                dialogHeight = ScreenHelper.getScreenHeight(getActivity())
                        - ScreenHelper.dpToPixel(getActivity().getResources(), 2 * 10);
            }
            int dialogWidth = ScreenHelper.getScreenWidth(getActivity())
                    - ScreenHelper.dpToPixel(getActivity().getResources(), 2 * 5);
            dialog.getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (initHandlerOrTaskIsRunning) {
            if (loadDiaryHandler != null) {
                loadDiaryHandler.removeCallbacksAndMessages(null);
            }
            if (mTask != null) {
                mTask.cancel(true);
            }
            dismissAllowingStateLoss();
        }
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        if (diaryViewerHandler != null) {
            diaryViewerHandler.removeCallbacksAndMessages(null);
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
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
        //load Time
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(diaryInfoCursor.getLong(1));
        timeTools = TimeTools.getInstance(getActivity().getApplicationContext());
        sdf = new SimpleDateFormat("HH:mm");
        setDiaryTime();
        if (isEditMode) {
            //Allow to edit diary
            LL_diary_time_information.setOnClickListener(this);
            EDT_diary_title.setText(diaryInfoCursor.getString(2));
        } else {
            String diaryTitleStr = diaryInfoCursor.getString(2);
            if (diaryTitleStr == null || diaryTitleStr.equals("")) {
                diaryTitleStr = getString(R.string.diary_no_title);
            }
            TV_diary_title_content.setText(diaryTitleStr);
        }
        //load location
        String locationName = diaryInfoCursor.getString(7);
        if (locationName != null && !"".equals(locationName)) {
            haveLocation = true;
            IV_diary_location_name_icon.setVisibility(View.VISIBLE);
            TV_diary_location.setText(locationName);
        } else {
            haveLocation = false;
            IV_diary_location_name_icon.setVisibility(View.VISIBLE);
        }
        initLocationIcon();
        setIcon(diaryInfoCursor.getInt(3), diaryInfoCursor.getInt(4));
        diaryInfoCursor.close();
        //Get diary detail
        loadDiaryItemContent(dbManager);
        dbManager.closeDB();
    }

    private void initView(View rootView) {
        if (isEditMode) {
            initProgressDialog();
            initLocationManager();

            LL_diary_time_information = (LinearLayout) rootView.findViewById(R.id.LL_diary_time_information);
            SP_diary_mood = (Spinner) rootView.findViewById(R.id.SP_diary_mood);
            SP_diary_mood.setVisibility(View.VISIBLE);
            SP_diary_weather = (Spinner) rootView.findViewById(R.id.SP_diary_weather);
            SP_diary_weather.setVisibility(View.VISIBLE);

            //For hidden hint
            EDT_diary_title.setText(" ");
            EDT_diary_title.getBackground().mutate().setColorFilter(ThemeManager.getInstance().getThemeMainColor(getActivity()),
                    PorterDuff.Mode.SRC_ATOP);

            initMoodSpinner();
            initWeatherSpinner();
            IV_diary_location.setOnClickListener(this);

            IV_diary_delete.setOnClickListener(this);
            IV_diary_clear.setVisibility(View.GONE);

            IV_diary_photo.setImageResource(R.drawable.ic_photo_camera_white_24dp);
            IV_diary_photo.setOnClickListener(this);
        } else {
            EDT_diary_title.setVisibility(View.GONE);
            RL_diary_weather = (RelativeLayout) rootView.findViewById(R.id.RL_diary_weather);
            RL_diary_weather.setVisibility(View.GONE);
            RL_diary_mood = (RelativeLayout) rootView.findViewById(R.id.RL_diary_mood);
            RL_diary_mood.setVisibility(View.GONE);

            IV_diary_weather = (ImageView) rootView.findViewById(R.id.IV_diary_weather);
            TV_diary_weather = (TextView) rootView.findViewById(R.id.TV_diary_weather);
            IV_diary_weather.setVisibility(View.VISIBLE);
            TV_diary_weather.setVisibility(View.VISIBLE);

            IV_diary_location_name_icon.setVisibility(View.VISIBLE);

            TV_diary_title_content = (TextView) rootView.findViewById(R.id.TV_diary_title_content);
            TV_diary_title_content.setVisibility(View.VISIBLE);
            TV_diary_title_content.setTextColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

            IV_diary_delete.setOnClickListener(this);
            IV_diary_clear.setVisibility(View.GONE);
            IV_diary_save.setVisibility(View.GONE);

            IV_diary_photo.setImageResource(R.drawable.ic_photo_white_24dp);
            IV_diary_photo.setOnClickListener(this);
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
        //To count how many photo is in the diary on view mode.
        int photoCount = 0;
        for (int i = 0; i < diaryContentCursor.getCount(); i++) {
            IDairyRow diaryItem = null;
            String content = "";
            if (diaryContentCursor.getInt(1) == IDairyRow.TYPE_PHOTO) {
                diaryItem = new DiaryPhoto(getActivity());
                content = FileManager.FILE_HEADER +
                        diaryFileManager.getDirAbsolutePath() + "/" + diaryContentCursor.getString(3);
                if (isEditMode) {
                    diaryItem.setEditMode(true);
                    ((DiaryPhoto) diaryItem).setDeleteClickListener(this);
                    //For get the right file name
                    ((DiaryPhoto) diaryItem).setPhotoFileName(diaryContentCursor.getString(3));
                } else {
                    diaryItem.setEditMode(false);
                    ((DiaryPhoto) diaryItem).setDraweeViewClickListener(this);
                    ((DiaryPhoto) diaryItem).setDraweeViewPositionTag(photoCount);
                    photoCount++;
                    diaryPhotoFileList.add(Uri.parse(content));
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

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.process_dialog_loading));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
    }

    private void initLocationManager() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
    }

    private void initLocationIcon() {
        if (haveLocation) {
            IV_diary_location.setImageResource(R.drawable.ic_location_on_white_24dp);
        } else {
            IV_diary_location.setImageResource(R.drawable.ic_location_off_white_24dp);
            TV_diary_location.setText(noLocation);
        }
    }

    private void startGetLocation() {
        //Open Google App or use geoCoder
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()) == ConnectionResult.SUCCESS) {
            openGooglePlacePicker();
        } else {
            openGPSListener();
        }
    }

    private void openGooglePlacePicker() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()) == ConnectionResult.SUCCESS) {
            try {
                progressDialog.show();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.toast_google_service_not_work), Toast.LENGTH_LONG).show();
        }
    }

    private void openGPSListener() {
        progressDialog.show();
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0,
                    locationListener);
            //Waiting gps max timeout is 15s
            diaryViewerHandler.sendEmptyMessageDelayed(0, 15000);
        } catch (SecurityException e) {
            //do nothing
        }

    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            diaryLocations = new Location(location);
            diaryViewerHandler.removeCallbacksAndMessages(null);
            diaryViewerHandler.sendEmptyMessage(0);
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


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
            IV_diary_weather.setImageResource(DiaryInfoHelper.getWeatherResourceId(weather));
            TV_diary_weather.setText(getResources().getStringArray(R.array.weather_list)[weather]);
        }
    }

    private void loadFileFromTemp(String fileName) {
        try {
            String tempFileSrc = FileManager.FILE_HEADER + diaryFileManager.getDirAbsolutePath() + "/" + fileName;
            DiaryPhoto diaryPhoto = new DiaryPhoto(getActivity());
            diaryPhoto.setPhoto(Uri.parse(tempFileSrc), fileName);
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
                diaryPhoto.setPosition(tag.getPositionTag() + 1);
                diaryPhoto.setDeleteClickListener(this);
                diaryItemHelper.createItem(diaryPhoto, tag.getPositionTag() + 1);
            } else {
                //Add photo
                diaryPhoto.setPosition(diaryItemHelper.getItemSize());
                diaryPhoto.setDeleteClickListener(this);
                diaryItemHelper.createItem(diaryPhoto);
                //Add new edittext
                DiaryText diaryText = new DiaryText(getActivity());
                diaryText.setPosition(diaryItemHelper.getItemSize());
                diaryItemHelper.createItem(diaryText);
                diaryText.getView().requestFocus();
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

    private void updateDiary() {

        //Create locationName
        String locationName = TV_diary_location.getText().toString();
        if (noLocation.equals(locationName)) {
            locationName = "";
        }
        new UpdateDiaryTask(getActivity(), calendar.getTimeInMillis(), EDT_diary_title.getText().toString(),
                SP_diary_mood.getSelectedItemPosition(), SP_diary_weather.getSelectedItemPosition(),
                locationName,
                //Check  attachment
                diaryItemHelper.getNowPhotoCount() > 0 ? true : false,
                diaryItemHelper, diaryFileManager, this).execute(((DiaryActivity) getActivity()).getTopicId(), diaryId);

    }

    private void openPhotoBottomSheet() {
        DiaryPhotoBottomSheet diaryPhotoBottomSheet = DiaryPhotoBottomSheet.newInstance(true);
        diaryPhotoBottomSheet.setTargetFragment(this, 0);
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
                    DiaryItemHelper.getVisibleWidth(getActivity()), DiaryItemHelper.getVisibleHeight(getActivity()),
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
                DiaryItemHelper.getVisibleWidth(getActivity()), DiaryItemHelper.getVisibleHeight(getActivity()),
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
            case R.id.IV_diary_location:
                if (haveLocation) {
                    haveLocation = false;
                    initLocationIcon();
                } else {
                    if (PermissionHelper.checkPermission(this, PermissionHelper.REQUEST_ACCESS_FINE_LOCATION_PERMISSION)) {
                        //Check gps is open
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            startGetLocation();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.toast_location_not_open), Toast.LENGTH_LONG).show();
                        }

                    }
                }
                break;
            case R.id.IV_diary_photo_delete:
                int deletePosition = (int) v.getTag();
                Log.e("test", "deletePosition = " + deletePosition);
                diaryItemHelper.remove(deletePosition);
                LL_diary_item_content.removeViewAt(deletePosition);
                diaryItemHelper.mergerAdjacentText(deletePosition);
                diaryItemHelper.resortPosition();
                break;
            case R.id.SDV_diary_new_photo:
                int draweeViewPosition = (int) v.getTag();
                Intent gotoPhotoDetailViewer = new Intent(getActivity(), PhotoDetailViewerActivity.class);
                gotoPhotoDetailViewer.putParcelableArrayListExtra(
                        PhotoDetailViewerActivity.DIARY_PHOTO_FILE_LIST, diaryPhotoFileList);
                gotoPhotoDetailViewer.putExtra(PhotoDetailViewerActivity.SELECT_POSITION, draweeViewPosition);
                getActivity().startActivity(gotoPhotoDetailViewer);
                break;
            case R.id.IV_diary_photo:
                if (isEditMode) {
                    //Allow add photo
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
                } else {
                    //Show the gallery
                    Intent gotoPhotoOverviewIntent = new Intent(getActivity(), PhotoOverviewActivity.class);
                    gotoPhotoOverviewIntent.putExtra(PhotoOverviewActivity.PHOTO_OVERVIEW_TOPIC_ID, ((DiaryActivity) getActivity()).getTopicId());
                    gotoPhotoOverviewIntent.putExtra(PhotoOverviewActivity.PHOTO_OVERVIEW_DIARY_ID, diaryId);
                    getActivity().startActivity(gotoPhotoOverviewIntent);
                }
                break;
            case R.id.IV_diary_close_dialog:
                if (isEditMode) {
                    EditDiaryBackDialogFragment backDialogFragment = new EditDiaryBackDialogFragment();
                    backDialogFragment.setTargetFragment(DiaryViewerDialogFragment.this, 0);
                    backDialogFragment.show(getFragmentManager(), "backDialogFragment");
                } else {
                    dismiss();
                }
                break;
            case R.id.IV_diary_delete:
                DiaryDeleteDialogFragment diaryDeleteDialogFragment =
                        DiaryDeleteDialogFragment.newInstance(((DiaryActivity) getActivity()).getTopicId(), diaryId);
                diaryDeleteDialogFragment.setTargetFragment(this, 0);
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

    private static class DiaryViewerHandler extends Handler {

        private WeakReference<DiaryViewerDialogFragment> mFrag;

        DiaryViewerHandler(DiaryViewerDialogFragment aFragment) {
            mFrag = new WeakReference<>(aFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DiaryViewerDialogFragment theFrag = mFrag.get();
            if (theFrag != null) {
                theFrag.TV_diary_location.setText(getLocationName(theFrag));
                theFrag.initLocationIcon();
            }
        }

        private String getLocationName(DiaryViewerDialogFragment theFrag) {
            StringBuilder returnLocation = new StringBuilder();
            try {
                if (theFrag.diaryLocations != null) {
                    List<String> providerList = theFrag.locationManager.getAllProviders();
                    if (null != theFrag.diaryLocations && null != providerList && providerList.size() > 0) {
                        double longitude = theFrag.diaryLocations.getLongitude();
                        double latitude = theFrag.diaryLocations.getLatitude();
                        Geocoder geocoder = new Geocoder(theFrag.getActivity().getApplicationContext(), Locale.getDefault());
                        List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (null != listAddresses && listAddresses.size() > 0) {
                            try {
                                returnLocation.append(listAddresses.get(0).getCountryName());
                                returnLocation.append(" ");
                                returnLocation.append(listAddresses.get(0).getAdminArea());
                                returnLocation.append(" ");
                                returnLocation.append(listAddresses.get(0).getLocality());
                                theFrag.haveLocation = true;
                            } catch (Exception e) {
                                //revert it in finally
                            }
                        } else {
                            Toast.makeText(theFrag.getActivity(), theFrag.getString(R.string.toast_geocoder_fail), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(theFrag.getActivity(), theFrag.getString(R.string.toast_location_timeout), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(theFrag.getActivity(), theFrag.getString(R.string.toast_geocoder_fail), Toast.LENGTH_LONG).show();
            } finally {
                theFrag.diaryLocations = null;
                try {
                    theFrag.locationManager.removeUpdates(theFrag.locationListener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                theFrag.progressDialog.dismiss();
                if (returnLocation.length() == 0) {
                    returnLocation.append(theFrag.noLocation);
                    theFrag.haveLocation = false;
                }
            }
            return returnLocation.toString();
        }
    }
}
