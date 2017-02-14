package com.kiminonawa.mydiary.shared.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by daxia on 2016/11/23.
 */

public class BitmapHelper {

    public static Bitmap getBitmapFromReturnedImage(Context context, Uri selectedImage, int reqWidth, int reqHeight) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(selectedImage);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        // Calculate inSampleSize
        options.inScaled = true;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            options.inPurgeable = true;
            options.inInputShareable = true;
        }
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        // close the input stream
        inputStream.close();

        // reopen the input stream
        inputStream = context.getContentResolver().openInputStream(selectedImage);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
        return bitmap;
    }

    public static Bitmap getBitmapFromTempFileSrc(String tempFileSrc, int reqWidth, int reqHeight) throws IOException {
        InputStream inputStream = new FileInputStream(tempFileSrc);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        // Calculate inSampleSize
        options.inScaled = true;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            options.inPurgeable = true;
            options.inInputShareable = true;
        }
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        // close the input stream
        inputStream.close();

        // reopen the input stream
        inputStream = new FileInputStream(tempFileSrc);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();
        return bitmap;
    }

    /**
     * Note: The decoder uses a final value based on powers of 2,
     * any other value will be rounded down to the nearest power of 2.
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, final int reqWidth, final int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;
        try {
            if ((height > reqHeight || width > reqWidth) && options.outHeight > 0 && options.outWidth > 0) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                //Old version (version =< 29):
                //  Choose the max ratio as inSampleSize value, I hope it can show fully without scrolling
                //New version (Version > 30)
                //  This bitmap is only for making photo size small ,
                // The only show one page method is implemented by fresco
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
                // This offers some additional logic in case the image has a strange
                // aspect ratio. For example, a panorama may have a much larger
                // width than height. In these cases the total pixels might still
                // end up being too large to fit comfortably in memory, so we should
                // be more aggressive with sample down the image (=larger inSampleSize).
                final float totalPixels = width * height;

                // Anything more than 2x the requested pixels we'll sample down further
                final float totalReqPixelsCap = reqWidth * reqHeight * 2;

                while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                    inSampleSize *= 2;
                }
            }
        }catch (Exception e){
            //For avoid crash
            e.printStackTrace();
            inSampleSize = 1;
        }
        return inSampleSize;
    }
}
