package com.example.highlighter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

public class Main extends Activity {

    private View.OnClickListener _doOcrClick;
    private String _path = "";
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _doOcrClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    doOcr();
                }
                catch(IOException e)
                {
                    Log.e("Highlighter", "Whoops!" + e.getMessage());
                    e.getStackTrace();
                }
            }
        };

        Button doOcrBtn = (Button) findViewById(R.id.do_ocr_btn);
        doOcrBtn.setOnClickListener(_doOcrClick);

    }

    private void doOcr() throws IOException
    {


        String DATA_PATH = Environment.getExternalStorageDirectory() + "/tesseract/";
        String lang = "eng";

        TessBaseAPI baseApi = new TessBaseAPI();

        // lang = for which the language data exists, usually "eng"
        Log.v("Highlighter", "Data path is: " + DATA_PATH);
        baseApi.init(DATA_PATH, lang);
        // Eg. baseApi.init("/mnt/sdcard/tesseract/tessdata/eng.traineddata", "eng");

        ImageView text_sample = (ImageView)findViewById(R.id.text_sample);

        baseApi.setImage(transformImage("/highlighter/test_image.JPG"));
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        TextView resultTxt = (TextView)findViewById(R.id.result_text);

        // Got our first result!
        String result1 = recognizedText;

        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(transformImage("/highlighter/test_image2.JPG"));
        recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        String result2 = recognizedText;

        resultTxt.setText(getHighlightedText(result1, result2));
    }

    public Bitmap transformImage(String image_path) throws IOException
    {
        // _path = path to the image to be OCRed
        String path = Environment.getExternalStorageDirectory() + image_path;

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        ExifInterface exif = new ExifInterface(path);
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

        if (rotate != 0) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        }

        return bitmap.copy(Bitmap.Config.ARGB_8888, true);

    }

    public String getHighlightedText(String str1, String str2)
    {
        String diff = "";

        StringTokenizer strToken1 = new StringTokenizer(str1);
        StringTokenizer strToken2 = new StringTokenizer(str2);

        while(strToken1.hasMoreTokens())
        {
            String strTk1 = strToken1.nextToken();
            String strTk2 = strToken2.nextToken();

            if(compareWords(strTk1, strTk2))
                diff += strTk2 + " ";

        }

        return diff;
    }

    private boolean compareWords(String w1, String w2)
    {
        int tolerance = (int)(w1.length() * 0.4f);
        int errors = 0;

        char [] w1Chars = w1.toCharArray();
        char [] w2Chars = w2.toCharArray();

        for (int i = 0; i < w1.length(); i++) {

            if(i >= w2.length())
                errors++;
            else {
                if (w1Chars[i] != w2Chars[i]) {
                    errors++;
                }
            }

            if (errors >= tolerance)
                return false;
        }


        return true;
    }
}
