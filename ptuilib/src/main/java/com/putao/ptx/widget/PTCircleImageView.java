package com.putao.ptx.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.putao.ptx.ptuilib.R;
import com.putao.ptx.util.LogUtil;
import com.putao.ptx.util.Util;

/**
 * <p><br/>ClassName : {@link PTCircleImageView}
 * <br/>Description : 圆形ImageView
 * <br/>
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-11 18:18:35</p>
 */

public class PTCircleImageView extends AppCompatImageView {

    /** @see PTCircleImageView#getClass().getSimpleName() */
    private static final String TAG = "xiaosw-PTCircleImageView";

    ///////////////////////////////////////////////////////////////////////////
    // default vale
    ///////////////////////////////////////////////////////////////////////////
    private static final ScaleType SCALE_TYPE = ScaleType.FIT_CENTER;
    /** 圆形 */
    private static final int TYPE_CIRCLE = 0;
    /** 圆角 */
    private static final int TYPE_ROUND = 1;
    /** 默认圆角大小 */
    private static final int DEFAULT_RADIUS_DP = 12;

    ///////////////////////////////////////////////////////////////////////////
    // custom attrs
    ///////////////////////////////////////////////////////////////////////////
    private int mType;
    /** 圆角大小，仅适用于{@link #TYPE_ROUND},否则无效 */
    private float mRadius;
    /** 禁止圆角转换 default false*/
    private boolean mDisableCircleTransformation;

    ///////////////////////////////////////////////////////////////////////////
    // filed
    ///////////////////////////////////////////////////////////////////////////
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private BitmapShader mBitmapShader;
    private boolean mNeededUpdateShader;
    private Bitmap mShaderBitmap;
    private Matrix mMatrix;

    public PTCircleImageView(Context context) {
        this(context, null);
    }

    public PTCircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PTCircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMatrix = new Matrix();
        mType = TYPE_CIRCLE;
        parseAttrs(context, attrs);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PTCircleImageView);
            setType(ta.getInt(R.styleable.PTCircleImageView_type, TYPE_CIRCLE));
            setRadius(ta.getDimension(R.styleable.PTCircleImageView_android_radius, Util.dp2px(context, DEFAULT_RADIUS_DP)));
            setDisableCircleTransformation(ta.getBoolean(R.styleable.PTCircleImageView_disableCircleTransformation, false));
            ta.recycle();
        }
    }

    public int getType() {
        return mType;
    }

    /**
     * @param type Pass {@link #TYPE_CIRCLE} or {@link #TYPE_ROUND}. Default
     * value is {@link #TYPE_CIRCLE}.
     */
    public void setType(int type) {
        if (mType != type) {
            mType = type;
            requestLayout();
        }
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        if (mRadius != radius) {
            mRadius = radius;
            if (mType == TYPE_ROUND) {
                invalidate();
            }
        }
    }

    public boolean isDisableCircleTransformation() {
        return mDisableCircleTransformation;
    }

    public void setDisableCircleTransformation(boolean disableCircleTransformation) {
        mDisableCircleTransformation = disableCircleTransformation;
    }

    public void setNeededUpdateShader(boolean neededUpdateShader) {
        mNeededUpdateShader = neededUpdateShader;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        setNeededUpdateShader(true);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setNeededUpdateShader(true);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        setNeededUpdateShader(true);
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(@Px int start, @Px int top, @Px int end, @Px int bottom) {
        setNeededUpdateShader(true);
        super.setPaddingRelative(start, top, end, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setNeededUpdateShader(true);
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        setNeededUpdateShader(true);
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        setNeededUpdateShader(true);
        super.setImageResource(resId);
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        setNeededUpdateShader(true);
        super.setImageURI(uri);
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType#%s not supported.", scaleType));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDisableCircleTransformation) {
            super.onDraw(canvas);
            return;
        }
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return; // couldn't resolve the URI
        }

        if (drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }
        setShader(mNeededUpdateShader);
        if (mType == TYPE_ROUND) {
            drawRoundRect(canvas);
        } else {
            drawCircle(canvas);
        }
    }

    /**
     * 绘制圆角图片
     * @param canvas
     */
    private void drawRoundRect(Canvas canvas) {
        canvas.drawRoundRect(getPaddingLeft(),
            getPaddingTop(),
            getWidth() - getPaddingRight(),
            getHeight() - getPaddingBottom(),
            mRadius, mRadius, mPaint);
    }

    /**
     * 绘制圆形图片
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        canvas.save();
        try {
            if (mWidth > mHeight) {
                canvas.translate((mWidth - mHeight) >> 1, 0);
            } else {
                canvas.translate(0, (mHeight - mWidth) >> 1);
            }
            canvas.drawCircle(getPaddingLeft() + Math.min(mWidth, mHeight) >> 1,
                getPaddingTop() + Math.min(mWidth, mHeight) >> 1,
                Math.min(mWidth, mHeight) >> 1,
                mPaint);
        } catch (Exception e) {
            LogUtil.e(TAG, "drawCircle: ", e);
        } finally {
            canvas.restore();
        }
    }

    /**
     * @param neededNew
     */
    private void setShader(boolean neededNew) {
        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }
        if (mBitmapShader == null || neededNew) {
            mShaderBitmap = Util.toBitmap(getDrawable());
            if (mShaderBitmap == null) {
                invalidate();
                return;
            }
            mBitmapShader = new BitmapShader(mShaderBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            // 保证图片尺寸与当前view大小尽量一致
            float scale;
            if(mType == TYPE_ROUND) { // 填充满
                scale = Math.max((float) mWidth / mShaderBitmap.getWidth(), (float) mHeight / mShaderBitmap.getHeight());
            } else { // 最大内切圆尺寸即可
                scale = Math.min((float) mWidth / mShaderBitmap.getWidth(), (float) mHeight / mShaderBitmap.getHeight());
            }
            // shader的变换矩阵，我们这里主要用于放大或者缩小
            mMatrix.setScale(scale, scale);
            // 设置变换矩阵
            mBitmapShader.setLocalMatrix(mMatrix);
            // 设置shader
            mPaint.setShader(null);
            mPaint.setShader(mBitmapShader);
            mNeededUpdateShader = false;
        }
    }
}
