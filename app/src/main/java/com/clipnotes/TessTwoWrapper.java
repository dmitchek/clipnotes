package com.clipnotes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.AsyncTask;

import com.clipnotes.activity.Results;
import com.googlecode.tesseract.android.TessBaseAPI;


/**
 * Created by dave on 1/17/15.
 */
public class TessTwoWrapper {

    public static final String OCR_RESULTS = "com.clipnotes.OCR_RESULTS";
    public static final String DATA_PATH = "/tesseract/";
    private static final String LANG = "eng";

    public static void doOcr(final Context context, final Bitmap[] bitmaps, final Intent intent)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = bitmaps.length;
                String [] results = new String [count];

                TessBaseAPI baseApi = new TessBaseAPI();
                baseApi.init(context.getFilesDir() + DATA_PATH, LANG);

                for(int i = 0; i < count; i++)
                {
                    baseApi.setImage(bitmaps[i]);
                    results[i] = baseApi.getUTF8Text();
                    bitmaps[i].recycle();
                }

                baseApi.end();

                // Post results as an intent
                intent.putExtra("results", results);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        }).start();

    }



    /*private Bitmap getBitmap()
    {
        // _path = path to the image to be OCRed
        ExifInterface exif = new ExifInterface(_path);
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        /*if (rotate != 0) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        }
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        return bitmap;
    }*/
}
