package com.kiminonawa.mydiary.backup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;

/**
 * Created by daxia on 2017/2/14.
 */

public class BackupActivity extends AppCompatActivity implements View.OnClickListener,
        ExportAsyncTask.ExportCallBack {


    private final int EXPORT_SRC_PICKER_CODE = 0;
    private final int IMPORT_SRC_PICKER_CODE = 1;
    /*
     * UI
     */
    private TextView TV_backup_export_src, TV_backup_import_src;
    private MyDiaryButton But_backup_export, But_backup_import;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        //UI
        TV_backup_export_src = (TextView) findViewById(R.id.TV_backup_export_src);
        TV_backup_export_src.setOnClickListener(this);

        TV_backup_import_src = (TextView) findViewById(R.id.TV_backup_import_src);
        TV_backup_import_src.setOnClickListener(this);

        But_backup_export = (MyDiaryButton) findViewById(R.id.But_backup_export);
        But_backup_export.setOnClickListener(this);
        But_backup_export.setEnabled(false);

        But_backup_import = (MyDiaryButton) findViewById(R.id.But_backup_import);
        But_backup_import.setOnClickListener(this);
        But_backup_import.setEnabled(false);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == EXPORT_SRC_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = intent.getData();
            if (uri != null) {
                File file = com.nononsenseapps.filepicker.Utils.getFileForUri(uri);
                TV_backup_export_src.setText(file.getAbsolutePath());
                But_backup_export.setEnabled(true);
            }
        } else if (requestCode == IMPORT_SRC_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = intent.getData();
            if (uri != null) {
                File file = com.nononsenseapps.filepicker.Utils.getFileForUri(uri);
                TV_backup_import_src.setText(file.getAbsolutePath());
                But_backup_import.setEnabled(true);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.TV_backup_export_src:
                Intent exportIntent = new Intent(this, DirectoryPickerActivity.class);

                exportIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                exportIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
                exportIntent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
                exportIntent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                startActivityForResult(exportIntent, EXPORT_SRC_PICKER_CODE);
                break;
            case R.id.TV_backup_import_src:
                Intent importIntent = new Intent(this, DirectoryPickerActivity.class);

                importIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                importIntent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                importIntent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                importIntent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                startActivityForResult(importIntent, IMPORT_SRC_PICKER_CODE);
                break;
            case R.id.But_backup_export:
                new ExportAsyncTask(this, this, TV_backup_export_src.getText().toString())
                        .execute();
                break;
            case R.id.But_backup_import:

                break;
        }
    }

    @Override
    public void onExportCompiled(boolean exportSuccessful) {
        //TODO Add some step later (e.g. copy file or upload file)
    }
}
