package com.kiminonawa.mydiary.shared;

import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created by daxia on 2016/12/12.
 */

public class OldVersionHelper {


    public static boolean Version17MoveTheDiaryIntoNewDir(Context context) throws Exception {
        FileManager rootFileManager = new FileManager(context, FileManager.ROOT_DIR);
        File[] dataFiles = rootFileManager.getDir().listFiles();
        boolean moveIntoNewDir = false;
        //router all dir first
        for (int i = 0; i < dataFiles.length; i++) {
            if (FileManager.isNumeric(dataFiles[i].getName()) && dataFiles[i].listFiles().length > 0) {
                moveIntoNewDir = true;
                break;
            }
        }
        //If the numeric dir is exist , move it
        if (moveIntoNewDir) {
            FileManager diaryFM = new FileManager(context, FileManager.DIARY_ROOT_DIR);
            File destDir = diaryFM.getDir();
            FileUtils.deleteDirectory(destDir);
            for (int i = 0; i < dataFiles.length; i++) {
                if (FileManager.isNumeric(dataFiles[i].getName())) {
                    FileUtils.moveDirectoryToDirectory(dataFiles[i],
                            new FileManager(context, FileManager.DIARY_ROOT_DIR).getDir()
                            , true);
                }
            }
            //Remove the diary/temp/
            FileUtils.deleteDirectory(new File(diaryFM.getDirAbsolutePath() + "/temp"));
        }
        return moveIntoNewDir;
    }
}
