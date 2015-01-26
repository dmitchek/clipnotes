package com.clipnotes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

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

    private int TEXT_HEIGHT = 80;

    public class Line{
        public int x;
        public int y;
        public int length;
        public int height;

        Line(int start, int top, int l)
        {
            x = start;
            y = top;
            length = l;
            height = TEXT_HEIGHT;
        }
    }

    private Vector<Line> mLines = new Vector<Line>();

    public Highlighter(Context context, AttributeSet attrs) {
        super(context, attrs);
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

                //highlightText();

                break;
            }

            case MotionEvent.ACTION_MOVE: {

                mEndX = ev.getX();
                mEndY = ev.getY();
                mHighlighting = true;
                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            {
                saveLine();
                mHighlighting = false;
                break;
            }
        }


        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.rgb(255, 255, 0));
        rectPaint.setAlpha(70);

        Rect r = new Rect();

        // Draw previous lines
        for(int i = 0; i < mLines.size(); i++)
        {
            r.top = mLines.get(i).y;
            r.left = mLines.get(i).x;
            r.right = mLines.get(i).x + mLines.get(i).length;
            r.bottom = mLines.get(i).y + TEXT_HEIGHT;

            canvas.drawRect(r, rectPaint);
        }


        r.top = (int)mStartY - (TEXT_HEIGHT/2);
        r.left = (int)mStartX;
        r.right = (int)mEndX;
        r.bottom = (int)mStartY + (TEXT_HEIGHT/2);
        canvas.drawRect(r, rectPaint);

        //canvas.drawBitmap(mBackground, 0, 0, new Paint());

        //int size = getWidth() * getHeight();
        //int [] pixels = new int[size];

        /*if(mBackground != null && mHighlighting) {

            try {
                mBackground.getPixels(pixels, 0, -1, (int) mStartX, (int) mStartY, width(), height());

                int darkest = 255;
                int lightest = 0;
                for (int i = 0; i < pixels.length; i++) {
                    if (pixels[i] > lightest)
                        lightest = pixels[i];

                    if (pixels[i] < darkest)
                        darkest = pixels[i];
                }

                for (int i = 0; i < pixels.length; i++) {
                    int color = pixels[i];

                    Paint pointPaint = new Paint();
                    pointPaint.setColor(Color.rgb(0, 0, 0));

                    if (color == darkest)
                        canvas.drawPoint(i % getWidth(), i % getHeight(), pointPaint);

                }

            } catch(IllegalArgumentException e) {
                Log.v(TAG, "IllegalArgumentException in onDraw(): width: " + width() +
                           " height: " + height());
            }
        }*/
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

    public int width()
    {
        return (int)Math.abs(mEndX - mStartX);
    }

    public int height()
    {
        return (int)Math.abs(mEndY - mStartY);
    }

    private void saveLine()
    {
        Line line = new Line((int)mStartX, (int)mStartY - (TEXT_HEIGHT/2), width());
        mLines.add(line);

        mEndX = mStartX;
        mEndY = mStartY;
    }

    public Vector<Line> getLines() { return mLines; }
    private void highlightText()
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

    }

}
