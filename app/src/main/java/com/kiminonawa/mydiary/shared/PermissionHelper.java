package com.kiminonawa.mydiary.shared;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.kiminonawa.mydiary.R;

/**
 * Created by daxia on 2016/11/23.
 */

public class PermissionHelper {


    /**
     * Permission
     */
    public static final int REQUEST_ACCESS_FINE_LOCATION_PERMISSION = 1;
    public static final int REQUEST_CAMERA_AND_WRITE_ES_PERMISSION = 2;
    public static final int REQUEST_WRITE_ES_PERMISSION = 3;

    public static boolean checkPermission(Fragment fragment, final int requestCode) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION_PERMISSION:
                if (ContextCompat.checkSelfPermission(fragment.getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                requestCode);
                        return false;
                    } else {
                        fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                requestCode);
                        return false;
                    }
                }
                break;
            case REQUEST_CAMERA_AND_WRITE_ES_PERMISSION:
                if (ContextCompat.checkSelfPermission(fragment.getActivity(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(fragment.getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(),
                            Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        fragment.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                requestCode);
                        return false;
                    } else {
                        fragment.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                requestCode);
                        return false;
                    }
                }
                break;
            case REQUEST_WRITE_ES_PERMISSION:
                if (ContextCompat.checkSelfPermission(fragment.getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                requestCode);
                        return false;
                    } else {
                        fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                requestCode);
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermission(Activity activity, final int requestCode) {
        switch (requestCode) {
            case REQUEST_WRITE_ES_PERMISSION:
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                requestCode);
                        return false;
                    } else {
                        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                requestCode);
                        return false;
                    }
                }
                break;
        }
        return true;
    }


    public static boolean checkAllPermissionResult(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void showAddPhotoDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.diary_location_permission_title))
                .setMessage(context.getString(R.string.diary_photo_permission_content))
                .setPositiveButton(context.getString(R.string.dialog_button_ok), null);
        builder.show();
    }

    public static void showAccessDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.diary_location_permission_title))
                .setMessage(context.getString(R.string.diary_location_permission_content))
                .setPositiveButton(context.getString(R.string.dialog_button_ok), null);
        builder.show();
    }

}
