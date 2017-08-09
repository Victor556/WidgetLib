package com.putao.ptx.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.putao.ptx.ptuilib.R;


/**
 * @ClassName {@link PTCheckBoxButton}
 * @Description 添加src属性
 * @Date 2016-05-14 04:51.
 * @Author xiaoshiwang.
 */
public class PTCheckBoxButton extends AppCompatCheckBox {
    /** TopDrawable宽度 */
    private int mDrawableWidth;
    /** TopDrawable高度 */
    private int mDrawableHeight;
    private Drawable mSrcDrawable;
    private Rect mSrcRect;
    public PTCheckBoxButton(Context context) {
        super(context);
    }

    public PTCheckBoxButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewDrawable);
        mDrawableWidth = ta.getInt(R.styleable.ViewDrawable_drawable_width, -1);
        mDrawableHeight = ta.getInt(R.styleable.ViewDrawable_drawable_height, -1);
        mSrcDrawable = ta.getDrawable(R.styleable.ViewDrawable_src);
        setCompoundDrawables(mSrcDrawable, null, null, null);
        mSrcDrawable = getCompoundDrawables()[0];
        ta.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (null != mSrcDrawable
                && -1 != mDrawableWidth
                && -1 != mDrawableHeight
                && widthSize > 0) {
            int left = (widthSize- mDrawableWidth) / 2  + getPaddingLeft();
            int top =  (heightSize- mDrawableHeight) / 2  + getPaddingTop();
            int right = left + mDrawableWidth;
            int bottom = top + mDrawableHeight;
            mSrcRect = new Rect(left, top, right, bottom);
            mSrcDrawable.setBounds(mSrcRect);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if (null != mSrcDrawable) {
            mSrcDrawable.draw(canvas);
        }
    }
}
