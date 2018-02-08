package com.kiminonawa.mydiary.shared;

import android.content.Context;

import com.kiminonawa.mydiary.shared.file.DirFactory;
import com.kiminonawa.mydiary.shared.file.IDir;
import com.kiminonawa.mydiary.shared.file.LocalDir;
import com.kiminonawa.mydiary.shared.file.MyDiaryFileUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created by daxia on 2016/12/12.
 */

public class OldVersionHelper {


    public static boolean Version17MoveTheDiaryIntoNewDir(Context context) throws Exception {
        IDir rootLocalDir = DirFactory.CreateDirByType(context, LocalDir.ROOT_DIR);
        File[] dataFiles = rootLocalDir.getDir().listFiles();
        boolean moveIntoNewDir = false;
        //router all dir first
        for (int i = 0; i < dataFiles.length; i++) {
            if (MyDiaryFileUtils.isNumeric(dataFiles[i].getName()) && dataFiles[i].listFiles().length > 0) {
                moveIntoNewDir = true;
                break;
            }
        }
        //If the numeric dir is exist , move it
        if (moveIntoNewDir) {
            IDir diaryDir = DirFactory.CreateDirByType(context, LocalDir.DIARY_ROOT_DIR);
            File destDir = diaryDir.getDir();
            FileUtils.deleteDirectory(destDir);
            for (int i = 0; i < dataFiles.length; i++) {
                if (MyDiaryFileUtils.isNumeric(dataFiles[i].getName())) {
                    FileUtils.moveDirectoryToDirectory(dataFiles[i],
                            DirFactory.CreateDirByType(context, LocalDir.DIARY_ROOT_DIR).getDir()
                            , true);
                }
            }
            //Remove the diary/temp/
            FileUtils.deleteDirectory(new File(diaryDir.getDirAbsolutePath() + "/temp"));
        }
        return moveIntoNewDir;
    }
}
