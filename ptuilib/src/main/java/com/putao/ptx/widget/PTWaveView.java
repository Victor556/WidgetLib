package com.putao.ptx.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.putao.ptx.ptuilib.R;
import com.putao.ptx.util.LogUtil;
import com.putao.ptx.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><br/>ClassName : {@link PTWaveView}
 * <br/>Description : 通过贝塞尔曲线实现水波纹控件
 * <br/>
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-10 11:11:17</p>
 */

public class PTWaveView extends View {
    
    /** @see PTWaveView#getClass().getSimpleName() */
    private static final String TAG = "xiaosw-PTWave";

    ///////////////////////////////////////////////////////////////////////////
    // default value
    ///////////////////////////////////////////////////////////////////////////
    /**  单个水波波峰长度 */
    private final int DEFAULT_WAVE_WIDTH_DP = 300;
    /** 波峰高度 */
    private final int DEFAULT_WAVE_HEIGHT_DP = 50;
    /** 总进度（用于控制水波上升） */
    private final int DEFAULT_WAVE_MAX = 100;
    /** 当前进度（用于控制水波上升） */
    private final int DEFAULT_WAVE_PROGRESS = 30;
    /** 水波颜色 */
    private final int DEFAULT_WAVE_COLOR = Color.parseColor("#FF33B5E5");
    /** 每帧移动的距离 */
    private final int DEFAULT_WAVE_SPEED = 10;
    /** 水波层数 */
    private final int DEFAULT_WAVE_LAYER_COUNT = 1;
    /** 水波从左往右移动 */
    private final int ENTRY_TYPE_WAVE_FROM_LEFT_TO_RIGHT = 0;
    /** 水波从右往左移动 */
    private final int ENTRY_TYPE_WAVE_FROM_RIGHT_TO_LEFT = 1;


    ///////////////////////////////////////////////////////////////////////////
    // attrs
    ///////////////////////////////////////////////////////////////////////////
    private int mWaveWidth;
    private int mWaveHeight;
    private int mMax;
    private int mProgress;
    private int mWaveColor;
    private int mWaveSpeed;
    private int mWaveLayerCount;
    private int mWaveEntryBy;
    private Bitmap mBitmap;

    ///////////////////////////////////////////////////////////////////////////
    // filed
    ///////////////////////////////////////////////////////////////////////////
    private List<Paint> mPaints;
    private List<Path> mPaths;
    private Paint mDefaultPaint;
    private float mOriginalY;
    private float mMoveX; // 控制移动
    private float mLayerOffsetX; // 多层wave时，水平距离
    private volatile boolean isRunningAnim;
    private AutoMoveTask mAutoMoveTask;

    private int mWidth;
    private int mHeight;

    private Region mRegionClip;
    private Region mRegionResult;

    public PTWaveView(Context context) {
        super(context);
        init(context, null);
    }

    public PTWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PTWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PTWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initPaint();
        mRegionClip = new Region();
        mRegionResult = new Region();
    }

    private void initPaint() {
        if (null == mPaints) {
            mPaints = new ArrayList<>();
        } else {
            mPaints.clear();
        }
        if (null == mPaths) {
            mPaths = new ArrayList<>();
        } else {
            mPaths.clear();
        }
        Paint mLastPaint = null;
        for (int i = 0; i < mWaveLayerCount; i++) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setColor(mWaveColor);
            if (null != mLastPaint) {
                paint.setAlpha((int) (mLastPaint.getAlpha() * 0.6f));
            } else {
                paint.setAlpha(50);
            }
            mPaints.add(paint);
            mPaths.add(new Path());
            mLastPaint = paint;
        }

        mDefaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mWaveWidth = (int) Util.dp2px(context, DEFAULT_WAVE_WIDTH_DP);
        mWaveHeight = (int)  Util.dp2px(context, DEFAULT_WAVE_HEIGHT_DP);
        mMax = DEFAULT_WAVE_MAX;
        mProgress = DEFAULT_WAVE_PROGRESS;
        mWaveSpeed = DEFAULT_WAVE_SPEED;
        mWaveLayerCount = DEFAULT_WAVE_LAYER_COUNT;
        mWaveEntryBy = ENTRY_TYPE_WAVE_FROM_LEFT_TO_RIGHT;

        if (null != attrs) {
            TypedArray ta =  context.obtainStyledAttributes(attrs, R.styleable.PTWaveView);
            setWaveWidth(ta.getDimensionPixelOffset(R.styleable.PTWaveView_waveWidth, mWaveWidth));
            setWaveHeight(ta.getDimensionPixelOffset(R.styleable.PTWaveView_waveHeight, mWaveHeight));
            setMax(ta.getInt(R.styleable.PTWaveView_android_max, DEFAULT_WAVE_MAX));
            setProgress(ta.getInt(R.styleable.PTWaveView_android_progress, DEFAULT_WAVE_PROGRESS));
            setWaveColor(ta.getColor(R.styleable.PTWaveView_waveColor, DEFAULT_WAVE_COLOR));
            setWaveSpeed(ta.getInt(R.styleable.PTWaveView_waveSpeed, DEFAULT_WAVE_SPEED));
            setWaveEntryBy(ta.getInt(R.styleable.PTWaveView_waveEnterBy, ENTRY_TYPE_WAVE_FROM_LEFT_TO_RIGHT));
            setWaveLayerCount(ta.getInt(R.styleable.PTWaveView_waveLayerCount, DEFAULT_WAVE_LAYER_COUNT));
            int iconId = ta.getResourceId(R.styleable.PTWaveView_waveIcon, -1);
            if (iconId != -1) {
                setBitmap(BitmapFactory.decodeResource(getResources(), iconId), true);
            }
            ta.recycle();
        }
    }

    public void setWaveLayerCount(int waveLayerCount) {
        if (waveLayerCount < 0) {
            throw new IllegalArgumentException("waveLayerCount must >= 1");
        }
        if (mWaveLayerCount != waveLayerCount) {
            this.mWaveLayerCount = Math.min(waveLayerCount, 5); // 最多绘制5层
            if (mWaveLayerCount > 1) {
                mLayerOffsetX = mWaveWidth / 2 / waveLayerCount;
            } else {
                mLayerOffsetX = 0;
            }
        }
        initPaint();
    }

    public void setBitmap(Bitmap bitmap) {
        setBitmap(bitmap, false);
    }

    public void setBitmap(Bitmap bitmap, boolean needTailor) {
        mBitmap = bitmap;
        if (needTailor) {
            mBitmap = Util.tailorRoundBitmap(mBitmap);
        }
    }

    public int getWaveWidth() {
        return mWaveWidth;
    }

    public void setWaveWidth(int waveWidth) {
        mWaveWidth = waveWidth;
    }

    public int getWaveHeight() {
        return mWaveHeight;
    }

    public void setWaveHeight(int waveHeight) {
        mWaveHeight = waveHeight;
    }

    public int getWaveLayerCount() {
        return mWaveLayerCount;
    }

    public void setWaveColor(int waveColor) {
        mWaveColor = waveColor;
        if (null != mPaints) {
            for (Paint paint : mPaints) {
                paint.setColor(mWaveColor);
            }
        }

    }

    public void setWaveSpeed(int waveSpeed) {
        mWaveSpeed = waveSpeed;
    }

    public int getWaveSpeed() {
        return mWaveSpeed;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public int getMax() {
        return mMax;
    }

    /**
     * 设置水波移动方向
     * @param waveEntryBy {@link #ENTRY_TYPE_WAVE_FROM_LEFT_TO_RIGHT}
     *   or {@link #ENTRY_TYPE_WAVE_FROM_RIGHT_TO_LEFT},
     *  default is {@link #ENTRY_TYPE_WAVE_FROM_LEFT_TO_RIGHT}
     */
    public void setWaveEntryBy(int waveEntryBy) {
        if (waveEntryBy != mWaveEntryBy) {
            if (waveEntryBy == ENTRY_TYPE_WAVE_FROM_RIGHT_TO_LEFT) {
                mWaveEntryBy = ENTRY_TYPE_WAVE_FROM_RIGHT_TO_LEFT;
            } else {
                mWaveEntryBy = ENTRY_TYPE_WAVE_FROM_LEFT_TO_RIGHT;
            }
        }
    }

    public void setProgress(int progress) {
        mProgress = Math.min(mMax, progress);
    }

    public int getProgress() {
        return mProgress;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(200, widthMeasureSpec);
        int height = getDefaultSize(100, heightMeasureSpec);
        setMeasuredDimension(width, height);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < mPaints.size(); i++) {
            canvas.drawPath(mPaths.get(i), mPaints.get(i));
        }
        drawIcon(canvas);
        canvas.restore();
    }

    /**
     * 绘制浮动icon
     * @param canvas
     */
    private void drawIcon(Canvas canvas) {
        if (null == mBitmap
            || mWaveLayerCount != 1) {
            return;
        }
        float x = mWidth / 2;
        mRegionClip.set((int) (x - 0.1), 0, (int) x, mHeight);
        mRegionResult.setPath(mPaths.get(0), mRegionClip);
        Rect bounds = mRegionResult.getBounds();
        canvas.drawBitmap(mBitmap,
            bounds.left - mBitmap.getWidth() / 2,
            bounds.top - mBitmap.getHeight(),
            mDefaultPaint);

    }

    /**
     * 设置曲线位置数据
     */
    private void setPathData() {
        // 使用二阶贝塞尔曲线， 一个水波包含一个波峰，一个波谷
        float halfWaveWidth = mWaveWidth / 2; // 一个波峰长度
        mOriginalY = mHeight * (1 - mProgress / (float) mMax);
        for (int i = 0; i < mPaths.size(); i++) {
            Path path = mPaths.get(i);
            path.reset();
            if (mWaveEntryBy == ENTRY_TYPE_WAVE_FROM_RIGHT_TO_LEFT) {
                path.moveTo(-mWaveWidth - mMoveX + mLayerOffsetX * i, mOriginalY);
            } else {
                path.moveTo(-mWaveWidth + mMoveX - mLayerOffsetX * i, mOriginalY);
            }

            for (int j = -mWaveWidth; j < mWidth + mWaveWidth; j += mWaveWidth) {
                path.rQuadTo(halfWaveWidth / 2, -mWaveHeight, halfWaveWidth, 0);
                path.rQuadTo(halfWaveWidth / 2, mWaveHeight, halfWaveWidth, 0);
            }

            path.lineTo(mWidth + mLayerOffsetX * i, mHeight);
            path.lineTo(0, mHeight);
            path.close();
        }
    }

    /**
     * 波纹动起来
     */
    public void startAnim() {
        if (isRunningAnim) {
            LogUtil.i(TAG, "anim already runing!!!");
            return;
        }
        if (mAutoMoveTask == null) {
            mAutoMoveTask = new AutoMoveTask();
        }
        post(mAutoMoveTask);
        isRunningAnim = true;
    }

    public void stopAnim() {
        if (null != mAutoMoveTask) {
            removeCallbacks(mAutoMoveTask);
            mAutoMoveTask = null;
        }
        isRunningAnim = false;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.moveOffsetX = mProgress;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mMoveX = ss.moveOffsetX;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.VISIBLE != visibility) {
            stopAnim();
        } else {
            startAnim();
        }
    }

    /**
     * 存储平移数据
     */
    private static class SavedState extends BaseSavedState {
        float moveOffsetX;

        /**
         * Constructor called from {@link android.widget.ProgressBar#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            moveOffsetX = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(moveOffsetX);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * 控制平移task
     */
    private class AutoMoveTask implements Runnable {

        @Override
        public void run() {
            synchronized (PTWaveView.this) {
                removeCallbacks(this);
                long start = System.currentTimeMillis();
                mMoveX += mWaveSpeed;
                if (mMoveX > mWaveWidth) {
                    mMoveX = 0;
                }
                setPathData();
                invalidate();
                long delayMillis = 16 - (System.currentTimeMillis() - start);
                postDelayed(this, Math.max(0, delayMillis));
            }
        }
    }

}
