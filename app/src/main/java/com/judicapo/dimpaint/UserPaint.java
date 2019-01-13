package com.judicapo.dimpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UserPaint extends View {

    private static int BRUSH_SIZE = 20;
    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private static final float TOTAL_RATIO = 100;
    private Random rnd = new Random();
    private Paint mPaint;
    private SparseArray<UserPath> paths;
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private GestureDetector gestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor;
    private CountDownTimer cTimer;
    private float centerX;
    private float centerY;
    public ViewType viewType;

    public UserPaint(Context context) {
        this(context, null);
    }

    public UserPaint(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gestureDetector = new GestureDetector(context, new GestureListener());
        this.mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(DEFAULT_COLOR);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setXfermode(null);
        this.mPaint.setAlpha(0xff);
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        this.viewType = ViewType.DESIGN;
        this.paths = new SparseArray<>();
        this.mScaleFactor = 1.0f;
        this.cTimer = null;
        this.centerX = width / 2;
        this.centerY = height / 2;

        this.mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.mCanvas = new Canvas(mBitmap);
        this.currentColor = DEFAULT_COLOR;
        this.strokeWidth = BRUSH_SIZE;
    }


    public void clear() {
        this.backgroundColor = DEFAULT_BG_COLOR;
        this.mScaleFactor = 1.0f;
        this.paths.clear();
        this.cancelTimer();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        for (int size = paths.size(), i = 0; i < size; i++) {
            UserPath fp = paths.valueAt(i);
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);
            canvas.scale(mScaleFactor, mScaleFactor);

            switch (this.viewType) {
                case NONE:
                    break;
                case DESIGN:
                    mCanvas.drawPath(fp, mPaint);
                    break;
                case VECTOR:
                    mCanvas.drawPath(fp, mPaint);
                    break;
                case SCALAR:
                    if (fp.top != 0 && fp.bottom != 0 && fp.right != 0 && fp.left != 0)
                        mCanvas.drawRect(fp.left,fp.top,fp.right,fp.bottom,mPaint);
                    break;
                case TIMER:
                    if (fp.top != 0 && fp.bottom != 0 && fp.right != 0 && fp.left != 0)
                        mCanvas.drawRect(fp.left,fp.top,fp.right,fp.bottom,mPaint);
                    else mCanvas.drawPath(fp, mPaint);
                    break;
            }

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(int pointId,float x, float y) {
        UserPath fp = new UserPath(this.currentColor, this.strokeWidth, x, y);
        fp.reset();
        fp.moveTo(x, y);
        switch (this.viewType) {
            case NONE:
                break;
            case DESIGN:
                paths.put(pointId, fp);
                break;
            case VECTOR:
                paths.put(this.paths.size(), fp);
                break;
            case SCALAR:
                paths.put(this.paths.size(), fp);
                break;
            case TIMER:
//                double d = Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2));
//                if (d > TOTAL_RATIO) break;
                for (int size = paths.size(), i = 0; i < size; i++) {
                    UserPath p = paths.valueAt(i);
                    if (p.closeTo(x, y, TOTAL_RATIO)) {
                        paths.remove(i);
                        invalidate();
                    }
                }
                break;
        }
    }

    private void triangleStart(int[] c) {
        List<UserPath> list = new ArrayList<>();

        for (int aC : c) {
            list.add(paths.get(aC));
        }
        Collections.sort(list);
        int pivotIndex = list.size() / 2;
        UserPath pivot = list.get(pivotIndex);
        list.remove(pivot);

        pivot.drawTriangle(list);
    }

    private void touchMove(int pointerId, float x, float y) {
        UserPath fp = paths.get(pointerId);
        float dx = Math.abs(x - fp.x);
        float dy = Math.abs(y - fp.y);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            fp.quadTo(fp.x, fp.y, (x + fp.x) / 2, (y + fp.y) / 2);
            fp.x = x;
            fp.y = y;
        }
    }

    private void touchUp(int pointerId) {
/*        UserPath fp = paths.get(pointerId);
        fp.path.lineTo(fp.x, fp.y);*/
        if (this.viewType == ViewType.DESIGN) paths.remove(pointerId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.currentColor = Color.argb(255, this.rnd.nextInt(256), this.rnd.nextInt(256), this.rnd.nextInt(256));
        // get pointer index from the event object
        int pointerIndex = event.getActionIndex();

        // get pointer ID
        int pointerId = event.getPointerId(pointerIndex);

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        switch(maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                touchStart(pointerId, x, y);
                if (this.viewType == ViewType.VECTOR && event.getPointerCount() == 3) {
                    int[] c = new int[event.getPointerCount()];
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        c[i] = event.getPointerId(i);
                    }
                    triangleStart(c);
                }
                break;
            case MotionEvent.ACTION_MOVE :
                for (int i = 0; viewType == ViewType.DESIGN && i < event.getPointerCount(); i++) {
                    touchMove(event.getPointerId(i), event.getX(i), event.getY(i));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                touchUp(pointerId);
                break;
        }

        this.gestureDetector.onTouchEvent(event);
        this.mScaleGestureDetector.onTouchEvent(event);
        invalidate();
        return true;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (viewType == ViewType.SCALAR) {

                paths.get(paths.size() - 1).drawRectangle(200);
            }
            return true;
        }

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            if (viewType == ViewType.SCALAR) {

                mScaleFactor *= scaleGestureDetector.getScaleFactor();

                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            }
            return true;
        }
    }

    //start timer function
    public void startTimer() {
        if (this.viewType == ViewType.TIMER) {
            cTimer = new CountDownTimer(30000, 100) {
                public void onTick(long millisUntilFinished) {
                    for (int i = 0; i < paths.size(); i++) {
                        UserPath fp = paths.valueAt(i);
                        fp.move(20);
                        invalidate();
                    }
                }
                public void onFinish() {
                }
            };
            cTimer.start();
        }
    }


    //cancel timer
    public void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }
}
