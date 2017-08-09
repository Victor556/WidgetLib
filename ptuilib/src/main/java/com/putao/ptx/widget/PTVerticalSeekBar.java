/**
 * @Project : XToolLib-1.0.0
 */

package com.putao.ptx.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @ClassName : {@link PTVerticalSeekBar}
 * @Description: 竖直SeekBar
 * onSizeChanged 交换宽高的值
 * onMeasure 交换宽高的值
 * onDraw 旋转SeekBar方向
 * onTouchEvent 根据Y坐标设置Progress
 * @date 2016-1-11上午11:19:06
 * @Author xiaoshiwang <xiaoshiwang@rytong.com>
 */
public class PTVerticalSeekBar extends AppCompatSeekBar {

    public PTVerticalSeekBar(Context context) {
        this(context, null);
    }

    public PTVerticalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public PTVerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //将SeekBar转转90度
        canvas.rotate(-90);
        //将旋转后的视图移动回来
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;
        }
        return true;
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(l);
    }
}