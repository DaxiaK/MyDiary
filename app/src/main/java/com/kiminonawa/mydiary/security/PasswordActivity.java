package com.kiminonawa.mydiary.security;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.kiminonawa.mydiary.R;

/**
 * Created by daxia on 2017/1/10.
 */

public class PasswordActivity extends AppCompatActivity {


    private EditText EDT_password_number_1, EDT_password_number_2, EDT_password_number_3,
            EDT_password_number_4;

    private Button But_password_key_1, But_password_key_2, But_password_key_3,
            But_password_key_4, But_password_key_5, But_password_key_6,
            But_password_key_7, But_password_key_8, But_password_key_9,
            But_password_key_cancel, But_password_key_0;
    private ImageButton But_password_key_backspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        EDT_password_number_1 = (EditText) findViewById(R.id.EDT_password_number_1);
        EDT_password_number_2 = (EditText) findViewById(R.id.EDT_password_number_2);
        EDT_password_number_3 = (EditText) findViewById(R.id.EDT_password_number_3);
        EDT_password_number_4 = (EditText) findViewById(R.id.EDT_password_number_4);

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
    }
}
