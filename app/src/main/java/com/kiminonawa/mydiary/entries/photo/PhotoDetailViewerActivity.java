package com.kiminonawa.mydiary.entries.photo;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ScreenHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by daxia on 2017/4/12.
 */

public class PhotoDetailViewerActivity extends AppCompatActivity {


    public final static String DIARY_PHOTO_FILE_LIST = "DIARY_PHOTO_FILE_LIST";
    public final static String SELECT_POSITION = "SELECT_POSITION";

    /**
     * GUI
     */
    @BindView(R.id.VP_diary_photo_detail)
    ViewPager VPDiaryPhotoDetail;


    private PhotoDetailPagerAdapter mAdapter;
    private ArrayList<Uri> diaryPhotoFileList;
    private int selectPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Modify this activity into full screen mode
            ScreenHelper.closeInmmersiveMode(getWindow().getDecorView());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setStatusBarColor();
            }
        } else {
            setTheme(R.style.Theme_NoActionBar_FullScreen);
        }

        //Set the layout
        setContentView(R.layout.activity_diary_photo_detail_viewer);
        ButterKnife.bind(this);

        //Modify the status bar color
        diaryPhotoFileList = getIntent().getParcelableArrayListExtra(DIARY_PHOTO_FILE_LIST);
        selectPosition = getIntent().getIntExtra(SELECT_POSITION, -1);
        if (diaryPhotoFileList == null || selectPosition == -1) {
            Toast.makeText(this, getString(R.string.photo_viewer_photo_path_fail), Toast.LENGTH_LONG).show();
            finish();
        } else {
            //Init The view pager
            mAdapter = new PhotoDetailPagerAdapter(getSupportFragmentManager(), diaryPhotoFileList);
            VPDiaryPhotoDetail.setAdapter(mAdapter);
            VPDiaryPhotoDetail.setCurrentItem(selectPosition);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.BLACK);
    }

}