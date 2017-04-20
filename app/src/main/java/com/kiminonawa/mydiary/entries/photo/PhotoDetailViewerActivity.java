package com.kiminonawa.mydiary.entries.photo;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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


    public final  static String DIARY_PHOTO_FILE_LIST ="DIARY_PHOTO_FILE_LIST";
    public final  static String SELECT_POSITION ="SELECT_POSITION";

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
        } else {
            setTheme(R.style.Theme_NoActionBar_FullScreen);
        }

        //Set the layout
        setContentView(R.layout.dialog_fragment_diary_photo_detail_viewer);
        ButterKnife.bind(this);

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

}