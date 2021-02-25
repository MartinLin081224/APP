package com.example.a10609516.app.Tools;

/**
 * Created by 10609516 on 2018/2/22.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SignView extends View {

    private Paint paint;
    private Canvas cacheCanvas;
    private Bitmap cachebBitmap;
    private Path path;
    static final int BACKGROUND_COLOR = Color.WHITE;
    static final int BRUSH_COLOR = Color.BLACK;
    private float cur_x, cur_y;
    isSignListener isListener;

    public SignView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // initView(context);
        // TODO Auto-generated constructor stub
    }

    public SignView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // initView(context);
        // TODO Auto-generated constructor stub
    }

    public SignView(Context context) {
        super(context);
        // initView(context);
        // TODO Auto-generated constructor stub
    }

    public void initView(Context context) {

    }

    /**
     * 設置畫筆、Bitmap、Canvas
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(0,71,171));
        path = new Path();
        cachebBitmap = Bitmap.createBitmap(
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec), Config.ARGB_8888);
        cachebBitmap.eraseColor(0);
        cacheCanvas = new Canvas(cachebBitmap);
        cacheCanvas.drawColor(Color.TRANSPARENT);
    }

    /**
     * 取得Bitmap
     * @return
     */
    public Bitmap getCachebBitmap() {
        return cachebBitmap;
    }

    /**
     * 清空Bitmap並再生成畫筆、Bitmap、Canvas
     */
    public void clear() {
        if (cacheCanvas != null) {
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            cacheCanvas.drawPaint(paint);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(6);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.rgb(0,71,171));
            invalidate();
        }
    }

    /**
     * Bitmap設定
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(BRUSH_COLOR);
        canvas.drawBitmap(cachebBitmap, 0, 0, null);
        canvas.drawPath(path, paint);
    }

    /**
     * Bitmap大小
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
        int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
        if (curW >= w && curH >= h) {
            return;
        }

        if (curW < w)
            curW = w;
        if (curH < h)
            curH = h;

        Bitmap newBitmap = Bitmap.createBitmap(curW, curH,
                Config.ARGB_8888);
        Canvas newCanvas = new Canvas();
        newCanvas.setBitmap(newBitmap);
        if (cachebBitmap != null) {
            newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
        }
        cachebBitmap = newBitmap;
        cacheCanvas = newCanvas;
    }

    /**
     * 畫筆事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if(isListener!=null){
                    isListener.sign();
                }
                cur_x = x;
                cur_y = y;
                path.moveTo(cur_x, cur_y);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                path.quadTo(cur_x, cur_y, x, y);
                cur_x = x;
                cur_y = y;
                break;
            }

            case MotionEvent.ACTION_UP: {
                cacheCanvas.drawPath(path, paint);
                path.reset();
                break;
            }
        }

        invalidate();

        return true;
    }

    /**
     * Sign簽名
     */
    public interface isSignListener{
        void sign();
    }

    /**
     * 監聽器
     * @param isListener
     */
    public void setIsListener(isSignListener isListener) {
        this.isListener = isListener;
    }

}
