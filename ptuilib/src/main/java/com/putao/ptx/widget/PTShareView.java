package com.putao.ptx.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.putao.ptx.ptuilib.R;


/**
 * @ClassName {@link PTShareView}
 * @Description 分享按钮
 * @Date 2016-05-14 04:51.
 * @Author xiaoshiwang.
 */
public class PTShareView extends AppCompatButton {
    /** TopDrawable宽度 */
    private int mDrawableWidth;
    /** TopDrawable高度 */
    private int mDrawableHeight;
    private Drawable mTopDrawable;
    // 标记是否已计算图片尺寸信息
    private boolean isCompute;
    public PTShareView(Context context) {
        super(context);
    }

    public PTShareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewDrawable);
        mTopDrawable = ta.getDrawable(R.styleable.ViewDrawable_drawable);
        mDrawableWidth = ta.getInt(R.styleable.ViewDrawable_drawable_width, -1);
        mDrawableHeight = ta.getInt(R.styleable.ViewDrawable_drawable_height, -1);
        ta.recycle();
    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isCompute = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (null != mTopDrawable
                && -1 != mDrawableWidth
                && -1 != mDrawableHeight
                && widthSize > 0) {
            int left = (widthSize - mDrawableWidth) / 2 + getPaddingLeft();
//            int top =  getPaddingTop(); // 设置的padding只是为了占位
            int top = 0;
            int right = left + mDrawableWidth;
            int bottom = top + mDrawableHeight;
            mTopDrawable.setBounds(new Rect(left, top, right, bottom));
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != mTopDrawable) {
            mTopDrawable.draw(canvas);
        }
    }
}
