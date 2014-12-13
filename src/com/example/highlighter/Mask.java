package com.example.highlighter;

import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by dave on 12/13/14.
 */
public class Mask extends RelativeLayout {

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private float mLastTouchX;
    private float mLastTouchY;

    private float mPosX;
    private float mPosY;

    private final int INVALID_POINTER_ID = -1;

    private int mRadius = 10;

    private Point [] mCircles;
    private int mCircleIndex = -1;

    public Mask(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCircles = new Point[4];
        for(int i = 0; i < mCircles.length; i++)
            mCircles[i] = new Point(0,0);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int layout_width = getLayoutParams().width;
        int layout_height = getLayoutParams().height;

        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.rgb(0,0,100));
        rectPaint.setAlpha(100);

        Rect r = new Rect();
        r.top = mRadius;
        r.left = mRadius;
        r.right = layout_width-mRadius;
        r.bottom = layout_height-mRadius;
        canvas.drawRect(r, rectPaint);

        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.rgb(200,200,200));

        // Save circle coords
        mCircles[0].x = mRadius;
        mCircles[0].y = mRadius;
        mCircles[1].x = layout_width - mRadius;
        mCircles[1].y = mRadius;
        mCircles[2].x = mRadius;
        mCircles[2].y = layout_height - mRadius;
        mCircles[3].x = layout_width - mRadius;
        mCircles[3].y = layout_height - mRadius;

        int index = 0;
        for(Point p : mCircles) {

            if(index == mCircleIndex)
                circlePaint.setColor(Color.rgb(50,50,50));
            else
                circlePaint.setColor(Color.rgb(200, 200, 200));

            canvas.drawCircle(p.x, p.y, mRadius, circlePaint);
            index++;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                //final float x = MotionEventCompat.getX(ev, pointerIndex);
                //final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = ev.getRawX();
                mLastTouchY = ev.getRawY();

                int x = (int)mLastTouchX;
                int y = (int)mLastTouchY;

                int index = -1;
                int deltaX;
                int deltaY;
                int [] locations = new int[2];
                getLocationOnScreen(locations);
                for(Point p : mCircles)
                {
                    index++;

                    int circleX = locations[0] + p.x;
                    int circleY = locations[1] + p.y;
                    deltaX = Math.abs(x - circleX);
                    deltaY = Math.abs(y - circleY);
                    if( deltaX <= mRadius && deltaY <= mRadius )
                    {
                        mCircleIndex = index;
                        break;
                    }

                }

                // Save the ID of this pointer (for dragging)
                //mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                //final int pointerIndex =
                //        MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                final float x = ev.getRawX();
                final float y = ev.getRawY();

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                if(mCircleIndex >= 0)
                {
                    ViewGroup.LayoutParams params = getLayoutParams();

                    if(mCircleIndex == 0)
                    {
                        params.width -= dx;
                        params.height -= dy;

                        mPosX += dx;
                        mPosY += dy;
                    }
                    else if(mCircleIndex == 1)
                    {
                        params.width += dx;
                        params.height -= dy;

                        mPosY += dy;
                    }
                    else if(mCircleIndex == 2)
                    {
                        params.width -= dx;
                        params.height += dy;

                        mPosX += dx;
                    }
                    else if(mCircleIndex == 3)
                    {
                        params.width += dx;
                        params.height += dy;
                    }

                    setLayoutParams(params);

                    setX(mPosX);
                    setY(mPosY);
                }
                else {
                    mPosX += dx;
                    mPosY += dy;

                    setX(mPosX);
                    setY(mPosY);
                }


                invalidate();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mCircleIndex = -1;
                invalidate();
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mCircleIndex = -1;
                invalidate();
                break;
            }

        }
        return true;
    }



}
