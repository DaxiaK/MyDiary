package com.kiminonawa.mydiary.shared.file;

import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created by daxia on 2016/11/18.
 */

//
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永無Bug

public class LocalDir implements IDir {

    private static final String TAG = "LocalDir";

    private File fileDir;

    public LocalDir(File dir) {
        this.fileDir = dir;
    }


    public File getDir() {
        return fileDir;
    }

    public String getDirAbsolutePath() {
        return fileDir.getAbsolutePath();
    }

    public void clearDir() {
        try {
            if (fileDir != null && fileDir.isDirectory()) {
                FileUtils.cleanDirectory(fileDir);
            }
        } catch (Exception e) {
            Log.e(TAG, "ClearDir file", e);
        }
    }






}
