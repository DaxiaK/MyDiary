package com.kiminonawa.mydiary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.entries.diary.DiaryInfo;
import com.kiminonawa.mydiary.main.MainActivity;
import com.kiminonawa.mydiary.main.topic.ITopic;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ThemeManager;
import com.kiminonawa.mydiary.shared.UserProfile;


/**
 * Version History
 * 20161109
 * Add contacts function in version 10
 * ----
 * 20161108
 * Add memo function & show memo sample data in versionCode 6
 * ----
 */
public class InitActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    private static final String TAG = "InitActivity";


    /**
     * Google Sign-in
     */
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    /**
     * UI
     */
    private ProgressDialog mProgressDialog;

    /**
     * timer
     */
    private int initTime = 3000; // 3S
    private Handler initHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        loadSampleData();
        ThemeManager themeManager = ThemeManager.getInstance();
        themeManager.setCurrentTheme(SPFManager.getTheme(InitActivity.this));

        //Set google sign-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(this);


    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void gotoMainActivity() {
//        Go to mainactivity
        initHandler = new Handler();
        initHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent goMainPageIntent = new Intent(InitActivity.this, MainActivity.class);
                finish();
                InitActivity.this.startActivity(goMainPageIntent);
            }
        }, initTime);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            UserProfile profile = UserProfile.getInstance();
            profile.setName(acct.getDisplayName());
            profile.setEmail(acct.getEmail());
            profile.setPhoto(acct.getPhotoUrl());
            profile.setPersonId(acct.getId());
            gotoMainActivity();
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loadSampleData() {

        DBManager dbManager = new DBManager(InitActivity.this);
        dbManager.opeDB();
        //Because memo function is run in version 6 ,
        //So , if version < 6 , show the sample memo data
        if (SPFManager.getVersionCode(InitActivity.this) < 6) {
            //Insert sample topic
            long mitsuhaMemoId = dbManager.insertTopic("ゼッタイ禁止", ITopic.TYPE_MEMO);
            long takiMemoId = dbManager.insertTopic("禁止事項 Ver.5", ITopic.TYPE_MEMO);
            //Insert sample memo
            if (mitsuhaMemoId != -1) {
                dbManager.insertMemo("女子にも触るな！", false, mitsuhaMemoId);
                dbManager.insertMemo("男子に触るな！", false, mitsuhaMemoId);
                dbManager.insertMemo("脚をひらくな！", true, mitsuhaMemoId);
                dbManager.insertMemo("体は見ない/触らない！！", false, mitsuhaMemoId);
                dbManager.insertMemo("お風呂ぜっっったい禁止！！！！！！！", true, mitsuhaMemoId);
            }
            if (takiMemoId != -1) {
                dbManager.insertMemo("司とベタベタする.....", true, takiMemoId);
                dbManager.insertMemo("奧寺先輩と馴れ馴れしくするな.....", true, takiMemoId);
                dbManager.insertMemo("女言葉NG！", false, takiMemoId);
                dbManager.insertMemo("遅刻するな！", true, takiMemoId);
                dbManager.insertMemo("訛り禁止！", false, takiMemoId);
                dbManager.insertMemo("無駄つかい禁止！", true, takiMemoId);
            }
        }

        if (SPFManager.getFirstRun(InitActivity.this)) {
            //Insert sample topic
            long diaryId = dbManager.insertTopic("DIARY", ITopic.TYPE_DIARY);
            if (diaryId != -1) {
                //Insert sample diary
                dbManager.insertDiary(1475665800000L, "東京生活3❤",
                        "There are many coffee shop in Tokyo!",
                        DiaryInfo.MOOD_HAPPY, DiaryInfo.WEATHER_RAINY, true, diaryId, "Tokyo");
                dbManager.insertDiary(1475241600000L, "No Title",
                        "My name is TAKI , I am a man!",
                        DiaryInfo.MOOD_SOSO, DiaryInfo.WEATHER_SUNNY, true, diaryId, "Itomori");
                dbManager.insertDiary(1475144400000L, "東京生活2",
                        "Today is second day , I like Tokyo!",
                        DiaryInfo.MOOD_UNHAPPY, DiaryInfo.WEATHER_CLOUD, false, diaryId, "Tokyo");
                SPFManager.setFirstRun(InitActivity.this, false);
            }
        }

        //Contacts function work in version 10
        if (SPFManager.getVersionCode(InitActivity.this) < 10) {
            //Insert sample cntacts
            long sampleContactsId = dbManager.insertTopic("緊急狀況以外不要聯絡", ITopic.TYPE_CONTACTS);

            //Insert sample memo
            if (sampleContactsId != -1) {
                dbManager.insertContacts(getString(R.string.profile_username_mitsuha), "090000000", "", sampleContactsId);
            }
        }

        dbManager.closeDB();

        //Save currentVersion
        if (SPFManager.getVersionCode(InitActivity.this) < BuildConfig.VERSION_CODE) {
            SPFManager.setVersionCode(InitActivity.this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

}
