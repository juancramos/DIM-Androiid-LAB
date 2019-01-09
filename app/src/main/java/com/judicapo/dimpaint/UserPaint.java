package com.judicapo.dimpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UserPaint extends View {

    public ViewType viewType = ViewType.DESIGN;
    public static int BRUSH_SIZE = 20;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private Random rnd = new Random();
    private static final float TOUCH_TOLERANCE = 4;
    private Paint mPaint;
    private SparseArray<UserPath> paths = new SparseArray<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    public UserPaint(Context context) {
        this(context, null);
    }

    public UserPaint(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }


    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
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

            mCanvas.drawPath(fp, mPaint);

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(int pointId,float x, float y) {
        UserPath fp = new UserPath(this.currentColor, this.strokeWidth, x, y);
        fp.reset();
        fp.moveTo(x, y);
        paths.put(pointId, fp);
    }

    private void triangleStart(int[] c) {
        List<UserPath> list = new ArrayList<>();

        for (int i = 0; i < c.length; i++) {
            list.add(paths.get(c[i]));
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
        paths.remove(pointerId);
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

        invalidate();
        return true;
    }
}
