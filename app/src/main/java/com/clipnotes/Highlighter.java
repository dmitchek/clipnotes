package com.clipnotes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by dave on 1/24/15.
 */
public class Highlighter extends RelativeLayout {

    private final String TAG = "Highlighter";
    private float mStartX;
    private float mStartY;

    private float mEndX;
    private float mEndY;

    private Bitmap mBackground = null;

    private boolean mHighlighting = false;

    private int mCurTextHeight;

    private Resources mRes;

    private ArrayList<Point> mPoints;

    private ArrayList<ArrayList<Point> > mPreviousPoints;

    public class Line{
        public Rect rect;
        public int textHeight;

        Line(Rect r, int height)
        {
            rect = r;
            textHeight = height;
        }
    }

    private Vector<Line> mLines = new Vector<Line>();

    public Highlighter(Context context, AttributeSet attrs)
    {

        super(context, attrs);

        mPreviousPoints = new ArrayList<>();

        mRes = getResources();
        mCurTextHeight = (int)mRes.getDimension(R.dimen.HIGHLIGHTER_MEDIUM_SIZE);
    }

    public void setBackground(Bitmap background)
    {
        mBackground = background;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                mStartX = ev.getX();
                mStartY = ev.getY();

                mPoints = new ArrayList<>();

                Point point = new Point();
                point.set((int)mStartX, (int)mStartY);
                mPoints.add(point);

                //highlightText();

                break;
            }

            case MotionEvent.ACTION_MOVE: {

                mEndX = ev.getX();
                mEndY = ev.getY();

                Point point = new Point();
                point.set((int)mEndX, (int)mEndY);
                mPoints.add(point);

                mHighlighting = true;
                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                saveRect();

                mPreviousPoints.add(mPoints);
                mPoints = new ArrayList<>();

                mEndX = mStartX;
                mEndY = mStartY;

                mHighlighting = false;
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            default:
            {
                mEndX = mStartX;
                mEndY = mStartY;
                mHighlighting = false;
                break;
            }
        }

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint pathPaint = new Paint();
        pathPaint.setColor(Color.rgb(255, 255, 0));
        //pathPaint.setStrokeWidth(mCurTextHeight);
        pathPaint.setAlpha(70);
        //pathPaint.setStyle(;

        // Draw previous Paths
        for(int i = 0; i < mPreviousPoints.size(); i++)
        {
            ArrayList<Point> points = mPreviousPoints.get(i);

            int textHeight = getLines().get(i).textHeight;

            for (int j = 0; j < points.size(); j++) {

                int y1 = points.get(j).y - (textHeight / 2);
                int y2 = points.get(j).y + (textHeight / 2);

                int startX = 0;
                if(j > 0)
                    startX = points.get(j-1).x;
                else
                    startX = (int)mStartX;


                canvas.drawRect(startX, y1, points.get(j).x, y2, pathPaint);
            }
        }


        if(mPoints != null) {
            for (int i = 0; i < mPoints.size(); i++) {
                int y1 = mPoints.get(i).y - (mCurTextHeight / 2);
                int y2 = mPoints.get(i).y + (mCurTextHeight / 2);

                int startX = 0;
                if(i > 0)
                    startX = mPoints.get(i-1).x;
                else
                    startX = (int)mStartX;

                canvas.drawRect(startX, y1, mPoints.get(i).x, y2, pathPaint);
            }
        }
    }

    @Override
    public float getX()
    {
        return mStartX;
    }

    @Override
    public float getY()
    {
        return mStartY;
    }

    public void setTextHeight(int textHeight)
    {
        if(!mHighlighting)
            mCurTextHeight = textHeight;
    }

    public int width()
    {
        return (int)Math.abs(mEndX - mStartX);
    }

    public int height()
    {
        return (int)Math.abs(mEndY - mStartY);
    }

    private void saveRect()
    {
        int minY = Integer.MAX_VALUE;
        int maxY = 0;

        for(int i = 0; i < mPoints.size(); i++) {
            if (minY > mPoints.get(i).y)
                minY = mPoints.get(i).y;

            if (maxY < mPoints.get(i).y)
                maxY = mPoints.get(i).y;
        }
        Rect rect = new Rect((int)mStartX, minY - (mCurTextHeight/2),
                (int)mEndX, maxY + (mCurTextHeight/2));

        mLines.add(new Line(rect, mCurTextHeight));
    }

    public Vector<Line> getLines() { return mLines; }

    /*private void highlightText()
    {
        int width = 300;
        int height = 100;
        int[] pixels=new int[width * height];
        mBackground.getPixels(pixels, 0, -1, 0, 0, width, height);
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                int pixel=pixels[y*width+x];

                if(pixel < 200)
                    pixel = 0;
                else
                    pixel = 255;

                pixels[y*width+x] = pixel;
            }
        }
        mBackground.setPixels(pixels, 0, width, 0, 0, width, height);

    }*/

}
