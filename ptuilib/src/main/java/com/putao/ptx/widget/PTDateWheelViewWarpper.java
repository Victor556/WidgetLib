package com.putao.ptx.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.putao.ptx.ptuilib.R;
import com.putao.ptx.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p><br/>ClassName : {@link PTDateWheelViewWarpper}
 * <br/>Description : 时间选择控件
 * <br/>
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-20 17:17:23</p>
 */

public class PTDateWheelViewWarpper extends LinearLayout implements PTWheelView.PTOnWheelViewScrollListener,
    PTWheelView.PTOnWheelViewChangedListener {

    /** @see PTDateWheelViewWarpper#getClass().getSimpleName() */
    private static final String TAG = "DatePickerWarpper";

    ///////////////////////////////////////////////////////////////////////////
    // const filed
    ///////////////////////////////////////////////////////////////////////////
    /** 最小支持年份 */
    public static final int MIN_START_YEAR = 1900;
    /** 最大支持年份 */
    public static final int MAX_YEAR = 2099;
    /** 默认年月日，时分秒单位 */
    public static final String DEF_UNIT = "";

    public static final int DEFUALT_NORMAL_TEXT_SIZE_SP = 10;
    public static final int DEFUALT_SELECTED_TEXT_SIZE_SP = 14;
    public static final int DEFAULT_VISIBLE_ITEMS = 5;

    ///////////////////////////////////////////////////////////////////////////
    // attrs
    ///////////////////////////////////////////////////////////////////////////
    private int mMaxYear; // 最大年
    private int mNormalTextSize; // 默认文字大小
    private int mSelectedTextSize; // 选中文字大小
    private boolean is24HourFormat;
    private int mVisibleItems;
    private boolean isChildUseWeight;
    private boolean disableAmOrPm;
    /** 是否使用单位 */
    private boolean useUnit;

    ///////////////////////////////////////////////////////////////////////////
    // filed
    ///////////////////////////////////////////////////////////////////////////
    private Map<Integer, DateDataBean> mDateDatas;
    private Calendar mCalendar;
    private OnDateWheelDataChangeListener mOnDateWheelDataChangeListener;

    public PTDateWheelViewWarpper(Context context) {
        this(context, null);
    }

    public PTDateWheelViewWarpper(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PTDateWheelViewWarpper(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PTDateWheelViewWarpper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    ///////////////////////////////////////////////////////////////////////////
    // override method
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onScrollingStarted(PTWheelView wheel) {

    }

    @Override
    public void onScrollingFinished(PTWheelView wheel) {
        int currentItem = wheel.getCurrentItem();
        int tag = (int) wheel.getTag();
        DateDataBean dateData = mDateDatas.get(tag);
        dateData.mCurrentItem = currentItem;
        dateData.mDateAdapter.updateTextStyle(currentItem);
        refreshDayIfNeeded(tag);
    }

    private void refreshDayIfNeeded(int tag) {
        if (tag == Calendar.MONTH
            || tag == Calendar.YEAR) {
            calculateDays();
            DateDataBean day = mDateDatas.get(Calendar.DAY_OF_MONTH);
            day.mCurrentValue = Math.min(getMaxDayOfMouthAndYear(), day.mCurrentValue);
            day.mCurrentItem = day.mCurrentValue - 1;
            updateAdapterWithDate(day);
        }
    }

    @Override
    public void onChanged(PTWheelView wheel, int oldValue, int newValue) {
        final PTAbstractWheelTextAdapter adapter = (PTAbstractWheelTextAdapter) wheel.getPTWheelViewAdapter();
        String day = adapter.updateTextStyle(newValue);
        if (!TextUtils.isEmpty(day)) {
            int tag = (int)wheel.getTag();
            DateDataBean dateDataBean = mDateDatas.get(tag);
            if (tag == Calendar.AM_PM) {
                if (day.trim().equals(getResources().getString(R.string.str_am))) {
                    dateDataBean.mCurrentValue = Calendar.AM;
                } else if (day.trim().equals(getResources().getString(R.string.str_pm))) {
                    dateDataBean.mCurrentValue = Calendar.PM;
                } else {
                    dateDataBean.mCurrentValue = Calendar.AM_PM;
                }
            } else {
                if (useUnit) {
                    day = day.trim().substring(0, day.length() - dateDataBean.mUnit.length());
                }
                dateDataBean.mCurrentValue = Integer.parseInt(day);
            }
            dateDataBean.mCurrentItem = wheel.getCurrentItem();
        }
        notifiDateChange(wheel);
    }

    private void notifiDateChange(PTWheelView wheelView) {
        if (null != mOnDateWheelDataChangeListener) {
            mOnDateWheelDataChangeListener.onDateChange(
                wheelView,
                mDateDatas.get(Calendar.YEAR).mCurrentValue,
                mDateDatas.get(Calendar.MONTH).mCurrentValue,
                mDateDatas.get(Calendar.DAY_OF_MONTH).mCurrentValue,
                mDateDatas.get(Calendar.AM_PM).mCurrentValue,
                mDateDatas.get(Calendar.HOUR).mCurrentValue,
                mDateDatas.get(Calendar.MINUTE).mCurrentValue,
                mDateDatas.get(Calendar.SECOND).mCurrentValue);
        }
    }

    private void init(AttributeSet attrs) {
        initData();
        parseAttrs(attrs);
        initView();
    }

    private void initData() {
        mCalendar = Calendar.getInstance();
        mDateDatas = new HashMap<>();
        buildDateDataBeanWithTag(Calendar.YEAR);
        buildDateDataBeanWithTag(Calendar.MONTH);
        buildDateDataBeanWithTag(Calendar.DAY_OF_MONTH);
        buildDateDataBeanWithTag(Calendar.AM_PM);
        buildDateDataBeanWithTag(Calendar.HOUR);
        buildDateDataBeanWithTag(Calendar.MINUTE);
        buildDateDataBeanWithTag(Calendar.SECOND);
    }

    private DateDataBean buildDateDataBeanWithTag(int tag) {
        DateDataBean dateDataBean = new DateDataBean();
        dateDataBean.mPTWheelView = new PTWheelView(getContext());
        dateDataBean.mPTWheelView.setTag(tag);
        mDateDatas.put(tag, dateDataBean);
        return dateDataBean;
    }

    private void parseAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PTDateWheelViewWarpper);

        mVisibleItems = Math.max(ta.getInt(R.styleable.PTDateWheelViewWarpper_visibleItems, DEFAULT_VISIBLE_ITEMS), 1);
        isChildUseWeight = ta.getBoolean(R.styleable.PTDateWheelViewWarpper_childUseWeight, true);
        is24HourFormat = ta.getBoolean(R.styleable.PTDateWheelViewWarpper_is24HourFormat, DateFormat.is24HourFormat(getContext()));
        disableAmOrPm = ta.getBoolean(R.styleable.PTDateWheelViewWarpper_disableAmOrPm, false);
        useUnit = ta.getBoolean(R.styleable.PTDateWheelViewWarpper_useUnit, false);
        mNormalTextSize = ta.getDimensionPixelSize(R.styleable.PTDateWheelViewWarpper_normalTextSize,
            Util.sp2px(getContext(), DEFUALT_NORMAL_TEXT_SIZE_SP));
        mSelectedTextSize = ta.getDimensionPixelSize(R.styleable.PTDateWheelViewWarpper_selectedTextSize,
            Util.sp2px(getContext(), DEFUALT_SELECTED_TEXT_SIZE_SP));

        // year
        setYear(ta.getBoolean(R.styleable.PTDateWheelViewWarpper_showYear, false),
            ta.getInt(R.styleable.PTDateWheelViewWarpper_maxYear, MAX_YEAR),
            ta.getInt(R.styleable.PTDateWheelViewWarpper_currentYear, mCalendar.get(Calendar.YEAR)));

        // month
        setMonth(ta.getBoolean(R.styleable.PTDateWheelViewWarpper_showMonth, false),
            ta.getInt(R.styleable.PTDateWheelViewWarpper_currentMonth, mCalendar.get(Calendar.MONTH) +1));

        // day
        setDay(ta.getBoolean(R.styleable.PTDateWheelViewWarpper_showDay, false),
            ta.getInt(R.styleable.PTDateWheelViewWarpper_currentDay, mCalendar.get(Calendar.DAY_OF_MONTH)));

        // hour
        int currentHour;
        if (is24HourFormat) {
            currentHour = ta.getInt(R.styleable.PTDateWheelViewWarpper_currentHour, mCalendar.get(Calendar.HOUR_OF_DAY));
        } else {
            currentHour = ta.getInt(R.styleable.PTDateWheelViewWarpper_currentHour, mCalendar.get(Calendar.HOUR));
        }
        setHour(ta.getBoolean(R.styleable.PTDateWheelViewWarpper_showHour, false),
            currentHour);

        // minute
        setMinute(ta.getBoolean(R.styleable.PTDateWheelViewWarpper_showMinute, false),
            ta.getInt(R.styleable.PTDateWheelViewWarpper_currentMinute, mCalendar.get(Calendar.MINUTE)),
            ta.getInt(R.styleable.PTDateWheelViewWarpper_minuteCalibration, 1));

        // second
        setSecond(ta.getBoolean(R.styleable.PTDateWheelViewWarpper_showSecond, false),
            ta.getInt(R.styleable.PTDateWheelViewWarpper_currentSecond, mCalendar.get(Calendar.SECOND)),
            ta.getInt(R.styleable.PTDateWheelViewWarpper_secondCalibration, 1));


        setAmOrPmOrAllDay(is24HourFormat);
        ta.recycle();
    }

    /**
     * 设置年
     * @param enable
     * @param maxYear 最大年份
     * @param currentYear 当前年
     */
    private void setYear(boolean enable, int maxYear, int currentYear) {
        DateDataBean year = mDateDatas.get(Calendar.YEAR);
        mMaxYear = Math.max(MIN_START_YEAR, maxYear);
        year.mEnable = enable;
        year.mCurrentValue = Math.max(MIN_START_YEAR, Math.min(mMaxYear, currentYear));
        year.mCurrentItem = Math.min(MAX_YEAR - 1, Math.max(0, year.mCurrentValue - MIN_START_YEAR));
        year.mPTWheelView.setVisibleItems(mVisibleItems);
        calculateYears(mMaxYear);
    }

    /**
     * 设置月份
     * @param enable
     * @param currentMonth 当前月
     */
    private void setMonth(boolean enable, int currentMonth) {
        DateDataBean month = mDateDatas.get(Calendar.MONTH);
        month.mEnable = enable;
        currentMonth = Math.min(12, Math.max(1, currentMonth));
        month.mCurrentValue = currentMonth;
        month.mCurrentItem = currentMonth - 1;
        month.mPTWheelView.setVisibleItems(mVisibleItems);
        calculateMonths();
    }

    /**
     * 设置天
     * @param enable
     * @param currentDay 当前天
     */
    private void setDay(boolean enable, int currentDay) {
        DateDataBean day = mDateDatas.get(Calendar.DAY_OF_MONTH);
        day.mEnable = enable;
        currentDay = Math.min(getMaxDayOfMouthAndYear(), Math.max(1, currentDay));
        day.mCurrentValue = currentDay;
        day.mCurrentItem = currentDay - 1;
        day.mPTWheelView.setVisibleItems(mVisibleItems);
        calculateDays();
    }

    /**
     * 设置小时
     * @param enable
     * @param currentHour 当前小时
     */
    private void setHour(boolean enable, int currentHour) {
        DateDataBean hour = mDateDatas.get(Calendar.HOUR);
        hour.mEnable = enable;
        int maxHour = 23;
        if (!is24HourFormat) {
            maxHour = 11;
            currentHour %= 12;
        }
        currentHour = Math.min(maxHour, Math.max(0, currentHour));
        hour.mCurrentValue = currentHour;
        hour.mCurrentItem = currentHour;
        hour.mPTWheelView.setVisibleItems(mVisibleItems);
        calculateHours();
    }

    /**
     * 设置分钟
     * @param enable
     * @param currentMinute 当前分钟
     * @param minuteCalibration 分钟表盘刻度
     */
    private void setMinute(boolean enable, int currentMinute, int minuteCalibration) {
        DateDataBean minute = mDateDatas.get(Calendar.MINUTE);
        minute.mEnable = enable;
        currentMinute = Math.min(59, Math.max(0, currentMinute));
        minuteCalibration = Math.max(1, Math.min(59, minuteCalibration));
        if (minuteCalibration > 1) {
            int secondMod = currentMinute % minuteCalibration;
            if ((secondMod << 1) >= minuteCalibration) {
                currentMinute += (minuteCalibration - secondMod);
                if (currentMinute > 59) {
                    currentMinute -= minuteCalibration;
                }
            } else {
                currentMinute -= secondMod;
            }
        }
        minute.mCurrentValue = currentMinute;
        minute.mCurrentItem = currentMinute / minuteCalibration;
        minute.mCalibration = Math.max(1, Math.min(59, minuteCalibration));
        minute.mPTWheelView.setVisibleItems(mVisibleItems);
        calculateMinutes();
    }

    /**
     * 设置秒钟
     * @param enable
     * @param currentSecond 当前秒
     * @param secondCalibration 秒盘刻度
     */
    private void setSecond(boolean enable, int currentSecond, int secondCalibration) {
        DateDataBean second = mDateDatas.get(Calendar.SECOND);
        second.mEnable = enable;
        currentSecond = Math.min(59, Math.max(0, currentSecond));
        secondCalibration = Math.max(1, Math.min(59, secondCalibration));
        if (secondCalibration > 1) {
            int secondMod = currentSecond % secondCalibration;
            if ((secondMod << 1) >= secondCalibration) {
                currentSecond += (secondCalibration - secondMod);
                if (currentSecond > 59) {
                    currentSecond -= secondCalibration;
                }
            } else {
                currentSecond -= secondMod;
            }
        }
        second.mCurrentValue = currentSecond;
        second.mCalibration = Math.max(1, Math.min(59, secondCalibration));
        second.mCurrentItem = currentSecond / second.mCalibration;
        second.mPTWheelView.setVisibleItems(mVisibleItems);
        calculateSeconds();
    }

    private void initView() {
        removeAllViews();
        for (Map.Entry<Integer, DateDataBean> dateDataBeanEntry : mDateDatas.entrySet()) {
            DateDataBean dateDataBean = dateDataBeanEntry.getValue();
            // set adapter
            updateAdapterWithDate(dateDataBean);
        }

        // 按序添加
        addView(mDateDatas.get(Calendar.YEAR).mPTWheelView, buildLayoutParams());
        addView(mDateDatas.get(Calendar.MONTH).mPTWheelView, buildLayoutParams());
        addView(mDateDatas.get(Calendar.DAY_OF_MONTH).mPTWheelView, buildLayoutParams());
        addView(mDateDatas.get(Calendar.AM_PM).mPTWheelView, buildLayoutParams());
        addView(mDateDatas.get(Calendar.HOUR).mPTWheelView, buildLayoutParams());
        addView(mDateDatas.get(Calendar.MINUTE).mPTWheelView, buildLayoutParams());
        addView(mDateDatas.get(Calendar.SECOND).mPTWheelView, buildLayoutParams());
    }

    private void updateAdapterWithDate(DateDataBean dateDataBean) {
        DateAdapter dateAdapter = new DateAdapter(getContext(), dateDataBean, mNormalTextSize, mSelectedTextSize);
        dateDataBean.mDateAdapter = null;
        dateDataBean.mDateAdapter = dateAdapter;
        dateDataBean.mPTWheelView.setVisibleItems(mVisibleItems);
        dateDataBean.mPTWheelView.setPTWheelViewAdapter(dateDataBean.mDateAdapter);
        dateDataBean.mPTWheelView.setCurrentItem(dateDataBean.mCurrentItem);
        dateDataBean.mPTWheelView.addWheelViewScrollingListener(this);
        dateDataBean.mPTWheelView.addWheelViewChangingListener(this);
        if (!dateDataBean.mEnable) {
            dateDataBean.mPTWheelView.setVisibility(GONE);
        }
    }

    @NonNull
    private LayoutParams buildLayoutParams() {
        LayoutParams wheelViewParams;
        setGravity(Gravity.CENTER);
        if (isChildUseWeight) {
            if (getOrientation() == LinearLayout.HORIZONTAL) {
                wheelViewParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                wheelViewParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0);
            }
            wheelViewParams.weight = 1;
        } else {
            wheelViewParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return wheelViewParams;
    }

    ///////////////////////////////////////////////////////////////////////////
    // set/getValueWithKey
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 获取当前对应时间
     * @param key {@link Calendar#YEAR} selectedYear
    *              {@link Calendar#MONTH} -> selectedMonth
    *              {@link Calendar#DAY_OF_MONTH} -> selectedDay
    *              {@link Calendar#HOUR} -> selectedHour
    *              {@link Calendar#MINUTE} -> selectedMinute
    *              {@link Calendar#SECOND} ->  selectedSecond
     * @return current selected value  if exist
     */
    public int getValueWithKey(int key) {
        if (mDateDatas.containsKey(key)) {
            return mDateDatas.get(key).mCurrentValue;
        } else {
            return -1;
        }
    }

    public OnDateWheelDataChangeListener getOnDateWheelDataChangeListener() {
        return mOnDateWheelDataChangeListener;
    }

    public void setOnDateWheelDataChangeListener(OnDateWheelDataChangeListener onDateWheelDataChangeListener) {
        mOnDateWheelDataChangeListener = onDateWheelDataChangeListener;
    }

    ///////////////////////////////////////////////////////////////////////////
    // open api
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 年分滚轮是否可见 ---未完善！！！！
     * @param key {@link Calendar#YEAR} ---> year wheel
     *              {@link Calendar#MONTH} ---> month wheel
     *              {@link Calendar#DAY_OF_MONTH} ---> day wheel
     *              {@link Calendar#HOUR} ---> hour wheel
     *              {@link Calendar#MINUTE} ---> minute wheel
     *              {@link Calendar#SECOND} ---> second wheel
     * @param enable
     */
    private boolean setWheelViewEnableWithKey(int key, boolean enable) {
        DateDataBean dateData = mDateDatas.get(key);
        if (null != dateData
            && enable != dateData.mEnable) {
            dateData.mEnable = enable;
            dateData.mPTWheelView.setCurrentItem(dateData.mCurrentItem);
            if (dateData.mEnable) {
                dateData.mPTWheelView.setVisibility(VISIBLE);
            } else {
                dateData.mPTWheelView.setVisibility(GONE);
            }
            requestLayout();
            return true;
        }
        return false;
    }

    public boolean getWheelViewEnableWithKey(int key) {
        if (mDateDatas.containsKey(key)) {
            return mDateDatas.get(key).mEnable;
        }
        return false;
    }

    /**
     * 这只当前显示
     * @param key
     * @param value 必须在包含范围内（如分钟在0~59，小时在0~23），否则无效
     * @return true设置成功，否则失败
     */
    public boolean setCurrentByWithValue(int key, int value) {
        DateDataBean dateDate = mDateDatas.get(key);
        if (null != dateDate) {
            for (int i = 0; i < dateDate.mDateDatas.size(); i++) {
                if (Integer.parseInt(dateDate.mDateDatas.get(i)) == value) {
                    dateDate.mCurrentItem = i;
                    dateDate.mCurrentValue = i + 1;
                    updateAdapterWithDate(dateDate);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isUseUnit() {
        return useUnit;
    }

    /**
     * 是否使用单位（默认为年月日时分秒为单位）
     * @param useUnit
     */
    public void setUseUnit(boolean useUnit) {
        if (useUnit == this.useUnit) {
            return;
        }
        this.useUnit = useUnit;
        for (Map.Entry<Integer, DateDataBean> entry : mDateDatas.entrySet()) {
            if (entry.getKey() == Calendar.AM_PM) {
                continue;
            }
            updateAdapterWithDate(entry.getValue());
        }

    }

    /**
     * @param calibration
     * @return
     */
    public boolean setMinuteCalibration(int calibration) {
        DateDataBean minute = mDateDatas.get(Calendar.MINUTE);
        if (calibration != minute.mCalibration) {
            setMinute(minute.mEnable, minute.mCurrentItem, calibration);
            updateAdapterWithDate(minute);
        }
        return false;
    }

    /**
     * @param calibration
     * @return
     */
    public boolean setSecondCalibration(int calibration) {
        DateDataBean second = mDateDatas.get(Calendar.SECOND);
        if (calibration != second.mCalibration) {
            setMinute(second.mEnable, second.mCurrentItem, calibration);
            updateAdapterWithDate(second);
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // calculate datas
    ///////////////////////////////////////////////////////////////////////////
    private void initOrReset(DateDataBean dateDataBean) {
        if (null == dateDataBean.mDateDatas) {
            dateDataBean.mDateDatas = new ArrayList<>();
        } else {
            dateDataBean.mDateDatas.clear();
        }
    }

    /**
     * @return 年份数据
     */
    public List<String> calculateYears(int endYear) {
        return calculateYears(endYear, getResources().getString(R.string.str_year));
    }

    /**
     *
     * @param endYear 计算年份数据
     * @param unit 年份单位，默认无单位
     * @return
     */
    private List<String> calculateYears(int endYear, String unit) {
        endYear = Math.max(MIN_START_YEAR, endYear);
        return buildDate(Calendar.YEAR, MIN_START_YEAR, endYear, unit);
    }

    /**
     * @return 月份数据
     */
    public List<String> calculateMonths() {
        return calculateMonths(getResources().getString(R.string.str_month));
    }

    /**
     *
     * @param unit 月份单位，默认无单位
     * @return
     */
    private List<String> calculateMonths(String unit) {
        return buildDate(Calendar.MONTH, 12, unit);
    }

    /**
     * @return 天数据
     */
    public List<String> calculateDays() {
        return calculateDays(getResources().getString(R.string.str_day));
    }

    /**
     *
     * @param unit 天单位，默认无单位
     * @return
     */
    private List<String> calculateDays(String unit) {
        return buildDate(Calendar.DAY_OF_MONTH, getMaxDayOfMouthAndYear(), unit);
    }

    /**
     * @return 小时数据
     */
    public List<String> calculateHours() {
        return calculateHours(getResources().getString(R.string.str_hour));
    }

    /**
     *
     * @param unit 小时单位，默认无单位
     * @return
     */
    private List<String> calculateHours(String unit) {
        int maxHour;
        if (is24HourFormat) {
            maxHour = 23;
        } else {
            maxHour = 11;
        }
        return buildDate(Calendar.HOUR, 0, maxHour, unit);
    }

    /**
     * @return 分钟数据
     */
    public List<String> calculateMinutes() {
        return calculateMinutes(getResources().getString(R.string.str_minute));
    }

    /**
     *
     * @param unit 分钟单位，默认无单位
     * @return
     */
    private List<String> calculateMinutes(String unit) {
        return buildDate(Calendar.MINUTE, 0, 59, unit);
    }

    /**
     * @return 秒钟数据
     */
    public List<String> calculateSeconds() {
        return calculateSeconds(getResources().getString(R.string.str_second));
    }

    /**
     *
     * @param unit 秒钟单位，默认无单位
     * @return
     */
    private List<String> calculateSeconds(String unit) {

        return buildDate(Calendar.SECOND, 0, 59, unit);
    }

    private List<String> buildDate(int key, int max, String unit) {
        return buildDate(key, 1, max, unit);
    }

    private List<String> buildDate(int key, int start, int max, String unit) {
        DateDataBean dateData = mDateDatas.get(key);
        initOrReset(dateData);
        if (TextUtils.isEmpty(unit)) {
            unit = DEF_UNIT;
        }
        dateData.mUnit = unit;
        for (int i = start; i <= max; i += dateData.mCalibration) {
            String num = i + "";
            if (i < 10) {
                num = "0".concat(num);
            }
            dateData.mDateDatas.add(num);
        }
        return dateData.mDateDatas;
    }

    private int getMaxDayOfMouthAndYear() {
        int maxDay = 30;
        DateDataBean year = mDateDatas.get(Calendar.YEAR);
        DateDataBean month = mDateDatas.get(Calendar.MONTH);
        switch (month.mCurrentValue) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                maxDay = 31;
                break;

            case 2:
                if ((year.mCurrentValue % 4 == 0 && year.mCurrentValue % 100 != 0)) {
                    maxDay = 29;
                } else {
                    maxDay = 28;
                }
                break;
        }
        return maxDay;
    }

    public void setAmOrPmOrAllDay(boolean allDay) {
        DateDataBean dateData = mDateDatas.get(Calendar.AM_PM);
        if (!disableAmOrPm && (mDateDatas.get(Calendar.HOUR).mEnable
            || mDateDatas.get(Calendar.MINUTE).mEnable
            || mDateDatas.get(Calendar.SECOND).mEnable)) {
            dateData.mEnable = true;
        } else {
            dateData.mEnable = false;
        }
        initOrReset(dateData);
        if (allDay) {
            dateData.mCurrentValue = Calendar.AM_PM;
            dateData.mDateDatas.add(getContext().getString(R.string.str_all_day));
            dateData.mCurrentItem = 0;
        } else {
            dateData.mDateDatas.add(getContext().getString(R.string.str_am));
            dateData.mDateDatas.add(getContext().getString(R.string.str_pm));
            int amp = mCalendar.get(Calendar.AM_PM);
            if (amp == Calendar.AM) {
                dateData.mCurrentValue = Calendar.AM;
                dateData.mCurrentItem = 0;
            } else {
                dateData.mCurrentValue = Calendar.PM;
                dateData.mCurrentItem = 1;
            }
        }
        dateData.mUnit = "";
    }


    /**
     * 滚轮适配器
     */
    private class DateAdapter extends PTAbstractWheelTextAdapter {

        /** 填充数据 */
        private DateDataBean mDateDataBean;

        public DateAdapter(Context context, DateDataBean dateDataBean,
                           int normalTextSize, int selectedTextSize) {
            super(context, R.layout.pt_item_wheel_date, NO_RESOURCE, dateDataBean.mCurrentItem,
                selectedTextSize, normalTextSize);
            this.mDateDataBean = dateDataBean;
        }

        @Override
        public int getItemsCount() {
            return mDateDataBean.mDateDatas.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            String itemValue = mDateDataBean.mDateDatas.get(index);
            if (useUnit) {
                itemValue += mDateDataBean.mUnit;
            }
            return itemValue;
        }
    }

    /**
     * Wheel填充数据
     */
    private class DateDataBean {

        /** 显示数据 */
        private List<String> mDateDatas;
        /** 是否显示 */
        private boolean mEnable = false;
        /** 滚轮控件 */
        private PTWheelView mPTWheelView;
        /** 滚轮适配器 */
        private DateAdapter mDateAdapter;
        /** 当前值 */
        private int mCurrentValue;
        private String mUnit;
        /** 当前position */
        private int mCurrentItem;
        /** 时间刻度 */
        private int mCalibration = 1;

        public DateDataBean() {
            this(new ArrayList<String>(), false);
        }

        public DateDataBean(List<String> dateDatas, boolean enable) {
            this.mDateDatas = (null == dateDatas ? new ArrayList<String>() : dateDatas);
            this.mEnable = enable;
        }
    }

    public interface OnDateWheelDataChangeListener {

        void onDateChange(PTWheelView wheelView, int year, int month, int day, int timeUnit, int hour, int minute, int second);

    }

}
