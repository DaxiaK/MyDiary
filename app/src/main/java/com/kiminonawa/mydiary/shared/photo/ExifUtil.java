package com.kiminonawa.mydiary.shared.photo;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import com.kiminonawa.mydiary.shared.FileManager;

import java.io.IOException;

/**
 * Created by daxia on 2016/11/22.
 *
 * @see:https://gist.github.com/9re/1990019#file-exifutil-java
 * @see:http://sylvana.net/jpegcrop/exif_orientation.html
 */

public class ExifUtil {

    public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(src);
            return rotate(bitmap, orientation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap rotateBitmap(Context context, Uri uri, Bitmap bitmap) {
        String path = FileManager.getRealPathFromURI(context, uri);
        if (path == null) {
            return bitmap;
        }
        return rotateBitmap(path, bitmap);
    }

    private static Bitmap rotate(Bitmap bitmap, int orientation) {
        if (orientation == 1) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        switch (orientation) {
            case 2:
                matrix.setScale(-1, 1);
                break;
            case 3:
                matrix.setRotate(180);
                break;
            case 4:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case 5:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case 6:
                matrix.setRotate(90);
                break;
            case 7:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case 8:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }

        try {
            Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return oriented;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;

        try {
            ExifInterface exifInterface = new ExifInterface(src);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return orientation;
    }
}
