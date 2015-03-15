package com.clipnotes.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ExifInterface;
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
    private ImageView mCapturePreview;
    private String mCurrentPhotoPath = "";
    private Button mActionBtn;
    private boolean mCaptured = false;
    private float mDensity;
    private Resources mRes;
    private Button mSetHighlighterSmall;
    private Button mSetHighlighterMedium;
    private Button mSetHighlighterLarge;
    private View.OnClickListener mSetHighlighterListener;
    private Intent mResultIntent = null;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);

        mHighlighter = (Highlighter)findViewById(R.id.highlighter);

        mCapturePreview = (ImageView)findViewById(R.id.capture_preview);

        mActionBtn = (Button)findViewById(R.id.capture);

        mActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mCaptured)
                    performOcr();
                else
                    dispatchTakePictureIntent();

                //Intent intent = new Intent(getApplicationContext(), Results.class);
                //startActivity(intent);

            }
        });

        mSetHighlighterListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                int textHeight = 0;

                switch(id)
                {
                    case R.id.highlighter_size_small:
                        textHeight = (int)mRes.getDimension(R.dimen.HIGHLIGHTER_SMALL_SIZE);
                        break;

                    case R.id.highlighter_size_medium:
                        textHeight = (int)mRes.getDimension(R.dimen.HIGHLIGHTER_MEDIUM_SIZE);
                        break;

                    case R.id.highlighter_size_large:
                        textHeight = (int)mRes.getDimension(R.dimen.HIGHLIGHTER_LARGE_SIZE);
                        break;
                }

                if(textHeight > 0)
                    mHighlighter.setTextHeight(textHeight);
            }
        };

        mSetHighlighterSmall = (Button)findViewById(R.id.highlighter_size_small);
        mSetHighlighterSmall.setOnClickListener(mSetHighlighterListener);

        mSetHighlighterMedium = (Button)findViewById(R.id.highlighter_size_medium);
        mSetHighlighterMedium.setOnClickListener(mSetHighlighterListener);

        mSetHighlighterLarge = (Button)findViewById(R.id.highlighter_size_large);
        mSetHighlighterLarge.setOnClickListener(mSetHighlighterListener);

        mDensity = getApplicationContext().getResources().getDisplayMetrics().density;
        mRes = getResources();

        storeTrainedDataFile("eng");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(mOCRREsultsReceiver != null)
            unregisterReceiver(mOCRREsultsReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);*/

            if(mCurrentPhotoPath.length() > 0) {

                //Display display = getWindowManager().getDefaultDisplay();
                //Point size = new Point();
                //display.getSize(size);

                try {
                    mScaledCapture = loadScaledImage();

                    if(mScaledCapture != null)
                        mCapturePreview.setImageBitmap(mScaledCapture);

                    mCaptured = true;
                    mActionBtn.setText("Get Text");

                } catch (IOException e)
                {
                    Log.d(TAG, "Failed to load image");
                }
            }
        }
    }

    private Bitmap loadScaledImage() throws IOException
    {

        Bitmap scaledBitmap = null;

        ExifInterface exif = new ExifInterface(mCurrentPhotoPath);

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

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting pre rotate
        Matrix mtx = new Matrix();
        mtx.preRotate(rotate);

        Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        tmpBitmap = tmpBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Resources res = getResources();

        int scaleX = Math.round(mRes.getDimension(R.dimen.OCR_IMAGE_X));
        int scaleY = Math.round(mRes.getDimension(R.dimen.OCR_IMAGE_Y));

        scaledBitmap = Bitmap.createScaledBitmap(tmpBitmap,
                                                 scaleX,
                                                 scaleY,
                                                 false);

        tmpBitmap.recycle();
        System.gc();

        return scaledBitmap;
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

            mCaptured = false;
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

        mCapturePreview.setVisibility(View.GONE);

        Vector<Highlighter.Line> lines = mHighlighter.getLines();
        bitmaps = new Bitmap[lines.size()];

        Iterator<Highlighter.Line> iter = lines.iterator();

        int index = 0;
        while(iter.hasNext())
        {
            Highlighter.Line r = iter.next();
            bitmaps[index] = cropImageForProcessing(mScaledCapture, r.rect);
            index++;
        }

        mHighlighter.setVisibility(View.GONE);

        mScaledCapture.recycle();

        TessTwoWrapper.doOcr(getApplicationContext(), bitmaps, mResultIntent);
        System.gc();
    }

    private int getTranslatedX(int x)
    {
        return translateCord(0, x);
    }

    private int getTranslatedY(int y)
    {
        return translateCord(1, y);
    }

    private int translateCord(int axis, int value)
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if(axis == 0)
            return Math.round(value * (mRes.getDimension(R.dimen.OCR_IMAGE_X) / size.x));
        else
            return Math.round(value * (mRes.getDimension(R.dimen.OCR_IMAGE_Y) / size.y));
    }

    private Bitmap cropImageForProcessing(final Bitmap orgBitmap, Rect rect)
    {
        //Display display = getWindowManager().getDefaultDisplay();
        //Point size = new Point();
        //display.getSize(size);

         //float sx = size.x / mRes.getDimension(R.dimen.OCR_IMAGE_X);
        //float sy = size.y / mRes.getDimension(R.dimen.OCR_IMAGE_Y);
        //float s1 = sx > sy ? sx : sy;

        //int dy = line.endY - line.startY;
        //double deg = Math.atan2(Math.abs(dx), Math.abs(dy))*(180.0/Math.PI);

        //Matrix mtx = new Matrix();
        //mtx.postRotate(360.0F - (float)deg, mRes.getDimension(R.dimen.OCR_IMAGE_X)/2,
        //                           mRes.getDimension(R.dimen.OCR_IMAGE_Y)/2);

        //mtx.postScale(sx, sy);

        /*Bitmap tempBitmap = Bitmap.createBitmap(orgBitmap,
                                                0,
                                                0,
                                                (int)mRes.getDimension(R.dimen.OCR_IMAGE_X),
                                                (int)mRes.getDimension(R.dimen.OCR_IMAGE_Y),
                                                mtx,
                                                false);

        /*Canvas canvas = new Canvas(tempBitmap);

        Paint pathPaint = new Paint();
        pathPaint.setColor(Color.rgb(255, 255, 0));
        pathPaint.setStrokeWidth(line.height);
        pathPaint.setAlpha(70);
        pathPaint.setStyle(Paint.Style.STROKE);
        Path path = new Path();

        path.moveTo(getTranslatedX(line.startX), getTranslatedY(line.startY));
        path.lineTo(getTranslatedX(line.endX), getTranslatedY(line.startY));
        canvas.drawPath(path, pathPaint);*/

        int width = Math.abs(rect.right - rect.left);
        int height = Math.abs(rect.bottom - rect.top);

        return Bitmap.createBitmap(orgBitmap,
                getTranslatedX(rect.left),
                getTranslatedY(rect.top),
                getTranslatedX(width),
                getTranslatedX(height));
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
