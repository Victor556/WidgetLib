package com.putao.ptx.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.putao.ptx.ptuilib.R;

import java.util.ArrayList;

/**
 * Created by liw on 2017/4/19.
 */

public class PTCustomSeekBar extends View {
    private int width;
    private int height;
    private int perWidth = 0;
    private Paint mPaint;
    private Paint mTextPaint;
    private Paint buttonPaint;
    private Bitmap thumb;
    private Bitmap spot;
    private Bitmap spotOn;
    private int curSections = 2;
    private int bitMapHeight;
    private int textMove = 40;//字与下方点的距离，因为字体字体是40px，再加上10的间隔
    private int[] colors = new int[]{0x000000, 0x1e000000};//进度条的橙色,进度条的灰色,字体的灰色
    private int textSize;
    private TouchResponse responseOnTouch;
    private ArrayList<String> sectionTitle;

    public PTCustomSeekBar(Context context) {
        super(context);
    }

    public PTCustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PTCustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        curSections = 0;
        thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon_dot);
        spot = BitmapFactory.decodeResource(getResources(), R.drawable.icon_line);
        spotOn = BitmapFactory.decodeResource(getResources(), R.drawable.icon_line);
        bitMapHeight = thumb.getHeight() / 2;
        textMove = bitMapHeight + 12;
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);//锯齿不显示
        mPaint.setStrokeWidth(3);
        mTextPaint = new Paint(Paint.DITHER_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(0x8a000000);
        buttonPaint = new Paint(Paint.DITHER_FLAG);
        buttonPaint.setAntiAlias(true);
    }

    /**
     * 实例化后调用，设置bar的段数和文字
     */
    public void initData(ArrayList<String> section) {
        if (section != null) {
            sectionTitle = section;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec) + 13;
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 62, getResources().getDisplayMetrics());
        setMeasuredDimension(width, height);
        // width = width - bitMapHeight / 2;
        perWidth = (width - sectionTitle.size() * spot.getWidth() - thumb.getWidth() / 2) / (sectionTitle.size() - 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(0);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        mPaint.setAlpha(255);
        mPaint.setColor(colors[1]);
//        canvas.drawLine(bitMapHeight, height * 2 / 3,
//                width - bitMapHeight - spotOn.getWidth() / 2, height * 2 / 3, mPaint);
        canvas.drawLine(bitMapHeight / 2, height * 2 / 3,
                width - bitMapHeight - spotOn.getWidth(), height * 2 / 3, mPaint);
        int section = 0;
        while (section < sectionTitle.size()) {
            mPaint.setAlpha(255);
            if (section == sectionTitle.size() - 1) {

                canvas.drawBitmap(spotOn, width - 22, height * 2 / 3 - spot.getHeight() / 2, mPaint);
            } else {
                canvas.drawBitmap(spot, (section) * width / (sectionTitle.size() - 1) + 5,
                        height * 2 / 3 - spot.getHeight() / 2, mPaint);
            }

            if (section == sectionTitle.size() - 1) {
                canvas.drawBitmap(spot, width - 150,
                        height * 2 - spot.getHeight(), mPaint);
                canvas.drawText(sectionTitle.get(section), width - 55 -
                        (sectionTitle.get(section).length() - 1) * 15, height * 2 / 3 - textMove, mTextPaint);
            } else if (section == 0) {
                canvas.drawText(sectionTitle.get(section), (section) * width / (sectionTitle.size() - 1),
                        height * 2 / 3 - textMove, mTextPaint);
            } else {
                canvas.drawText(sectionTitle.get(section), (section) * width / (sectionTitle.size() - 1) - 20,
                        height * 2 / 3 - textMove, mTextPaint);
            }
            section++;
        }
        if (curSections == sectionTitle.size() - 1) {
            canvas.drawBitmap(thumb, width - thumb.getWidth() - spotOn.getWidth() - bitMapHeight + 8,
                    height * 2 / 3 - bitMapHeight, buttonPaint);
        } else if (curSections == 0) {
            canvas.drawBitmap(thumb, curSections * width / (sectionTitle.size() - 1),
                    height * 2 / 3 - bitMapHeight, buttonPaint);
        } else {
            canvas.drawBitmap(thumb, curSections * width / (sectionTitle.size() - 1) - thumb.getWidth() / 2,
                    height * 2 / 3 - bitMapHeight, buttonPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int downX = (int) event.getX();
                responseTouch(downX);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                responseTouch(moveX);
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getX();
                responseTouch(upX);
                responseOnTouch.onTouchResponse(curSections);
                break;
            default:
                break;
        }
        return true;
    }

    public void responseTouch(int x) {
        if (x <= width - bitMapHeight / 2) {
            curSections = (x + perWidth / 3) / perWidth;
        } else {
            curSections = sectionTitle.size() - 1;
        }
        invalidate();
    }

    //设置监听
    public void setResponseOnTouch(TouchResponse response) {
        responseOnTouch = response;
    }

    //设置进度
    public void setProgress(int progress) {
        curSections = progress;
        invalidate();
    }

    public interface TouchResponse {
        void onTouchResponse(int volume);
    }
}