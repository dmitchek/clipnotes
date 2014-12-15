package com.example.highlighter.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.example.highlighter.CameraPreview;
import com.example.highlighter.R;
import com.example.highlighter.Mask;


/**
 * Created by dave on 12/13/14.
 */
public class Capture extends Activity {

    private Mask mMask;
    private Camera mCamera;
    private CameraPreview mPreview;

    private Handler mUpdateHandler;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);

        mMask = (Mask)findViewById(R.id.mask);

        mUpdateHandler = new Handler();

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
                showRingDialog();
            }
        });


    }

    /*@Override
    public void onPause()
    {
        super.onPause();

        //mCamera.release();
    }*/

    private void showRingDialog()
    {
        final ProgressDialog progressDialog = ProgressDialog.show(Capture.this,
                "Please wait", "Capturing text...", true);

        progressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setCancelable(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // Here you should write your time consuming task...

                    // Let the progress ring for 3 seconds...
                    Thread.sleep(3000);

                } catch (Exception e) {

                }

                progressDialog.dismiss();
                startActivity(new Intent(getApplicationContext(), Results.class));

            }

        }).start();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
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
    }
}
