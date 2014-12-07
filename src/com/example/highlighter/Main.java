package com.example.highlighter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

public class Main extends Activity {

    private View.OnClickListener _doOcrClick;
    private String _path = "";
    private TextView _errorTxt;
    private TextView _resultTxt;
    private ProgressBar _spinner;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _errorTxt = (TextView)findViewById(R.id.error_text);
        _resultTxt = (TextView)findViewById(R.id.result_text);

        _spinner = (ProgressBar)findViewById(R.id.progress_bar);

        _spinner.setVisibility(View.GONE);

        _doOcrClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _resultTxt.setText("Working...");
                _spinner.setVisibility(View.VISIBLE);

                DoOcr ocr = new DoOcr();
                ocr.execute();

            }
        };

        Button doOcrBtn = (Button) findViewById(R.id.do_ocr_btn);
        doOcrBtn.setOnClickListener(_doOcrClick);

    }

    private class DoOcr extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params)
        {
            try
            {
                return getDiff();
            }
            catch(Exception e)
            {
                _errorTxt.setText(e.getMessage());
                e.getStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {

            _resultTxt.setText(result);
            _spinner.setVisibility(View.GONE);
        }

        private String getDiff() throws IOException
        {
            String DATA_PATH = Environment.getExternalStorageDirectory() + "/tesseract/";
            String lang = "eng";

            /*TessBaseAPI baseApi = new TessBaseAPI();

            // lang = for which the language data exists, usually "eng"
            Log.v("Highlighter", "Data path is: " + DATA_PATH);
            baseApi.init(DATA_PATH, lang);
            // Eg. baseApi.init("/mnt/sdcard/tesseract/tessdata/eng.traineddata", "eng");


            Log.v("Highlighter", "Starting image 1");
            baseApi.setImage(transformImage("/highlighter/test_image.JPG"));
            String recognizedText = baseApi.getUTF8Text();
            baseApi.end();


            // Got our first result!
            String result1 = recognizedText;
            Log.v("Highlighter", "Storing result 1");

            _resultTxt.setText(result1);

            Log.v("Highlighter", "Starting image 2");darkness; made his light shine in our hearts to give us the light of the knowledge of the glory of God in the face of Christ' (2 Corinthians 4:5-6). Paul's argument is that the preaching of Jesus as Lord (the center and heart of all reality, the one in control of all events) is a message that is honored by God, and God is a being of incredible power and authority. In fact, asdallailleINIOSSISmodiSaik assellitaigniIMININIlessasse Now why are these people perishing? Their minds, Paul said, are blinded; that is, they live in darkness. They have already turned from the normal way by which God proposes to save people—that is, by an honest response to reality (see Hebrews 11:6). But their case is not hopeless, for the God whom Paul preaches is _1 • T Zala •knit mffCF hAIVP_

            baseApi.init(DATA_PATH, lang);
            baseApi.setImage(transformImage("/highlighter/test_image2.JPG"));
            recognizedText = baseApi.getUTF8Text();
            baseApi.end();

            String result2 = recognizedText;
            Log.v("Highlighter", "Storing result 2");*/

            String result1 = "darkness,' made his light shine in our hearts to give us the light of the knowledge of" +
                             " the glory of God in the face of Christ\" (2 Corinthians 4:5-6). Paul's argument is that " +
                             "the preaching of Jesus as Lord (the center and heart of all reality, the one in control of all events) " +
                             "is a message that is honored by God, and God is a being of incredible power and authority. " +
                             "In fact, He is the one who at creation com-manded the light to shine out of darkness. Notice, " +
                             "He did not command the light to shine into the dark-ness—He literally commanded the darkness to " +
                             "produce light! Now why are these people perishing? Their minds, Paul said, are blinded; that is, they " +
                             "live in darkness. They have already turned from the normal way by which God proposes to save people—that is, " +
                             "by an honest response to reality (see Hebrews 11:6). But their case is not hopeless, for the God whom Paul preaches " +
                             "is -1 • e I_ ..1 T alarm nitici have_ ";

            String result2 = "darkness; made his light shine in our hearts to give us the light of the knowledge of the " +
                             "glory of God in the face of Christ' (2 Corinthians 4:5-6). Paul's argument is that the preaching " +
                             "of Jesus as Lord (the center and heart of all reality, the one in control of all events) is a message " +
                             "that is honored by God, and God is a being of incredible power and authority. In fact, " +
                             "asdallailleINIOSSISmodiSaik assellitaigniIMININIlessasse Now why are these people perishing? " +
                             "Their minds, Paul said, are blinded; that is, they live in darkness. They have already turned from " +
                             "the normal way by which God proposes to save people—that is, by an honest response to reality (see Hebrews 11:6). " +
                             "But their case is not hopeless, for the God whom Paul preaches is _1 • T Zala •knit mffCF hAIVP_ \n";

            return getHighlightedText(result1, result2);
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

                if(!compareWords(strTk1, strTk2)) {

                    // We hit our first word that didn't match
                    // Add the words from the first string until we hit a word
                    // we recognize from the second string
                    //strTk2 = strToken2.nextToken();
                    while(strToken1.hasMoreTokens() && !compareWords(strTk1, strTk2))
                    {
                        diff += strTk1;
                        diff += " ";

                        strTk1 = strToken1.nextToken();
                    }
                }
            }

            return diff;
        }

        private boolean compareWords(String w1, String w2)
        {
            int tolerance = (int)(w1.length() * 0.7f);
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

}
