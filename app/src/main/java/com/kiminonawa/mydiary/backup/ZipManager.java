package com.kiminonawa.mydiary.backup;

import android.content.Context;
import android.util.Log;

import com.kiminonawa.mydiary.shared.FileManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by daxia on 2017/2/16.
 */

public class ZipManager {

    private FileManager diaryFileManager;
    private final int BUFFER_SIZE = 2048;


    public ZipManager(Context context) {
        //Copy data form diary
        diaryFileManager = new FileManager(context, FileManager.DIARY_ROOT_DIR);
    }

    public boolean zipFileAtPath(String backupJsonFilePath, String toLocation) {

        File sourceFile = diaryFileManager.getDir();
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER_SIZE];
                FileInputStream fi = new FileInputStream(sourceFile);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                ZipEntry entry = new ZipEntry(sourceFile.getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                    out.write(data, 0, count);
                }
            }
            //Zip the json file
            zipBackupJsonFile(backupJsonFilePath, out);

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void zipBackupJsonFile(String backupJsonFilePath, ZipOutputStream out) throws IOException {
        byte data[] = new byte[BUFFER_SIZE];
        FileInputStream fi = new FileInputStream(backupJsonFilePath);
        BufferedInputStream jsonFileOrigin = new BufferedInputStream(fi, BUFFER_SIZE);
        ZipEntry entry = new ZipEntry(BackupManager.BACKUP_JSON_FILE_NAME);
        out.putNextEntry(entry);
        int count;
        while ((count = jsonFileOrigin.read(data, 0, BUFFER_SIZE)) != -1) {
            out.write(data, 0, count);
        }
    }

    /**
     * Zips a subfolder
     */

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER_SIZE];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    public void unzip(String backupZieFilePath, String location) throws IOException {
        int size;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if (!location.endsWith("/")) {
                location += "/";
            }
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(
                    new BufferedInputStream(
                            new FileInputStream(backupZieFilePath), BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if (null != parentDir) {
                            if (!parentDir.isDirectory()) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        } finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unzip exception", e);
        }
    }
}
