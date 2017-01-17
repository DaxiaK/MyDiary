package com.kiminonawa.mydiary.security;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.main.MainActivity;
import com.kiminonawa.mydiary.shared.Encryption;
import com.kiminonawa.mydiary.shared.MyDiaryApplication;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.statusbar.ChinaPhoneHelper;

/**
 * Created by daxia on 2017/1/10.
 */

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * Mode
     */
    public static final int FAIL = -1;
    public static final int CREATE_PASSWORD = 0;
    public static final int CREATE_PASSWORD_WITH_VERIFY = 1;
    public static final int VERIFY_PASSWORD = 2;
    public static final int REMOVE_PASSWORD = 3;

    private int currentMode;
    /**
     * Password keeper
     */
    private int passwordPointer = 0;
    //the current password
    private StringBuilder passwordStrBuilder;
    //For verify password;
    private String createdPassword;

    //For start Main activity
    private boolean showReleaseNote;

    /**
     * UI
     */
    private ImageView IV_password_number_1, IV_password_number_2, IV_password_number_3,
            IV_password_number_4;

    private Button But_password_key_1, But_password_key_2, But_password_key_3,
            But_password_key_4, But_password_key_5, But_password_key_6,
            But_password_key_7, But_password_key_8, But_password_key_9,
            But_password_key_cancel, But_password_key_0;
    private ImageButton But_password_key_backspace;


    private TextView TV_password_message, TV_password_sub_message;


    //start lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        //For set status bar
        ChinaPhoneHelper.setStatusBar(this, true);

        //Get this page mode
        currentMode = getIntent().getIntExtra("password_mode", FAIL);
        showReleaseNote = getIntent().getBooleanExtra("showReleaseNote", false);
        if (currentMode == FAIL) {
            finish();
        }

        IV_password_number_1 = (ImageView) findViewById(R.id.IV_password_number_1);
        IV_password_number_2 = (ImageView) findViewById(R.id.IV_password_number_2);
        IV_password_number_3 = (ImageView) findViewById(R.id.IV_password_number_3);
        IV_password_number_4 = (ImageView) findViewById(R.id.IV_password_number_4);


        TV_password_message = (TextView) findViewById(R.id.TV_password_message);
        TV_password_sub_message = (TextView) findViewById(R.id.TV_password_sub_message);

        But_password_key_1 = (Button) findViewById(R.id.But_password_key_1);
        But_password_key_2 = (Button) findViewById(R.id.But_password_key_2);
        But_password_key_3 = (Button) findViewById(R.id.But_password_key_3);
        But_password_key_4 = (Button) findViewById(R.id.But_password_key_4);
        But_password_key_5 = (Button) findViewById(R.id.But_password_key_5);
        But_password_key_6 = (Button) findViewById(R.id.But_password_key_6);
        But_password_key_7 = (Button) findViewById(R.id.But_password_key_7);
        But_password_key_8 = (Button) findViewById(R.id.But_password_key_8);
        But_password_key_9 = (Button) findViewById(R.id.But_password_key_9);
        But_password_key_cancel = (Button) findViewById(R.id.But_password_key_cancel);
        But_password_key_0 = (Button) findViewById(R.id.But_password_key_0);
        But_password_key_backspace = (ImageButton) findViewById(R.id.But_password_key_backspace);

        But_password_key_1.setOnClickListener(this);
        But_password_key_2.setOnClickListener(this);
        But_password_key_3.setOnClickListener(this);
        But_password_key_4.setOnClickListener(this);
        But_password_key_5.setOnClickListener(this);
        But_password_key_6.setOnClickListener(this);
        But_password_key_7.setOnClickListener(this);
        But_password_key_8.setOnClickListener(this);
        But_password_key_9.setOnClickListener(this);
        But_password_key_0.setOnClickListener(this);
        But_password_key_backspace.setOnClickListener(this);

        But_password_key_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clearUiPassword();
        initUI();
    }

    private void initUI() {
        IV_password_number_1.requestFocus();
        switch (currentMode) {
            case CREATE_PASSWORD:
                TV_password_message.setText(getString(R.string.password_create_pwd));
                break;
            case CREATE_PASSWORD_WITH_VERIFY:
                TV_password_message.setText(getString(R.string.password_create_pwd_with_verify));
                break;
            case VERIFY_PASSWORD:
                TV_password_message.setText(getString(R.string.password_verify_pwd));
                But_password_key_cancel.setVisibility(View.INVISIBLE);
                But_password_key_cancel.setOnClickListener(null);
                break;
            case REMOVE_PASSWORD:
                TV_password_message.setText(getString(R.string.password_remove_pwd));
                break;
        }
    }

    private void setSubMessage() {
        switch (currentMode) {
            case CREATE_PASSWORD_WITH_VERIFY:
                TV_password_sub_message.setText(getString(R.string.password_create_pwd_with_verify_msg));
                break;
            case VERIFY_PASSWORD:
                TV_password_sub_message.setText(getString(R.string.password_verify_pwd_msg));
                break;
            case REMOVE_PASSWORD:
                TV_password_sub_message.setText(getString(R.string.password_remove_pwd_msg));
                break;
        }
    }

    private void clearUiPassword() {
        passwordPointer = 0;
        passwordStrBuilder = new StringBuilder();
        IV_password_number_1.setImageResource(R.drawable.ic_password_no_text_48dp);
        IV_password_number_2.setImageResource(R.drawable.ic_password_no_text_48dp);
        IV_password_number_3.setImageResource(R.drawable.ic_password_no_text_48dp);
        IV_password_number_4.setImageResource(R.drawable.ic_password_no_text_48dp);

        IV_password_number_1.clearColorFilter();
        IV_password_number_2.clearColorFilter();
        IV_password_number_3.clearColorFilter();
        IV_password_number_4.clearColorFilter();
    }

    private void afterPasswordChanged() {
        switch (currentMode) {
            case CREATE_PASSWORD:
                createdPassword = passwordStrBuilder.toString();
                clearUiPassword();
                currentMode = CREATE_PASSWORD_WITH_VERIFY;
                initUI();
                break;
            case CREATE_PASSWORD_WITH_VERIFY:
                if (createdPassword.equals(passwordStrBuilder.toString())) {
                    savePassword(Encryption.SHA256(passwordStrBuilder.toString()));
                    ((MyDiaryApplication) getApplication()).setHasPassword(true);
                    finish();
                } else {
                    clearUiPassword();
                    setSubMessage();
                }
                break;
            case VERIFY_PASSWORD:
                if (isPasswordCorrect(passwordStrBuilder.toString())) {
                    Intent goMainPageIntent = new Intent(this, MainActivity.class);
                    goMainPageIntent.putExtra("showReleaseNote", showReleaseNote);
                    finish();
                    this.startActivity(goMainPageIntent);
                } else {
                    clearUiPassword();
                    setSubMessage();
                }
                break;
            case REMOVE_PASSWORD:
                if (isPasswordCorrect(passwordStrBuilder.toString())) {
                    savePassword("");
                    ((MyDiaryApplication) getApplication()).setHasPassword(false);
                    finish();
                } else {
                    clearUiPassword();
                    setSubMessage();
                }
                break;
        }
    }


    private boolean isPasswordCorrect(String password) {
        return Encryption.SHA256(password).equals(SPFManager.getPassword(this));
    }

    private void savePassword(String password) {
        SPFManager.setAndEncryptPassword(this, password);
    }

    private void addPasswordStr(String passwordStr) {
        passwordStrBuilder.append(passwordStr);
        switch (passwordPointer) {
            case 0:
                IV_password_number_1.setImageResource(R.drawable.ic_password_dot_48dp);
                IV_password_number_1.setColorFilter(ThemeManager.getInstance().getThemeMainColor(this),
                        PorterDuff.Mode.SRC_ATOP);
                break;
            case 1:
                IV_password_number_2.setImageResource(R.drawable.ic_password_dot_48dp);
                IV_password_number_2.setColorFilter(ThemeManager.getInstance().getThemeMainColor(this),
                        PorterDuff.Mode.SRC_ATOP);
                break;
            case 2:
                IV_password_number_3.setImageResource(R.drawable.ic_password_dot_48dp);
                IV_password_number_3.setColorFilter(ThemeManager.getInstance().getThemeMainColor(this),
                        PorterDuff.Mode.SRC_ATOP);
                break;
            case 3:
                IV_password_number_4.setImageResource(R.drawable.ic_password_dot_48dp);
                IV_password_number_4.setColorFilter(ThemeManager.getInstance().getThemeMainColor(this),
                        PorterDuff.Mode.SRC_ATOP);
                break;
        }
        passwordPointer++;
    }

    private void removePasswordStr() {
        if (passwordPointer > 0) {
            passwordStrBuilder.setLength(passwordStrBuilder.length() - 1);
            switch (passwordPointer) {
                case 1:
                    IV_password_number_1.setImageResource(R.drawable.ic_password_no_text_48dp);
                    IV_password_number_1.clearColorFilter();
                    break;
                case 2:
                    IV_password_number_2.setImageResource(R.drawable.ic_password_no_text_48dp);
                    IV_password_number_2.clearColorFilter();
                    break;
                case 3:
                    IV_password_number_3.setImageResource(R.drawable.ic_password_no_text_48dp);
                    IV_password_number_3.clearColorFilter();
                    break;
            }
            passwordPointer--;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_password_key_1:
                addPasswordStr("1");
                break;
            case R.id.But_password_key_2:
                addPasswordStr("2");
                break;
            case R.id.But_password_key_3:
                addPasswordStr("3");
                break;
            case R.id.But_password_key_4:
                addPasswordStr("4");
                break;
            case R.id.But_password_key_5:
                addPasswordStr("5");
                break;
            case R.id.But_password_key_6:
                addPasswordStr("6");
                break;
            case R.id.But_password_key_7:
                addPasswordStr("7");
                break;
            case R.id.But_password_key_8:
                addPasswordStr("8");
                break;
            case R.id.But_password_key_9:
                addPasswordStr("9");
                break;
            case R.id.But_password_key_0:
                addPasswordStr("0");
                break;
            case R.id.But_password_key_backspace:
                removePasswordStr();
        }
        if (passwordPointer > 3) {
            afterPasswordChanged();
        }
    }
}
