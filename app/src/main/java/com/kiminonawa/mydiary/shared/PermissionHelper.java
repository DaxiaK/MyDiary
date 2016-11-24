package com.kiminonawa.mydiary.shared;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by daxia on 2016/11/23.
 */

public class PermissionHelper {


    /**
     * Permission
     */
    public static final int REQUEST_ACCESS_FINE_LOCATION_PERMISSION = 1;
    public static final int REQUEST_CAMERA_AND_WRITE_ES_PERMISSION = 2;

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
}
