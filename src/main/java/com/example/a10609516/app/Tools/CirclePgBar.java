package com.example.a10609516.app.Tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CirclePgBar extends View {

    private Paint mBackPaint;
    private Paint mFrontPaint;
    private TextPaint mTextPaint;
    //ProgressBar寬度
    private float mStrokeWidth = 12;
    private float mHalfStrokeWidth = mStrokeWidth / 2;
    //ProgressBar半徑大小
    private float mRadius = 120;
    private RectF mRect;
    private float mProgress;
    //目標值，想改多少就改多少
    private float mTargetProgress;
    private int mMax;
    private int mWidth;
    private int mHeight;
    private int mColor;

    View.OnClickListener click_handler;

    public CirclePgBar(Context context) {
        super(context);
        init();
    }

    public CirclePgBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CirclePgBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //完成相關参數初始化
        init();
    }

    /**
     * 完成相關参數初始化
     */
    private void init() {
        //讀條的背景色
        mBackPaint = new Paint();
        mBackPaint.setColor(Color.rgb(128, 128, 128));
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.STROKE);
        mBackPaint.setStrokeWidth(mStrokeWidth/8);

        //讀條的顏色
        mFrontPaint = new Paint();
        mFrontPaint.setColor(mColor);
        mFrontPaint.setAntiAlias(true);
        mFrontPaint.setStyle(Paint.Style.STROKE);
        mFrontPaint.setStrokeWidth(mStrokeWidth);

        //讀條的數值
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(40);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        click_handler = new OnClickListener() {
            @Override
            public void onClick(View view) {
                invalidate();
                Log.e("CirclePgBar","invalidate");
            }
        };
        this.setOnClickListener(click_handler);
    }

    /**
     * 重寫測量大小的onMeasure方法和繪製View的核心方法onDraw()
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getRealSize(widthMeasureSpec);
        mHeight = getRealSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        initRect();
        float angle = mProgress / (float) mMax * -360;
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mBackPaint);
        canvas.drawArc(mRect, -90, angle, false, mFrontPaint);
        canvas.drawText("" + mProgress, mWidth / 2 + mHalfStrokeWidth, mHeight / 2 + mHalfStrokeWidth, mTextPaint);
        if (mProgress < 0){
            mProgress = 0;
        }
        if (mProgress < mTargetProgress) {
            if((mTargetProgress - mProgress) > 4000){
                mProgress += 60;
            }else if((mTargetProgress - mProgress) > 3000){
                mProgress += 50;
            }else if((mTargetProgress - mProgress) > 2000) {
                mProgress += 40;
            }else if((mTargetProgress - mProgress) > 1000) {
                mProgress += 30;
            }else if((mTargetProgress - mProgress) > 500) {
                mProgress += 20;
            }else if((mTargetProgress - mProgress) > 200) {
                mProgress += 10;
            }else if((mTargetProgress - mProgress) > 100) {
                mProgress += 5;
            }else if((mTargetProgress - mProgress) > 50) {
                mProgress += 3;
            }else if((mTargetProgress - mProgress) > 1) {
                mProgress += 1;
            }else{
                mProgress += 0.5;
            }
        }
        invalidate();
    }

    public int getRealSize(int measureSpec) {
        int result = 1;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.UNSPECIFIED) {
            //自己計算
            result = (int) (mRadius * 2 + mStrokeWidth);
        } else {
            result = size;
        }
        return result;
    }

    private void initRect() {
        if (mRect == null) {
            mRect = new RectF();
            int viewSize = (int) (mRadius * 2);
            int left = (mWidth - viewSize) / 2;
            int top = (mHeight - viewSize) / 2;
            int right = left + viewSize;
            int bottom = top + viewSize;
            mRect.set(left, top, right, bottom);
        }
    }

    public void setmCirclePgBar(float mProgress, float mTargetProgress, int mMax, int mColor) {
        this.mProgress = mProgress;
        this.mTargetProgress = mTargetProgress;
        this.mMax = mMax;
        this.mColor = mColor;
        mFrontPaint.setColor(mColor);
        mTextPaint.setColor(mColor);
    }
}
