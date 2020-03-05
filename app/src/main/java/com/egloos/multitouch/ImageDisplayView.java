package com.egloos.multitouch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ImageDisplayView extends View {
    Paint paint;
    Matrix matrix;

    int lastX;
    int lastY;

    Bitmap mBitmap;
    Canvas mCanvas;

    float displayWidth = 0.0F;
    float displayHeight = 0.0F;

    int displayCenterX = 0;
    int displayCenterY = 0;

    public ImageDisplayView(Context context) {
        super(context);

        init(context);
    }

    public ImageDisplayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        paint = new Paint();
        matrix = new Matrix();

        lastX = -1;
        lastY = -1;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > 0 && h > 0){
            newImage(w, h);
        }
    }

    public void newImage(int width, int height){
        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);

        mBitmap = img;
        mCanvas = canvas;

        displayWidth = (float)width;
        displayHeight = (float)height;

        displayCenterX = width/2;
        displayCenterY = height/2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap != null){
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        int pointerCount = event.getPointerCount();
        Log.d(TAG, "Pointer Count : " + pointerCount);

        switch (action){
            case MotionEvent.ACTION_DOWN:
                if (pointerCount == 1){
                    float curX = event.getX();
                    float curY = event.getX();

                    startX = curX;
                    startY = curY;
                } else if (pointerCount == 2){
                    oldDistance = 0.0F;

                    isScrolling = true;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1){
                    if (isScrolling){
                        return true;
                    }

                    float curX = event.getX();
                    float curY = event.getY();

                    if (startX == 0.0F){
                        startX = curX;
                        startY = curY;

                        return true;
                    }

                    float offsetX = startX - curX;
                    float offsetY = startY - curY;

                    if (oldPointerCount == 2){

                    }else{
                        Log.d(TAG, "ACTION_MOVE : " + offsetX + ", " + offsetY);

                        if (totalScaleRatio > 1.0f){
                            moveImage(-offsetX, -offsetY);
                        }

                        startX = curX;
                        startY = curY;
                    }
                } else if (pointerCount == 2){
                    float x1 = event.getX(0);
                    float y1 = event.getY(0);
                    float x2 = event.getX(0);
                    float y2 = event.getX(0);

                    float dx = x1 - x2;
                    float dy = y1 - y2;
                    float distance = new Double(Math.sqrt(new Float(dx * dx + dy * dy).doubleValue())).floatValue();

                    float outScaleRatio = 0.0F;
                    if (oldDistance == 0.0F){
                        oldDistance = distance;

                        break;
                    }

                    if (distance > oldDistance){
                        if ((distance-oldDistance) < distanceThreshold){
                            return true;
                        }

                        outScaleRatio = scaleRatio + (oldDistance / distance * 0.05F);
                    }

                    if (outScaleRatio < MIN_SCALE_RATIO || outScaleRatio > MAX_SCALE_RATIO){
                        Log.d(TAG, "Invalid scaleRatio : " + outScaleRatio);
                    } else {
                        Log.d(TAG, "Distance : " + distance + ", ScaleRatio : " + outScaleRatio);
                        scaleImage(outScaleRatio);
                    }

                    oldDistance = distance;
                }
                oldPointerCount = pointerCount;

                break;
            case MotionEvent.ACTION_UP:
                if (pointerCount == 1){
                    float curX = event.getX();
                    float curY = event.getY();

                    float offsetX = startX - curX;
                    float offsetY = startY - curY;

                    if (oldPointerCount == 2){

                    } else {
                        moveImage(-offsetX, -offsetY);
                    }
                } else {
                    isScrolling = false;
                }

                return true;
        }
        return true;
    }

}
