package com.kiminonawa.mydiary.entries.diary;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v7.app.AlertDialog;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.entries.BaseDiaryFragment;
import com.kiminonawa.mydiary.entries.DiaryActivity;
import com.kiminonawa.mydiary.entries.diary.item.DiaryItemHelper;
import com.kiminonawa.mydiary.entries.diary.item.DiaryPhoto;
import com.kiminonawa.mydiary.entries.diary.item.DiaryText;
import com.kiminonawa.mydiary.entries.diary.item.DiaryTextTag;
import com.kiminonawa.mydiary.entries.diary.picker.DatePickerFragment;
import com.kiminonawa.mydiary.entries.diary.picker.TimePickerFragment;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.PermissionHelper;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.TimeTools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import static android.content.Context.LOCATION_SERVICE;
import static com.kiminonawa.mydiary.shared.PermissionHelper.REQUEST_ACCESS_FINE_LOCATION_PERMISSION;
import static com.kiminonawa.mydiary.shared.PermissionHelper.REQUEST_CAMERA_AND_WRITE_ES_PERMISSION;


/**
 * This page doesn't be used in the movie.
 * I define this page for write diary.
 */

public class DiaryFragment extends BaseDiaryFragment implements View.OnClickListener,
        LocationListener, DiaryPhotoBottomSheet.PhotoCallBack, Observer, SaveDiaryTask.SaveDiaryCallBack,
        CopyPhotoTask.CopyPhotoCallBack, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    private String TAG = "DiaryFragment";

    /**
     * UI
     */
    private LinearLayout LL_diary_item_content, LL_diary_time_information;
    private RelativeLayout RL_diary_info, RL_diary_edit_bar;
    private TextView TV_diary_month, TV_diary_date, TV_diary_day, TV_diary_time, TV_diary_location;
    private Spinner SP_diary_weather, SP_diary_mood;
    private EditText EDT_diary_title;
    private ImageView IV_diary_menu, IV_diary_location, IV_diary_photo, IV_diary_delete, IV_diary_clear, IV_diary_save;
    private TextView TV_diary_item_content_hint;
    /**
     * Location
     */
    private String noLocation;
    private Location diaryLocations = null;
    private LocationManager locationManager;
    private String locationProvider;
    private DiaryHandler diaryHandler = new DiaryHandler(this);
    private boolean isLocation;

    /**
     * Lazy load
     */
    private boolean isCreatedView = false;

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
    private FileManager tempFileManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        timeTools = TimeTools.getInstance(getActivity().getApplicationContext());
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locationProvider = LocationManager.NETWORK_PROVIDER;
        isLocation = SPFManager.getDiaryLocation(getActivity());
        noLocation = getActivity().getString(R.string.diary_no_location);
        //The file is not editable
        tempFileManager = new FileManager(getActivity(), FileManager.TEMP_DIR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diary, container, false);

        RL_diary_info = (RelativeLayout) rootView.findViewById(R.id.RL_diary_info);
        RL_diary_edit_bar = (RelativeLayout) rootView.findViewById(R.id.RL_diary_edit_bar);
        RL_diary_info.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
        RL_diary_edit_bar.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        LL_diary_time_information = (LinearLayout) rootView.findViewById(R.id.LL_diary_time_information);
        LL_diary_time_information.setOnClickListener(this);
        TV_diary_month = (TextView) rootView.findViewById(R.id.TV_diary_month);
        TV_diary_date = (TextView) rootView.findViewById(R.id.TV_diary_date);
        TV_diary_day = (TextView) rootView.findViewById(R.id.TV_diary_day);
        TV_diary_time = (TextView) rootView.findViewById(R.id.TV_diary_time);
        TV_diary_location = (TextView) rootView.findViewById(R.id.TV_diary_location);

        SP_diary_weather = (Spinner) rootView.findViewById(R.id.SP_diary_weather);
        SP_diary_weather.setVisibility(View.VISIBLE);
        SP_diary_mood = (Spinner) rootView.findViewById(R.id.SP_diary_mood);
        SP_diary_mood.setVisibility(View.VISIBLE);

        EDT_diary_title = (EditText) rootView.findViewById(R.id.EDT_diary_title);
        EDT_diary_title.getBackground().mutate().setColorFilter(ThemeManager.getInstance().getThemeMainColor(getActivity()),
                PorterDuff.Mode.SRC_ATOP);

        TV_diary_item_content_hint = (TextView) rootView.findViewById(R.id.TV_diary_item_content_hint);
        TV_diary_item_content_hint.setVisibility(View.VISIBLE);
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
        if (!isCreatedView) {
            initWeatherSpinner();
            initMoodSpinner();
            setCurrentTime(true);
            initLocationIcon();
            diaryItemHelper = new DiaryItemHelper(LL_diary_item_content);
            clearDiaryPage();
        }
        isCreatedView = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isCreatedView) {
            if (isVisibleToUser) {
                diaryItemHelper.addObserver(this);
            } else {
                diaryItemHelper.deleteObserver(this);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //For PermissionsResult
        if (firstAllowLocationPermission) {
            initDiaryGPS();
            firstAllowLocationPermission = false;
        }
        //For PermissionsResult
        if (firstAllowCameraPermission) {
            openPhotoBottomSheet();
            firstAllowCameraPermission = false;
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        diaryHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.REQUEST_ACCESS_FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    firstAllowLocationPermission = true;
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.diary_location_permission_title))
                            .setMessage(getString(R.string.diary_location_permission_content))
                            .setPositiveButton(getString(R.string.dialog_button_ok), null);
                    builder.show();
                    TV_diary_location.setText(noLocation);
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
            String tempFileSrc = tempFileManager.getDiaryDir().getAbsolutePath() + "/" + fileName;
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


    private void initDiaryGPS() {
        if (isLocation && locationManager.isProviderEnabled(locationProvider)) {
            if (PermissionHelper.checkPermission(this, REQUEST_ACCESS_FINE_LOCATION_PERMISSION)) {
                if (locationManager.isProviderEnabled(locationProvider)) {
                    locationManager.requestLocationUpdates(locationProvider, 3000, 0, this);
                }
                //Waiting gps max timeout is 20s
                diaryHandler.sendEmptyMessageDelayed(0, 20000);
            }
        } else {
            TV_diary_location.setText(noLocation);
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


    private void initLocationIcon() {
        if (isLocation) {
            IV_diary_location.setImageResource(R.drawable.ic_location_on_white_24dp);
            initDiaryGPS();
        } else {
            diaryHandler.removeCallbacksAndMessages(null);
            IV_diary_location.setImageResource(R.drawable.ic_location_off_white_24dp);
            TV_diary_location.setText(noLocation);
        }
    }

    private void initWeatherSpinner() {
        ImageArrayAdapter weatherArrayAdapter = new ImageArrayAdapter(getActivity(), DiaryInfoHelper.getWeatherArray());
        SP_diary_weather.setAdapter(weatherArrayAdapter);
    }

    private void initMoodSpinner() {
        ImageArrayAdapter moodArrayAdapter = new ImageArrayAdapter(getActivity(), DiaryInfoHelper.getMoodArray());
        SP_diary_mood.setAdapter(moodArrayAdapter);
    }

    private void clearDiaryPage() {
        SP_diary_mood.setSelection(0);
        SP_diary_weather.setSelection(0);
        EDT_diary_title.setText("");
        diaryItemHelper.initDiary();
        tempFileManager.clearDiaryDir();
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
                locationName, diaryItemHelper, tempFileManager, this).execute(getTopicId());
    }

    private void openPhotoBottomSheet() {
        DiaryPhotoBottomSheet diaryPhotoBottomSheet = DiaryPhotoBottomSheet.newInstance(false);
        diaryPhotoBottomSheet.setTargetFragment(this,0);
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

    @Override
    public void selectPhoto(Uri uri) {
        if (FileManager.isImage(
                FileManager.getFileNameByUri(getActivity(), uri))) {
            //1.Copy bitmap to temp for rotating & resize
            //2.Then Load bitmap call back ;
            new CopyPhotoTask(getActivity(), uri,
                    DiaryItemHelper.getVisibleWidth(), DiaryItemHelper.getVisibleHeight(),
                    tempFileManager, this).execute();
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
                tempFileManager, this).execute();
    }

    @Override
    public void onCopyCompiled(String fileName) {
        loadFileFromTemp(fileName);
    }


    @Override
    public void update(Observable observable, Object data) {
        if (diaryItemHelper.getItemSize() > 0) {
            TV_diary_item_content_hint.setVisibility(View.GONE);
            setIsCreating(true);
        } else {
            TV_diary_item_content_hint.setVisibility(View.VISIBLE);
            setIsCreating(false);
        }
    }

    @Override
    public void onDiarySaved() {
        //For next diary
        setCurrentTime(true);
        //Clear
        clearDiaryPage();
        //Set flag
        ((DiaryActivity) getActivity()).setEntriesRefresh(true);
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
                isLocation = !isLocation;
                SPFManager.setDiaryLocation(getActivity(), isLocation);
                initLocationIcon();
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
                clearDiaryPage();
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
            }
        }

        private String getLocationName(DiaryFragment theFrag) {
            String returnLocation = theFrag.noLocation;
            if (theFrag.diaryLocations == null) {
                try {
                    theFrag.diaryLocations = theFrag.locationManager.getLastKnownLocation(theFrag.locationProvider);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            List<String> providerList = theFrag.locationManager.getAllProviders();
            try {
                if (null != theFrag.diaryLocations && null != providerList && providerList.size() > 0) {
                    double longitude = theFrag.diaryLocations.getLongitude();
                    double latitude = theFrag.diaryLocations.getLatitude();
                    Geocoder geocoder = new Geocoder(theFrag.getActivity().getApplicationContext(), Locale.getDefault());
                    List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (null != listAddresses && listAddresses.size() > 0) {
                        returnLocation = listAddresses.get(0).getAddressLine(0);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                theFrag.diaryLocations = null;
            }
            return returnLocation;
        }
    }
}
