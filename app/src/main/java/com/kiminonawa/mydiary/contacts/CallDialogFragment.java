package com.kiminonawa.mydiary.contacts;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;


/**
 * Created by daxia on 2016/8/27.
 */
public class CallDialogFragment extends DialogFragment implements View.OnClickListener {


    /**
     * UI
     */
    private RelativeLayout RL_contacts_call_name;
    private TextView TV_contacts_call_name;
    private MyDiaryButton But_contacts_call_cancel, But_contacts_call_call;

    /**
     * Contacts Info
     */
    private String contactsName, contactsPhoneNumber;

    /**
     * Permission
     */
    private static final int REQUEST_CALL_PHONE_PERMISSION = 2;
    private boolean havePermission = false;
    private boolean RequestPermissionsResult = false;


    public static CallDialogFragment newInstance(String contactsName, String contactsPhoneNumber) {
        Bundle args = new Bundle();
        CallDialogFragment fragment = new CallDialogFragment();
        args.putString("contactsName", contactsName);
        args.putString("contactsPhoneNumber", contactsPhoneNumber);
        fragment.setArguments(args);
        return fragment;
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
        View rootView = inflater.inflate(R.layout.dialog_fragment_contacts_call, container);

        RL_contacts_call_name = (RelativeLayout) rootView.findViewById(R.id.RL_contacts_call_name);
        TV_contacts_call_name = (TextView) rootView.findViewById(R.id.TV_contacts_call_name);

        RL_contacts_call_name.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        But_contacts_call_call = (MyDiaryButton) rootView.findViewById(R.id.But_contacts_call_call);
        But_contacts_call_call.setOnClickListener(this);
        But_contacts_call_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_contacts_call_cancel);
        But_contacts_call_cancel.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (checkPermission(REQUEST_CALL_PHONE_PERMISSION)) {
            RequestPermissionsResult = true;
            havePermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        RequestPermissionsResult = true;
        switch (requestCode) {
            case REQUEST_CALL_PHONE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Do nothing , just show this dialog
                    havePermission = true;
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.contacts_call_phone_permission_title))
                            .setMessage(getString(R.string.contacts_call_phone_permission_content))
                            .setPositiveButton(getString(R.string.dialog_button_ok), null);
                    builder.show();
                    havePermission = false;
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (RequestPermissionsResult) {
            if (havePermission) {
                contactsName = getArguments().getString("contactsName", "");
                contactsPhoneNumber = getArguments().getString("contactsPhoneNumber", "");
                if (!"".equals(contactsName)) {
                    TV_contacts_call_name.setText(contactsName);
                }
            } else {
                dismiss();
            }
        }
    }

    private boolean checkPermission(final int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CALL_PHONE)) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                        requestCode);
                return false;
            } else {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                        requestCode);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_contacts_call_call:
                TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
                    //No module for calling phone
                    Toast.makeText(getActivity(), getString(R.string.contacts_call_phone_no_call_function), Toast.LENGTH_LONG)
                            .show();
                } else {
                    //Can call phone
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactsPhoneNumber));
                    startActivity(intent);
                }
                dismiss();
                break;
            case R.id.But_contacts_call_cancel:
                dismiss();
                break;
        }
    }


}
