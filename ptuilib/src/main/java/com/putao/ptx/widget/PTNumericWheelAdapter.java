package com.putao.ptx.widget;

import android.content.Context;

/**
 * <p><br/>ClassName : {@link PTNumericWheelAdapter}
 * <br/>Description : 数字选择器
 * <br/>
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-07 11:11:20</p>
 */

public class PTNumericWheelAdapter extends PTAbstractWheelTextAdapter {

    /**
     * The default min value
     */
    public static final int DEFAULT_MAX_VALUE = 9;

    /**
     * The default max value
     */
    private static final int DEFAULT_MIN_VALUE = 0;

    // Values
    private int mMinValue;
    private int mMaxValue;

    // format
    private String mFormat;

    /**
     * Constructor
     *
     * @param context the current context
     */
    public PTNumericWheelAdapter(Context context) {
        this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     *
     * @param context  the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public PTNumericWheelAdapter(Context context, int minValue, int maxValue) {
        this(context, minValue, maxValue, null);
    }

    /**
     * Constructor
     *
     * @param context  the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format   the mFormat string
     */
    public PTNumericWheelAdapter(Context context, int minValue, int maxValue, String format) {
        super(context);

        this.mMinValue = minValue;
        this.mMaxValue = maxValue;
        this.mFormat = format;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = mMinValue + index;
            return mFormat != null ? String.format(mFormat, value) : Integer.toString(value);
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return mMaxValue - mMinValue + 1;
    }

}
