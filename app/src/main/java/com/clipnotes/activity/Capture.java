package com.clipnotes.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.clipnotes.CameraPreview;
import com.clipnotes.Highlighter;
import com.clipnotes.R;
import com.clipnotes.TessTwoWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


/**
 * Created by dave on 12/13/14.
 */
public class Capture extends Activity {

    private final String TAG = "Capture";
    private final int REQUEST_TAKE_PHOTO = 1;
    private Highlighter mHighlighter;
    private Camera mCamera;
    private CameraPreview mPreview;

    private Handler mUpdateHandler;
    private BroadcastReceiver mOCRREsultsReceiver;

    private ProgressDialog mProgressDialog;

    private Bitmap mScaledCapture;
    private ImageView mImageView;
    private String mCurrentPhotoPath = "";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);

        mHighlighter = (Highlighter)findViewById(R.id.highlighter);

        //mHighlighter.setBackground(mBackground);

        mImageView = (ImageView)findViewById(R.id.text_sample);

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

        storeTrainedDataFile("eng");

        dispatchTakePictureIntent();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        unregisterReceiver(mOCRREsultsReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);*/

            if(mCurrentPhotoPath.length() > 0) {

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                mScaledCapture = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath),
                        size.x, size.y, false);

                mImageView.setImageBitmap(mScaledCapture);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalCacheDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "Unable to create file to capture image");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void performOcr(){

        showRingDialog();

        Bitmap[] bitmaps;

        mImageView.setVisibility(View.GONE);

        //mBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sample_text_image),
        //        mImageView.getWidth(), mImageView.getHeight(), false);

        Vector<Highlighter.Line> lines = mHighlighter.getLines();
        bitmaps = new Bitmap[lines.size()];

        Iterator<Highlighter.Line> iter = lines.iterator();

        int index = 0;
        while(iter.hasNext())
        {
            Highlighter.Line line = iter.next();
            bitmaps[index] = cropImageForProcessing(mScaledCapture, line.x, line.y,
                                                            line.length, line.height);
            index++;
        }

        mHighlighter.setVisibility(View.GONE);

        mScaledCapture.recycle();

        TessTwoWrapper.doOcr(getApplicationContext(), bitmaps);
        System.gc();
    }

    private Bitmap cropImageForProcessing(final Bitmap orgBitmap, int startX, int startY,
                                                                  int width,  int height)
    {
        return Bitmap.createBitmap(orgBitmap, startX, startY, width, height);
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
            // TODO: find a better way to determine file size
            byte[] buffer = new byte[30000];

            try {
                InputStream in = getApplicationContext().getResources().openRawResource(R.raw.eng);
                FileOutputStream fout = new FileOutputStream(dataFile, true);

                int read;
                while((read = in.read(buffer)) != -1)
                    fout.write(buffer, 0, read);

                fout.close();
                in.close();
            }
            catch(IOException e)
            {
                Log.d(TAG, "Failed to copy traineddata file to filesystem:" + e.getMessage());
                return false;
            }
        }

        return true;
    }

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
