package com.clipnotes.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.clipnotes.CameraPreview;
import com.clipnotes.R;
import com.clipnotes.Mask;
import com.clipnotes.TessTwoWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


/**
 * Created by dave on 12/13/14.
 */
public class Capture extends Activity {

    private final String TAG = "Capture";
    private Mask mMask;
    private Camera mCamera;
    private CameraPreview mPreview;

    private Handler mUpdateHandler;
    private BroadcastReceiver mOCRREsultsReceiver;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);

        mMask = (Mask)findViewById(R.id.mask);

        //mUpdateHandler = new Handler();

        // Create an instance of Camera
        // TODO: use this code when we actually want to use a camera
        /*mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);*/

        Button captureBtn = (Button)findViewById(R.id.capture);

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performOcr();
            }
        });

        mOCRREsultsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                String[] results = intent.getStringArrayExtra("results");

                TextView resultText = (TextView)findViewById(R.id.result_text);

                resultText.setText(results[0]);
            }
        };

        IntentFilter intent = new IntentFilter(TessTwoWrapper.OCR_RESULTS);
        registerReceiver(mOCRREsultsReceiver, intent);

        storeTrainedDataFile("eng");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mOCRREsultsReceiver);
    }

    private void performOcr(){

        showRingDialog();

        Bitmap[] bitmaps = new Bitmap[1];

        bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.text_sample);

        TessTwoWrapper.doOcr(getApplicationContext(), bitmaps);
    }

    private boolean storeTrainedDataFile(String lang)
    {
        File filesDir = getApplicationContext().getFilesDir();
        String dataFileDir = TessTwoWrapper.DATA_PATH + "/tessdata/";
        String dataFileName = lang + ".traineddata";
        File dataFile = new File(filesDir, dataFileDir + dataFileName);

        // trained data file does not exist so we must create it
        if(!dataFile.exists())
        {
            new File(filesDir, dataFileDir).mkdirs();

            // Create 30Mb buffer for file
            byte[] buffer = new byte[30000];

            try {
                InputStream in = getApplicationContext().getResources().openRawResource(R.raw.eng);
                FileOutputStream fout = new FileOutputStream(dataFile, true);

                int read;
                while((read = in.read(buffer)) != -1)
                    fout.write(buffer, 0, read);

                fout.close();
            }
            catch(IOException e)
            {
                Log.d(TAG, "Failed to copy traineddata file to filesystem:" + e.getMessage());
                return false;
            }
        }

        return true;
    }

    /*@Override
    public void onPause()
    {
        super.onPause();

        //mCamera.release();
    }*/

    private void showRingDialog()
    {
        mProgressDialog = ProgressDialog.show(Capture.this,
                "Please wait", "Capturing text...", true);

        mProgressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setCancelable(true);
    }

    /** A safe way to get an instance of the Camera object. */
    /*public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e("Capture", "Error loading camera: " + e.getMessage());

            c.release();
        }
        return c; // returns null if camera is unavailable
    }*/
}
