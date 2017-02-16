package com.kiminonawa.mydiary.backup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;

/**
 * Created by daxia on 2017/2/14.
 */

public class BackupActivity extends AppCompatActivity implements View.OnClickListener,
        ExportAsyncTask.ExportCallBack {


    /*
     * UI
     */
    private MyDiaryButton But_backup_export, But_backup_import;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        //UI
        But_backup_export = (MyDiaryButton) findViewById(R.id.But_backup_export);
        But_backup_export.setOnClickListener(this);
        But_backup_import = (MyDiaryButton) findViewById(R.id.But_backup_import);
        But_backup_import.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_backup_export:
                new ExportAsyncTask(this, this)
                        .execute();
                break;
            case R.id.But_backup_import:
                break;
        }
    }

    @Override
    public void onExportCompiled(boolean exportSuccessful) {
        //TODO send Notification
        Log.e("onExportCompiled", "Exporting is = " + exportSuccessful);
    }
}
