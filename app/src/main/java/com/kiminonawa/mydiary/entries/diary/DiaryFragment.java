package com.kiminonawa.mydiary.entries.diary;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;
import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.backup.obj.BUDiaryEntries;
import com.kiminonawa.mydiary.backup.obj.BUDiaryItem;
import com.kiminonawa.mydiary.entries.BaseDiaryFragment;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.entries.diary.item.DiaryPhoto;
import com.kiminonawa.mydiary.entries.diary.item.DiaryText;
import com.kiminonawa.mydiary.entries.diary.item.DiaryTextTag;
import com.kiminonawa.mydiary.entries.diary.item.IDairyRow;
import com.kiminonawa.mydiary.entries.diary.picker.DatePickerFragment;
import com.kiminonawa.mydiary.entries.diary.picker.TimePickerFragment;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.PermissionHelper;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.TimeTools;
import com.kiminonawa.mydiary.shared.ViewTools;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.kiminonawa.mydiary.R.id.IV_diary_location_name_icon;
import static com.kiminonawa.mydiary.backup.obj.BUDiaryEntries.NO_BU_DIARY_ID;
import static com.kiminonawa.mydiary.backup.obj.BUDiaryEntries.NO_BU_DIARY_TIME;
import static com.kiminonawa.mydiary.shared.PermissionHelper.REQUEST_ACCESS_FINE_LOCATION_PERMISSION;
import static com.kiminonawa.mydiary.shared.PermissionHelper.REQUEST_CAMERA_AND_WRITE_ES_PERMISSION;


/**
 * This page doesn't be used in the movie.
 * I define this page for write diary.
 */

public class DiaryFragment extends BaseDiaryFragment implements View.OnClickListener,
        DiaryPhotoBottomSheet.PhotoCallBack, Observer, SaveDiaryTask.SaveDiaryCallBack,
        CopyPhotoTask.CopyPhotoCallBack, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        ClearDialogFragment.ClearDialogCallback {


    private String TAG = "DiaryFragment";

    /**
     * UI
     */
    private ScrollView ScrollView_diary_content;
    private LinearLayout LL_diary_item_content, LL_diary_time_information;
    private RelativeLayout RL_diary_info;
    private TextView TV_diary_month, TV_diary_date, TV_diary_day, TV_diary_time, TV_diary_location;

    private Spinner SP_diary_weather, SP_diary_mood;
    private EditText EDT_diary_title;
    private ImageView IV_diary_menu, IV_diary_location, IV_diary_photo, IV_diary_delete, IV_diary_clear, IV_diary_save;
    private TextView TV_diary_item_content_hint;


    /**
     * Permission
     */
    private boolean firstAllowLocationPermission = false;
    private boolean firstAllowCameraPermission = false;

    /**
     * Time
     */
    private Calendar calendar;
    private TimeTools timeTools;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    /**
     * diary item
     */
    private DiaryItemHelper diaryItemHelper;
    /**
     * File
     */
    private FileManager diaryTempFileManager;

    /**
     * Google Place API
     */
    private int PLACE_PICKER_REQUEST = 1;
    /**
     * Location
     */
    private DiaryHandler diaryHandler;
    private Location diaryLocations = null;
    private LocationManager locationManager;
    private String noLocation;
    private boolean isLocation = false;
    private ProgressDialog progressDialog;
    private final static int GPS_TIMEOUT = 20 * 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        timeTools = TimeTools.getInstance(getActivity().getApplicationContext());
        noLocation = getString(R.string.diary_no_location);
        diaryTempFileManager = new FileManager(getActivity(), getTopicId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary, container, false);

        ScrollView_diary_content = (ScrollView) rootView.findViewById(R.id.ScrollView_diary_content);
        ViewTools.setScrollBarColor(getActivity(), ScrollView_diary_content);

        RL_diary_info = (RelativeLayout) rootView.findViewById(R.id.RL_diary_info);
        RL_diary_info.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        LinearLayout LL_diary_edit_bar = (LinearLayout) rootView.findViewById(R.id.LL_diary_edit_bar);
        LL_diary_edit_bar.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        LL_diary_time_information = (LinearLayout) rootView.findViewById(R.id.LL_diary_time_information);
        LL_diary_time_information.setOnClickListener(this);
        TV_diary_month = (TextView) rootView.findViewById(R.id.TV_diary_month);
        TV_diary_date = (TextView) rootView.findViewById(R.id.TV_diary_date);
        TV_diary_day = (TextView) rootView.findViewById(R.id.TV_diary_day);
        TV_diary_time = (TextView) rootView.findViewById(R.id.TV_diary_time);
        TV_diary_location = (TextView) rootView.findViewById(R.id.TV_diary_location);
        rootView.findViewById(IV_diary_location_name_icon).setVisibility(View.VISIBLE);

        SP_diary_weather = (Spinner) rootView.findViewById(R.id.SP_diary_weather);
        SP_diary_weather.setVisibility(View.VISIBLE);
        SP_diary_mood = (Spinner) rootView.findViewById(R.id.SP_diary_mood);
        SP_diary_mood.setVisibility(View.VISIBLE);

        EDT_diary_title = (EditText) rootView.findViewById(R.id.EDT_diary_title);
        EDT_diary_title.getBackground().mutate().setColorFilter(ThemeManager.getInstance().getThemeMainColor(getActivity()),
                PorterDuff.Mode.SRC_ATOP);

        TV_diary_item_content_hint = (TextView) rootView.findViewById(R.id.TV_diary_item_content_hint);
        //For create diary
        LL_diary_item_content = (LinearLayout) rootView.findViewById(R.id.LL_diary_item_content);
        LL_diary_item_content.setOnClickListener(this);

        IV_diary_menu = (ImageView) rootView.findViewById(R.id.IV_diary_menu);
        IV_diary_location = (ImageView) rootView.findViewById(R.id.IV_diary_location);
        IV_diary_location.setOnClickListener(this);
        IV_diary_photo = (ImageView) rootView.findViewById(R.id.IV_diary_photo);
        IV_diary_photo.setOnClickListener(this);
        IV_diary_delete = (ImageView) rootView.findViewById(R.id.IV_diary_delete);
        IV_diary_delete.setVisibility(View.GONE);
        IV_diary_clear = (ImageView) rootView.findViewById(R.id.IV_diary_clear);
        IV_diary_clear.setOnClickListener(this);
        IV_diary_save = (ImageView) rootView.findViewById(R.id.IV_diary_save);
        IV_diary_save.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        diaryHandler = new DiaryHandler(this);
        initWeatherSpinner();
        initMoodSpinner();
        setCurrentTime(true);
        initLocationManager();
        initProgressDialog();
        diaryItemHelper = new DiaryItemHelper(LL_diary_item_content);
        clearDiaryPage();
        //Revert the auto saved diary
        revertAutoSaveDiary();
    }


    @Override
    public void onStart() {
        super.onStart();
        diaryItemHelper.addObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //For PermissionsResult
        if (firstAllowLocationPermission) {
            startGetLocation();
            firstAllowLocationPermission = false;
        }
        //For PermissionsResult
        if (firstAllowCameraPermission) {
            openPhotoBottomSheet();
            firstAllowCameraPermission = false;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        //Auto Save the diary
        autoSaveDiary();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Release the resource
        diaryItemHelper.deleteObserver(this);

        if (locationManager != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        diaryHandler.removeCallbacksAndMessages(null);
        progressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(), data);
                if (place.getName() != null || !place.getName().equals("")) {
                    //try to spilt the string if it is a local
                    TV_diary_location.setText(place.getName());
                    isLocation = true;
                } else {
                    isLocation = false;
                }
                initLocationIcon();
            }
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    firstAllowLocationPermission = true;
                } else {
                    PermissionHelper.showAccessDialog(getActivity());
                }
                break;
            case PermissionHelper.REQUEST_CAMERA_AND_WRITE_ES_PERMISSION:
                if (grantResults.length > 0
                        && PermissionHelper.checkAllPermissionResult(grantResults)) {
                    firstAllowCameraPermission = true;
                } else {
                    PermissionHelper.showAddPhotoDialog(getActivity());
                }
                break;
        }
    }


    private void loadFileFromTemp(String fileName) {
        try {
            String tempFileSrc = FileManager.FILE_HEADER + diaryTempFileManager.getDirAbsolutePath() + "/" + fileName;
//            Bitmap resizeBmp = BitmapFactory.decodeFile(tempFileSrc);
//            if (resizeBmp != null) {
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
//            } else {
//                throw new FileNotFoundException(tempFileSrc + "not found or bitmap is null");
//            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(getActivity(), getString(R.string.toast_photo_path_error), Toast.LENGTH_LONG).show();
        } finally {
            diaryItemHelper.resortPosition();
        }
    }


    private void setCurrentTime(boolean updateCurrentTime) {
        if (updateCurrentTime) {
            calendar.setTimeInMillis(System.currentTimeMillis());
        }
        TV_diary_month.setText(timeTools.getMonthsFullName()[calendar.get(Calendar.MONTH)]);
        TV_diary_date.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        TV_diary_day.setText(timeTools.getDaysFullName()[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        TV_diary_time.setText(sdf.format(calendar.getTime()));
    }

    private void initLocationManager() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

    }

    private void initLocationIcon() {
        if (isLocation) {
            IV_diary_location.setImageResource(R.drawable.ic_location_on_white_24dp);
        } else {
            IV_diary_location.setImageResource(R.drawable.ic_location_off_white_24dp);
            TV_diary_location.setText(noLocation);
        }
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.process_dialog_loading));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
    }

    private void initWeatherSpinner() {
        ImageArrayAdapter weatherArrayAdapter = new ImageArrayAdapter(getActivity(), DiaryInfoHelper.getWeatherArray());
        SP_diary_weather.setAdapter(weatherArrayAdapter);
    }

    private void initMoodSpinner() {
        ImageArrayAdapter moodArrayAdapter = new ImageArrayAdapter(getActivity(), DiaryInfoHelper.getMoodArray());
        SP_diary_mood.setAdapter(moodArrayAdapter);
    }

    /**
     * Clear and set the UUI
     */
    private void clearDiaryPage() {
        isLocation = false;
        initLocationIcon();
        SP_diary_mood.setSelection(0);
        SP_diary_weather.setSelection(0);
        EDT_diary_title.setText("");
        diaryItemHelper.initDiary();
    }

    /**
     * The temp file only be clear when click clear button & diary save
     */
    private void clearDiaryTemp() {
        diaryTempFileManager.clearDir();
        SPFManager.clearDiaryAutoSave(getActivity(), getTopicId());
    }

    private void autoSaveDiary() {
        if (diaryItemHelper.getItemSize() > 0) {
            List<BUDiaryItem> diaryItemItemList = new ArrayList<>();
            for (int x = 0; x < diaryItemHelper.getItemSize(); x++) {
                diaryItemItemList.add(
                        new BUDiaryItem(diaryItemHelper.get(x).getType(),
                                diaryItemHelper.get(x).getPosition(),
                                diaryItemHelper.get(x).getContent()));
            }
            String locationName = TV_diary_location.getText().toString();
            if (noLocation.equals(locationName)) {
                locationName = "";
            }
            BUDiaryEntries autoSaveDiary = new BUDiaryEntries(
                    NO_BU_DIARY_ID, NO_BU_DIARY_TIME,
                    EDT_diary_title.getText().toString(),
                    SP_diary_mood.getSelectedItemPosition(),
                    SP_diary_weather.getSelectedItemPosition(),
                    diaryItemHelper.getNowPhotoCount() > 0 ? true : false,
                    locationName, diaryItemItemList);
            SPFManager.setDiaryAutoSave(getActivity(), getTopicId(), new Gson().toJson(autoSaveDiary));
        }
    }

    /**
     * Revert diray from SPF
     */
    private void revertAutoSaveDiary() {

        if (SPFManager.getDiaryAutoSave(getActivity(), getTopicId()) != null) {
            try {
                BUDiaryEntries autoSaveDiary = new Gson().fromJson(
                        SPFManager.getDiaryAutoSave(getActivity(), getTopicId()), BUDiaryEntries.class);
                //Title
                EDT_diary_title.setText(autoSaveDiary.getDiaryEntriesTitle());

                //load location
                String locationName = autoSaveDiary.getDiaryEntriesLocation();
                if (locationName != null && !"".equals(locationName)) {
                    isLocation = true;
                    TV_diary_location.setText(locationName);
                } else {
                    isLocation = false;
                }
                initLocationIcon();
                setIcon(autoSaveDiary.getDiaryEntriesMood(), autoSaveDiary.getDiaryEntriesWeather());
                loadDiaryItemContent(autoSaveDiary);
            } catch (Exception e) {
                Log.e(TAG, "Load auto save fail", e);
            }
            TV_diary_item_content_hint.setVisibility(View.INVISIBLE);
        } else {
            TV_diary_item_content_hint.setVisibility(View.VISIBLE);
        }
    }

    private void setIcon(int mood, int weather) {
        SP_diary_mood.setSelection(mood);
        SP_diary_weather.setSelection(weather);
    }

    private void loadDiaryItemContent(BUDiaryEntries autoSaveDiary) {
        for (int i = 0; i < autoSaveDiary.getDiaryItemList().size(); i++) {
            IDairyRow diaryItem = null;
            String content = "";
            if (autoSaveDiary.getDiaryItemList().get(i).getDiaryItemType() == IDairyRow.TYPE_PHOTO) {
                diaryItem = new DiaryPhoto(getActivity());
                content = FileManager.FILE_HEADER +
                        diaryTempFileManager.getDirAbsolutePath() + "/" +
                        autoSaveDiary.getDiaryItemList().get(i).getDiaryItemContent();
                ((DiaryPhoto) diaryItem).setDeleteClickListener(this);
                //For get the right file name
                ((DiaryPhoto) diaryItem).setPhotoFileName(
                        autoSaveDiary.getDiaryItemList().get(i).getDiaryItemContent());
            } else if (autoSaveDiary.getDiaryItemList().get(i).getDiaryItemType() == IDairyRow.TYPE_TEXT) {
                diaryItem = new DiaryText(getActivity());
                content = autoSaveDiary.getDiaryItemList().get(i).getDiaryItemContent();
            }
            //In this page , it always is  edit mode.
            diaryItem.setEditMode(true);
            diaryItem.setContent(content);
            diaryItem.setPosition(i);
            diaryItemHelper.createItem(diaryItem);
        }
    }

    private void saveDiary() {
        //Create locationName
        String locationName = TV_diary_location.getText().toString();
        if (noLocation.equals(locationName)) {
            locationName = "";
        }
        new SaveDiaryTask(getActivity(), calendar.getTimeInMillis(),
                EDT_diary_title.getText().toString(),
                SP_diary_mood.getSelectedItemPosition(), SP_diary_weather.getSelectedItemPosition(),
                //Check  attachment
                diaryItemHelper.getNowPhotoCount() > 0 ? true : false,
                locationName, diaryItemHelper, getTopicId(), this).execute(getTopicId());
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
        try {
            progressDialog.show();
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), getString(R.string.toast_google_service_not_work), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), getString(R.string.toast_google_service_not_work), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    private void openGPSListener() {
        progressDialog.show();
        try {
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            //Waiting gps max timeout is 20s
            diaryHandler.sendEmptyMessageDelayed(0, GPS_TIMEOUT);
        } catch (SecurityException e) {
            //do nothing
        }

    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            diaryLocations = new Location(location);
            diaryHandler.removeCallbacksAndMessages(null);
            diaryHandler.sendEmptyMessage(0);
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

    private void openPhotoBottomSheet() {
        DiaryPhotoBottomSheet diaryPhotoBottomSheet = DiaryPhotoBottomSheet.newInstance(false);
        diaryPhotoBottomSheet.setTargetFragment(this, 0);
        diaryPhotoBottomSheet.show(getFragmentManager(), "diaryPhotoBottomSheet");
    }

    private DiaryTextTag checkoutOldDiaryContent() {
        View focusView = getActivity().getCurrentFocus();
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


    @Override
    public void selectPhoto(Uri uri) {
        if (FileManager.isImage(
                FileManager.getFileNameByUri(getActivity(), uri))) {
            //1.Copy bitmap to temp for rotating & resize
            //2.Then Load bitmap call back ;
            new CopyPhotoTask(getActivity(), uri,
                    DiaryItemHelper.getVisibleWidth(getActivity()), DiaryItemHelper.getVisibleHeight(getActivity()),
                    diaryTempFileManager, this).execute();
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
                diaryTempFileManager, this).execute();
    }

    @Override
    public void onCopyCompiled(String fileName) {
        loadFileFromTemp(fileName);
    }


    @Override
    public void update(Observable observable, Object data) {
        if (diaryItemHelper.getItemSize() > 0) {
            TV_diary_item_content_hint.setVisibility(View.GONE);
        } else {
            TV_diary_item_content_hint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDiarySaved() {
        //For next diary
        setCurrentTime(true);
        //Clear
        clearDiaryPage();
        clearDiaryTemp();
        //Set flag
        ((DiaryActivity) getActivity()).callEntriesListRefresh();
        //Goto entries page
        ((DiaryActivity) getActivity()).gotoPage(0);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //Since JellyBean, the onDateSet() method of the DatePicker class is called twice
        if (view.isShown()) {
            calendar.set(year, monthOfYear, dayOfMonth);
            setCurrentTime(false);
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
            setCurrentTime(false);
        }
    }

    @Override
    public void onClear() {
        clearDiaryPage();
        clearDiaryTemp();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LL_diary_time_information:
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(calendar.getTimeInMillis());
                datePickerFragment.setOnDateSetListener(this);
                datePickerFragment.show(getFragmentManager(), "datePickerFragment");
                break;
            case R.id.LL_diary_item_content:
                if (diaryItemHelper.getItemSize() == 0) {
                    diaryItemHelper.initDiary();
                    //Add default edittext item
                    DiaryText diaryText = new DiaryText(getActivity());
                    diaryText.setPosition(diaryItemHelper.getItemSize());
                    diaryItemHelper.createItem(diaryText);
                    //set Focus
                    diaryText.getView().requestFocus();
                    //Show keyboard automatically
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(diaryText.getView(), InputMethodManager.SHOW_IMPLICIT);
                } else if (diaryItemHelper.getItemSize() == 1) {
                    //Make the soft keyboard can be opened when it is only one item.
                    diaryItemHelper.get(0).getView().requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(diaryItemHelper.get(diaryItemHelper.getItemSize() - 1).getView(), InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case R.id.IV_diary_photo_delete:
                int position = (int) v.getTag();
                diaryItemHelper.remove(position);
                LL_diary_item_content.removeViewAt(position);
                diaryItemHelper.mergerAdjacentText(position);
                diaryItemHelper.resortPosition();
                break;
            case R.id.IV_diary_location:
                if (isLocation) {
                    isLocation = false;
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
            case R.id.IV_diary_clear:
                if (diaryItemHelper.getItemSize() > 0 || EDT_diary_title.length() > 0) {
                    ClearDialogFragment clearDialogFragment = new ClearDialogFragment();
                    clearDialogFragment.setTargetFragment(this, 0);
                    clearDialogFragment.show(getFragmentManager(), "clearDialogFragment");
                }
                break;
            case R.id.IV_diary_save:
                if (diaryItemHelper.getItemSize() > 0) {
                    saveDiary();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_diary_empty), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private static class DiaryHandler extends Handler {

        private WeakReference<DiaryFragment> mFrag;

        DiaryHandler(DiaryFragment aFragment) {
            mFrag = new WeakReference<>(aFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DiaryFragment theFrag = mFrag.get();
            if (theFrag != null) {
                theFrag.TV_diary_location.setText(getLocationName(theFrag));
                theFrag.initLocationIcon();
            }
        }

        private String getLocationName(DiaryFragment theFrag) {
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
                                theFrag.isLocation = true;
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
                    theFrag.isLocation = false;
                }
            }
            return returnLocation.toString();
        }
    }
}
