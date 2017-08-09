package com.putao.ptx.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;

import com.putao.ptx.ptuilib.R;
import com.putao.ptx.util.LogUtil;
import com.putao.ptx.util.Util;

/**
 * <p>package        : com.putao.ptx.widget
 * <br/>
 * <br/>Description  :
 * <br/>
 * <br/>Author       : Victor<liuhe556@126.com>
 * <br/>
 * <br/>Created date : 2017-07-11</p>
 */
public class PTButton extends android.support.v7.widget.AppCompatButton {

    private static final int DEF_TEXT_SIZE_SP = 18;

    @Override
    public void invalidate() {
        super.invalidate();
        updateStatusAndColors();
    }

    ///////////////////////////////////////////////////////////////////////////
    // default value
    ///////////////////////////////////////////////////////////////////////////
    private final int DEF_COLOR_NORMAL = getResources().getColor(R.color.clr_green, null);
    private final int DEF_COLOR_PRESS = getResources().getColor(R.color.clr_green_pt, null);
    private final int DEF_COLOR_UNABLE = getResources().getColor(R.color.clr_gray, null);
    private final int DEF_COLOR_STROKE_NORMAL = DEF_COLOR_NORMAL;
    private final int DEF_COLOR_STROKE_PRESS = DEF_COLOR_PRESS;
    private final int DEF_COLOR_TEXT_NORMAL = getResources().getColor(R.color.clr_black, null);
    private final int DEF_COLOR_TEXT_PRESS = DEF_COLOR_TEXT_NORMAL;
    private final int DEF_WIDTH_STROKE = 0;
    private final int DEF_CORNER_RADIUS = 0;
    private final boolean DEF_HALF_CIRCLE = true;

    ///////////////////////////////////////////////////////////////////////////
    // attrs
    ///////////////////////////////////////////////////////////////////////////
    private int mColorNormal = DEF_COLOR_NORMAL;
    private int mColorPress = DEF_COLOR_PRESS;
    private int mColorUnable = DEF_COLOR_UNABLE;
    private int mColorTextNormal = DEF_COLOR_TEXT_NORMAL;
    private int mColorTextPress = DEF_COLOR_TEXT_PRESS;
    private int mColorTextUnable;


    private int mColorStrokeNormal = DEF_COLOR_STROKE_NORMAL;
    private int mColorStrokePress = DEF_COLOR_STROKE_PRESS;
    private int mWidthStroke = DEF_WIDTH_STROKE;

    private int mCornerRadius = DEF_CORNER_RADIUS;
    private boolean mIsHalfCircle = DEF_HALF_CIRCLE;
    private int mheight;
    private int mwidth;
    private boolean isCustomEnable = true;
    private boolean isCustomBackgroundEnable = false;
    private boolean isCustomTextColorEnable = false;

    public PTButton(Context context) {
        this(context, null);
    }

    public PTButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PTButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        updateStatusAndColors();
    }


    private void initAttrs(Context context, AttributeSet attrs) {

        if (null != attrs) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PTButton);
            try {

                mColorNormal = (ta.getColor(R.styleable.PTButton_color_normal, mColorNormal));
                mColorPress = (ta.getColor(R.styleable.PTButton_color_press, mColorPress));
                mColorUnable = (ta.getColor(R.styleable.PTButton_color_unable, getCurrentHintTextColor()));
                if (ta.hasValue(R.styleable.PTButton_color_normal) || ta.hasValue(R.styleable.PTButton_color_press)
                        || ta.hasValue(R.styleable.PTButton_color_unable)) {
                    isCustomBackgroundEnable = true;
                } else {
                    isCustomBackgroundEnable = false;
                    if (ta.hasValue(R.styleable.PTButton_android_background)) {
                        try {
                            mColorNormal = ta.getColor(R.styleable.PTButton_android_background, mColorNormal);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("initAttrs", "" + e);
                        }
                    }
                }

                mColorTextNormal = (ta.getColor(R.styleable.PTButton_color_text_normal, mColorTextNormal));
                mColorTextPress = (ta.getColor(R.styleable.PTButton_color_text_press, mColorTextNormal));
                mColorTextUnable = (ta.getColor(R.styleable.PTButton_color_text_unable, getCurrentHintTextColor()));
                if (ta.hasValue(R.styleable.PTButton_color_text_normal) || ta.hasValue(R.styleable.PTButton_color_text_press)
                        || ta.hasValue(R.styleable.PTButton_color_text_unable)) {
                    isCustomTextColorEnable = true;
                } else {
                    isCustomTextColorEnable = false;
                }

                mColorStrokeNormal = (ta.getColor(R.styleable.PTButton_color_stroke_normal, mColorStrokeNormal));
                mColorStrokePress = (ta.getColor(R.styleable.PTButton_color_stroke_press, mColorStrokePress));
                mWidthStroke = (ta.getDimensionPixelOffset(R.styleable.PTButton_width_stroke, mWidthStroke));
                mCornerRadius = (ta.getDimensionPixelOffset(R.styleable.PTButton_radius_corner, mCornerRadius));
                if (!isCustomBackgroundEnable && (ta.hasValue(R.styleable.PTButton_color_stroke_normal)
                        || ta.hasValue(R.styleable.PTButton_color_stroke_press)
                        || ta.hasValue(R.styleable.PTButton_width_stroke)
                        || ta.hasValue(R.styleable.PTButton_radius_corner))) {
                    isCustomBackgroundEnable = true;
                }
                mIsHalfCircle = (ta.getBoolean(R.styleable.PTButton_is_half_circle, mIsHalfCircle));

                int textSize0 = (ta.getDimensionPixelSize(R.styleable.PTButton_android_textSize, Util.sp2px(getContext(), DEF_TEXT_SIZE_SP)));
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize0);

                int gravity = ta.getInt(R.styleable.PTButton_android_gravity, Gravity.CENTER);
                setGravity(gravity);
                isCustomEnable = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ta.recycle();
            }

            LogUtil.i(TAG, "initAttrs mwidth:" + mwidth + "  mheight:" + mheight);
        } else {
            isCustomEnable = false;
        }
    }


    private void updateStatusAndColors() {
        setClickable(true);
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        mheight = measuredHeight == 0 ? mheight : measuredHeight;
        mwidth = measuredWidth == 0 ? mwidth : measuredWidth;

        if (isCustomEnable && isCustomTextColorEnable) {
            ColorStateList colors = createColorStateList();
            setTextColor(colors);
        }
        if (isCustomEnable && isCustomBackgroundEnable) {
            setBackground(newSelector());
        }

        LogUtil.d(TAG, "updateStatusAndColors");
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mwidth = right - left;
        mheight = bottom - top;
        updateStatusAndColors();
    }

    private static final String TAG = "PTButton";

    @Override
    public void setTextColor(@ColorInt int color) {
        super.setTextColor(color);
        setColorTextNormal(color);
    }

    /**
     * 对TextView设置不同状态时其文字颜色。
     */
    private ColorStateList createColorStateList() {
        int normal = mColorTextNormal;
        int pressed = mColorTextPress;
        int focused = mColorTextPress;
        int unable = mColorTextUnable;
        int[] colors = new int[]{pressed, focused, normal, focused, unable, normal};
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        states[5] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    /**
     * 设置Selector。
     */
    private StateListDrawable newSelector() {
        StateListDrawable bg = new StateListDrawable();
        GradientDrawable normal = new GradientDrawable();
        GradientDrawable pressed = new GradientDrawable();
        GradientDrawable focused = new GradientDrawable();
        GradientDrawable unable = new GradientDrawable();

        normal.setColor(mColorNormal);
        pressed.setColor(mColorPress);
        focused.setColor(mColorPress);
        unable.setColor(mColorUnable);


        normal.setStroke(mWidthStroke, mColorStrokeNormal);
        pressed.setStroke(mWidthStroke, mColorStrokePress);
        focused.setStroke(mWidthStroke, mColorStrokePress);
        unable.setStroke(mWidthStroke, mColorStrokeNormal);

//        normal.setSize(mwidth, mheight);
//        pressed.setSize(mwidth, mheight);
//        focused.setSize(mwidth, mheight);
//        unable.setSize(mwidth, mheight);

        int radius = mIsHalfCircle ? Math.min(mwidth, mheight) / 2 : mCornerRadius;
        normal.setCornerRadius(radius);
        pressed.setCornerRadius(radius);
        focused.setCornerRadius(radius);
        unable.setCornerRadius(radius);

        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        // View.ENABLED_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focused);
        // View.ENABLED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        // View.FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_focused}, focused);
        // View.WINDOW_FOCUSED_STATE_SET
        bg.addState(new int[]{android.R.attr.state_window_focused}, unable);
        // View.EMPTY_STATE_SET
        bg.addState(new int[]{}, normal);
        return bg;
    }


    public int getColorNormal() {
        return mColorNormal;
    }

    public void setColorNormal(int colorNormal) {
        mColorNormal = colorNormal;
        updateStatusAndColors();
    }

    public int getColorPress() {
        return mColorPress;
    }

    public void setColorPress(int colorPress) {
        mColorPress = colorPress;
        updateStatusAndColors();
    }

    public int getColorUnable() {
        return mColorUnable;
    }

    public void setColorUnable(int colorUnable) {
        mColorUnable = colorUnable;
        updateStatusAndColors();
    }

    public int getColorTextNormal() {
        return mColorTextNormal;
    }

    public void setColorTextNormal(int colorTextNormal) {
        mColorTextNormal = colorTextNormal;
        updateStatusAndColors();
    }

    public int getColorTextPress() {
        return mColorTextPress;
    }

    public void setColorTextPress(int colorTextPress) {
        mColorTextPress = colorTextPress;
        updateStatusAndColors();
    }

    public int getColorTextUnable() {
        return mColorTextUnable;
    }

    public void setColorTextUnable(int colorTextUnable) {
        mColorTextUnable = colorTextUnable;
        updateStatusAndColors();
    }


    public int getColorStrokeNormal() {
        return mColorStrokeNormal;
    }

    public void setColorStrokeNormal(int colorStrokeNormal) {
        mColorStrokeNormal = colorStrokeNormal;
        updateStatusAndColors();
    }

    public int getColorStrokePress() {
        return mColorStrokePress;
    }

    public void setColorStrokePress(int colorStrokePress) {
        mColorStrokePress = colorStrokePress;
        updateStatusAndColors();
    }

    public int getWidthStroke() {
        return mWidthStroke;
    }

    public void setWidthStroke(int widthStroke) {
        mWidthStroke = widthStroke;
        updateStatusAndColors();
    }

    public int getCornerRadius() {
        return mCornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        mCornerRadius = cornerRadius;
        updateStatusAndColors();
    }

    public boolean isHalfCircle() {
        return mIsHalfCircle;
    }

    public void setHalfCircle(boolean halfCircle) {
        mIsHalfCircle = halfCircle;
        updateStatusAndColors();
    }

    /**
     * 自定义按钮是否生效
     *
     * @return
     */
    public boolean isCustomEnable() {
        return isCustomEnable;
    }

    /**
     * 使自定义属性生效，当通过xml引入自定义属性之后，自动使能<br></>
     * 配合{@link #setCustomBackgroundEnable(boolean)}和{@link #setCustomTextColorEnable(boolean)}使用
     *
     * @param customEnable
     */
    public void setCustomEnable(boolean customEnable) {
        isCustomEnable = customEnable;
    }

    /**
     * 背景相关的自定义属性是否生效
     *
     * @return
     */
    public boolean isCustomBackgroundEnable() {
        return isCustomBackgroundEnable;
    }

    /**
     * 使能背景相关（各状态下的圆角，色框，背景色）功能<br/>
     * 当通过xml引入背景相关自定义属性之后，自动使能<br/>
     * 注意:确保{@link #isCustomEnable}为true
     *
     * @param customBackgroundEnable
     */
    public void setCustomBackgroundEnable(boolean customBackgroundEnable) {
        isCustomBackgroundEnable = customBackgroundEnable;
    }

    /**
     * 自定义文字状态颜色是否使能
     *
     * @return
     */
    public boolean isCustomTextColorEnable() {
        return isCustomTextColorEnable;
    }

    /**
     * 使能自定义文字颜色功能<br></>
     * 当通过xml引入文字颜色相关的自定义属性之后，自动使能<br></>
     * 注意:确保{@link #isCustomEnable}为true
     *
     * @param customTextColorEnable
     */
    public void setCustomTextColorEnable(boolean customTextColorEnable) {
        isCustomTextColorEnable = customTextColorEnable;
    }
}
