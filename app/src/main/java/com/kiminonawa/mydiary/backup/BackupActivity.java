package com.kiminonawa.mydiary.backup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.FileManager;
import com.kiminonawa.mydiary.shared.gui.MyDiaryButton;

/**
 * Created by daxia on 2017/2/14.
 */

public class BackupActivity extends AppCompatActivity implements View.OnClickListener {


    /*
     * DB
     */
    private DBManager dbManager;
    private FileManager backupFileManager;
    /*
     * UI
     */
    private MyDiaryButton But_backup_export;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        //DB
        dbManager = new DBManager(this);

        //UI
        But_backup_export = (MyDiaryButton) findViewById(R.id.But_backup_export);
        But_backup_export.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_backup_export:

                break;
        }
    }
}
