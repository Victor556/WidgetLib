package com.putao.ptx.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.putao.ptx.ptuilib.R;

import static com.putao.ptx.widget.PTFreshView.AnimConfig.CIRCLE_T0;
import static com.putao.ptx.widget.PTFreshView.AnimConfig.CIRCLE_T1;
import static com.putao.ptx.widget.PTFreshView.AnimConfig.CIRCLE_T2;

/**
 * Created by chenjw on 16-7-8.
 * modified by Victor 2017-7
 */
public class PTFreshView extends View {
    private Paint paint;
    private boolean forceStop = false;
    private long animStartTime;
    private RecogniseCanvasInfo canvasInfo;
    private float mWidthStroke = AnimConfig.CIRCLE_STROKE_WIDTH;
    private int mColorStroke = Color.argb(127, AnimConfig.CIRCLE_COLOR_R,
            AnimConfig.CIRCLE_COLOR_G, AnimConfig.CIRCLE_COLOR_B);
    private int mAngleStart = AnimConfig.CIRCLE_START_ANGLE;
    private int mDuration = CIRCLE_T0;
    private int mMinAlph = 50;


    public PTFreshView(Context context) {
        this(context, null);
    }

    public PTFreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PTFreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttr(attrs);
        init();
    }

    private static final String TAG = "PTFreshView";

    private void parseAttr(AttributeSet attrs) {

        if (null != attrs) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PTFreshView);
            try {
                mColorStroke = ta.getColor(R.styleable.PTFreshView_color_stroke, mColorStroke);
                mWidthStroke = ta.getDimensionPixelOffset(R.styleable.PTFreshView_width_stroke, (int) mWidthStroke);
                mAngleStart = ta.getInt(R.styleable.PTFreshView_angle_start, mAngleStart) % 360;
                mDuration = ta.getInt(R.styleable.PTFreshView_duration, mDuration);
                mMinAlph = ta.getInt(R.styleable.PTFreshView_min_alph, mMinAlph) % 255;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "parseAttr: " + e);
            } finally {

                try {
                    ta.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try {
            canvasInfo = getCurCanvasInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (paint == null) {
            paint = new Paint();
            paint.setAntiAlias(true);

            paint.setStrokeWidth(mWidthStroke/*AnimConfig.CIRCLE_STROKE_WIDTH*/);
            paint.setStyle(Paint.Style.STROKE);
        }
        paint.setColor(mColorStroke/*Color.argb(canvasInfo.alph, AnimConfig.CIRCLE_COLOR_R,
                AnimConfig.CIRCLE_COLOR_G, AnimConfig.CIRCLE_COLOR_B)*/);
        paint.setAlpha(canvasInfo.alph);
        int gap = getWidth() >> 2;
        RectF oval = new RectF(getPaddingLeft()/*gap*/, getPaddingTop()/*gap*/,
                getWidth() - getPaddingRight()/*gap*/, getHeight() - getPaddingBottom()/*gap*/);
        canvas.drawArc(oval, canvasInfo.start, canvasInfo.sweep, false, paint);

        if (forceStop) {
            animStartTime = 0;
        } else {
            postInvalidateDelayed(AnimConfig.CIRCLE_REFRESH_GAP);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void init() {

    }

    public void animStart() {
        postInvalidateDelayed(0);
        animStartTime = System.currentTimeMillis();
    }

    public void animStop() {
        forceStop = true;
    }


    private RecogniseCanvasInfo getCurCanvasInfo() {
        if (animStartTime == 0) {
            return new RecogniseCanvasInfo();
        }
        long animT = (System.currentTimeMillis() - animStartTime) % mDuration/*AnimConfig.CIRCLE_T0*/;

        float scale = (float) animT / mDuration/*AnimConfig.CIRCLE_T0*/;
        if (canvasInfo == null) {
            canvasInfo = new RecogniseCanvasInfo();
        }
        canvasInfo.alph = (int) ((255 - mMinAlph) * (1 - scale)) + mMinAlph;

        float p1;
        float t1 = CIRCLE_T1 * 1.0f / CIRCLE_T0 * mDuration;
        if (animT < t1 /*AnimConfig.CIRCLE_T1*/) {
            p1 = 0;
        } else {
            p1 = 360 * (animT - t1/*CIRCLE_T1*/) / (mDuration/*CIRCLE_T0 */ - t1/*CIRCLE_T1*/);
        }
        p1 += mAngleStart/*AnimConfig.CIRCLE_START_ANGLE*/;

        float p2;
        float t2 = CIRCLE_T2 * 1.0f / CIRCLE_T1 * mDuration;
        if (animT >= t2/*AnimConfig.CIRCLE_T2*/) {
            p2 = 360;
        } else {
            p2 = 360 * animT / t2/*CIRCLE_T2*/;
        }
        p2 += mAngleStart/*AnimConfig.CIRCLE_START_ANGLE*/;

        canvasInfo.start = p1;
        canvasInfo.sweep = p2 - p1;

        return canvasInfo;
    }

    class RecogniseCanvasInfo {
        int alph;
        float start;
        float sweep;
    }

    /**
     * Created by chenjw on 16-7-5.
     */
    public class AnimConfig {
        /**
         * 语音搜索（95f） 
         * 语音png 
         * 0f～24f    宽度100~108%  {@link #ICON_SCALE_TIME}  {@link #ICON_SCALE}
         * 24f~44f    宽度108%~100% 高度100%~108%      {@link #ICON_SCALE_TIME}    {@link #ICON_SCALE}
         * 44f～67f    高度108~100% {@link #ICON_SCALE_TIME} {@link #ICON_SCALE}
         * 波1  颜色 D8D8D8       
         * 10f出现  宽度100 高度100  {@link #WAVE_DT1}
         * 10f~60f  宽度和高度  100%~330% {@link #WAVE_DURATION}
         * 10f~60f  透明度100%~0%
         * 波2  比波1延迟15f   {@link #WAVE_DT2}
         * 波3  比波2延迟15f  {@link #WAVE_DT2}
         * 正在识别（67f)   
         * 色环
         * 0f～67f   F44336~透明度0%  {@link #CIRCLE_COLOR_R}{@link #CIRCLE_COLOR_G}{@link #CIRCLE_COLOR_B}
         * 1点 17f出现   17~67f   角度(360*)0%～100% {@link #CIRCLE_T1}
         * 2点          0f~62f   角度(360*)0%~100% {@link #CIRCLE_T2}
         */
        public static final float SCALE_DEFAULT = 1.0f;
        public static final float ICON_SCALE = 1.08f;
        public static final int ICON_SCALE_TIME = 1000 * 22 / 60;
        public static final int MAX_WAVE_N = 3;
        public static final int WAVE_DT1 = 1000 * 10 / 60;
        public static final int WAVE_DURATION = 1000 * 50 / 60;
        public static final float WAVE_SCALE = 3.3f;
        public static final int WAVE_DT2 = 1000 * 15 / 60;
        public static final int WAVE_RESTART_TIME = AnimConfig.WAVE_DT1 +
                AnimConfig.WAVE_DT2 * (AnimConfig.MAX_WAVE_N - 1) + AnimConfig.WAVE_DURATION;
        public static final int CIRCLE_REFRESH_GAP = 1000 * 1 / 60;
        public static final int CIRCLE_T0 = 1000 * 67 / 60;
        public static final int CIRCLE_T1 = 1000 * 17 / 60;
        public static final int CIRCLE_T2 = 1000 * 62 / 60;
        public static final int CIRCLE_COLOR_R = 0xF4;
        public static final int CIRCLE_COLOR_G = 0x43;
        public static final int CIRCLE_COLOR_B = 0x36;
        public static final float CIRCLE_STROKE_WIDTH = 6;
        public static final int CIRCLE_START_ANGLE = 270;
    }
}
