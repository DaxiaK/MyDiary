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

/**
 * Created by daxia on 2017/2/16.
 */

public class ZipManager {

    private String backupJsonFilePath;
    private FileManager diaryFileManager;
    private final int BUFFER_SIZE = 2048;


    public ZipManager(Context context, String backupJsonFilePath) {
        //Copy data form diary
        diaryFileManager = new FileManager(context, FileManager.DIARY_ROOT_DIR);
        this.backupJsonFilePath = backupJsonFilePath;
    }

    public boolean zipFileAtPath(String toLocation) {

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
            zipBackupJsonFile(out);

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void zipBackupJsonFile(ZipOutputStream out) throws IOException {
        byte data[] = new byte[BUFFER_SIZE];
        FileInputStream fi = new FileInputStream(backupJsonFilePath);
        BufferedInputStream jsonFileOrigin = new BufferedInputStream(fi, BUFFER_SIZE);
        ZipEntry entry = new ZipEntry("Output.json");
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

    public void unzip( String location) throws IOException {
        try {
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new FileInputStream(backupJsonFilePath));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();

                    if (ze.isDirectory()) {
                        File unzipFile = new File(path);
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        FileOutputStream fout = new FileOutputStream(path, false);
                        try {
                            for (int c = zin.read(); c != -1; c = zin.read()) {
                                fout.write(c);
                            }
                            zin.closeEntry();
                        } finally {
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            Log.e("ZIP", "Unzip exception", e);
        }
    }
}
