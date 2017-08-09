package com.putao.ptx.ptui

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.putao.ptx.util.LogUtil
import com.putao.ptx.widget.PTAbstractWheelTextAdapter
import com.putao.ptx.widget.PTDateWheelViewWarpper
import com.putao.ptx.widget.PTWheelView
import java.util.*
import kotlin.properties.Delegates


/**
 * <p><br/>ClassName : {@link PTWheelViewSample}
 * <br/>Description : 测试滚轮控件（实现年月日联动）
 * <br/>　
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-07 11:11:38</p>
 */
object PTWheelViewSample {

    val MIN_START_YEAR = 1900
    val DEFAULT_VISIBLE_ITEMS = 5
    val DEFUALT_NORMAL_TEXT_SIZE = 24
    val DEFUALT_SELECTED_TEXT_SIZE = 32
    val TAG = "PTWheelViewSample"

    private val mYearDatas: ArrayList<String> by lazy {
        arrayListOf<String>()
    }
    private val mMonthDatas: ArrayList<String> by lazy {
        arrayListOf<String>()
    }
    private val mDayDatas: ArrayList<String> by lazy {
        arrayListOf<String>()
    }

    private val mCalendar: Calendar = Calendar.getInstance()
    private var mCurrentYear: Int = 0
    private var mCurrentMonth: Int = 0
    private var mCurrentDay: Int = 0

    private var mYearAdapter: DateAdapter by Delegates.notNull()
    private var mMonthAdapter: DateAdapter by Delegates.notNull()
    private var mDayAdapter: DateAdapter by Delegates.notNull()

    var rootView: View by Delegates.notNull()
    var view_year: PTWheelView by Delegates.notNull()
    var view_month: PTWheelView by Delegates.notNull()
    var view_day: PTWheelView by Delegates.notNull()
    var date_wheel_view_wapper : PTDateWheelViewWarpper by Delegates.notNull()
    var tv_result: AppCompatTextView by Delegates.notNull()
    var tv_date_wheel_warpper_result: AppCompatTextView by Delegates.notNull()

    var mContext: Context by Delegates.notNull()

    fun generatePTWheelView(context: Context): View? = generateView(context, R.layout.view_wheel).also {
        LogUtil.e(TAG, "view = $it")
        it?.let {
            rootView = it
            mContext = context
            mCurrentYear = mCalendar.get(Calendar.YEAR)
            mCurrentMonth = mCalendar.get(Calendar.MONTH)
            mCurrentDay = mCalendar.get(Calendar.DAY_OF_MONTH)
            it.layoutParams = ViewGroup.LayoutParams(1500, ViewGroup.LayoutParams.WRAP_CONTENT)
            view_year = it.findViewById(R.id.view_year) as PTWheelView
            view_month = it.findViewById(R.id.view_month) as PTWheelView
            view_day = it.findViewById(R.id.view_day) as PTWheelView
            date_wheel_view_wapper = it.findViewById(R.id.date_wheel_view_warpper) as PTDateWheelViewWarpper
            tv_result = it.findViewById(R.id.tv_result) as AppCompatTextView
            tv_date_wheel_warpper_result = it.findViewById(R.id.tv_date_wheel_warpper_result) as AppCompatTextView
            updateResult()
            initData()
            setYearView()
            setMonthView()
            setDayView()

            setDateWhellViewWarpper()
        }
    }

    private fun  setDateWhellViewWarpper() {
        with(date_wheel_view_wapper) {

            tv_date_wheel_warpper_result.text = String.format("%d-%d-%d %d %d:%d:%d",
                    getValueWithKey(Calendar.YEAR),
                    getValueWithKey(Calendar.MONTH),
                    getValueWithKey(Calendar.DAY_OF_MONTH),
                    getValueWithKey(Calendar.AM_PM),
                    getValueWithKey(Calendar.HOUR),
                    getValueWithKey(Calendar.MINUTE),
                    getValueWithKey(Calendar.SECOND))

            setOnDateWheelDataChangeListener {wheelView,
                year, month, day, timeUnit, hour, minute, second ->
                tv_date_wheel_warpper_result.text = String.format("%d-%d-%d %d %d:%d:%d", year, month, day, timeUnit, hour, minute, second)
            }

            rootView.findViewById(R.id.tv_date_wheel_warpper_result).setOnClickListener {
                date_wheel_view_wapper.setMinuteCalibration(Random().nextInt(58) + 1)
            }
        }

    }

    private fun setYearView() {
        LogUtil.i(TAG, "setYearView: mCurrentYear = $mCurrentYear")
        mYearAdapter = DateAdapter(mContext, mYearDatas, mCurrentYear - MIN_START_YEAR)
        mYearAdapter.defaultTextSize = DEFUALT_NORMAL_TEXT_SIZE
        mYearAdapter.selectedTextSize = DEFUALT_SELECTED_TEXT_SIZE
        with(view_year) {
            visibleItems = DEFAULT_VISIBLE_ITEMS
            ptWheelViewAdapter = mYearAdapter
            currentItem = mCurrentYear - MIN_START_YEAR
            addWheelViewChangingListener { _, _, newValue ->
                val year = mYearAdapter.updateTextStyle(newValue)
                if (!TextUtils.isEmpty(year)) {
                    mCurrentYear = Integer.parseInt(year)
                }
                updateResult()
            }
            addWheelViewScrollingListener(object : PTWheelView.PTOnWheelViewScrollListener {
                override fun onScrollingStarted(wheel: PTWheelView) {

                }

                override fun onScrollingFinished(wheel: PTWheelView) {
                    val year = mYearAdapter.updateTextStyle(wheel.currentItem)
                    if (!TextUtils.isEmpty(year)) {
                        mCurrentYear = Integer.parseInt(year)
                    }
                    initDays(getMaxDayByMonthAndMonth(mCurrentYear, mCurrentMonth + 1))
                    setDayView()
                }
            })
        }

    }

    private fun setMonthView() {
        mMonthAdapter = DateAdapter(mContext, mMonthDatas, mCurrentMonth)
        mMonthAdapter.defaultTextSize = DEFUALT_NORMAL_TEXT_SIZE
        mMonthAdapter.selectedTextSize = DEFUALT_SELECTED_TEXT_SIZE
        with(view_month) {
            view_month.visibleItems = DEFAULT_VISIBLE_ITEMS
            ptWheelViewAdapter = mMonthAdapter
            currentItem = mCurrentMonth
            addWheelViewChangingListener { _, _, newValue ->
                val month = mMonthAdapter.updateTextStyle(newValue)
                if (!TextUtils.isEmpty(month)) {
                    mCurrentMonth = Integer.parseInt(month) - 1
                }
                updateResult()
            }

            addWheelViewScrollingListener(object : PTWheelView.PTOnWheelViewScrollListener {
                override fun onScrollingStarted(wheel: PTWheelView?) {
                }

                override fun onScrollingFinished(wheel: PTWheelView) {
                    mMonthAdapter.updateTextStyle(wheel.currentItem)
                    initDays(getMaxDayByMonthAndMonth(mCurrentYear, mCurrentMonth + 1))
                    setDayView()
                }

            })
        }
    }

    private fun setDayView() {
        mDayAdapter = DateAdapter(mContext, mDayDatas, mCurrentDay - 1)
        mDayAdapter.defaultTextSize = DEFUALT_NORMAL_TEXT_SIZE
        mDayAdapter.selectedTextSize = DEFUALT_SELECTED_TEXT_SIZE
        with(view_day) {
            visibleItems = DEFAULT_VISIBLE_ITEMS
            ptWheelViewAdapter = mDayAdapter
            currentItem = mCurrentDay - 1
            addWheelViewChangingListener { _, _, newValue ->
                val day = mDayAdapter.updateTextStyle(newValue)
                if (!TextUtils.isEmpty(day)) {
                    mCurrentDay = Integer.parseInt(day)
                }
                updateResult()
            }

            addWheelViewScrollingListener(object : PTWheelView.PTOnWheelViewScrollListener{
                override fun onScrollingStarted(wheel: PTWheelView?) {
                }

                override fun onScrollingFinished(wheel: PTWheelView) {
                    mDayAdapter.updateTextStyle(wheel.currentItem)
                }

            })
        }
    }

    private fun updateResult() {
        tv_result.text = "$mCurrentYear-${mCurrentMonth + 1}-$mCurrentDay"
    }

    /**
     * 根据当前年月获取当前月日数
     * @param year
     * *
     * @param month
     * *
     * @return
     */
    fun getMaxDayByMonthAndMonth(year: Int, month: Int): Int {
        var maxDay = 30
        var leayYear = (year % 4 == 0 && year % 100 != 0)
        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> {
                maxDay = 31
            }
            2 -> {
                 maxDay = if (leayYear) 29 else 28
            }
            4, 6, 9, 11 -> {
                maxDay = 30
            }
            else -> {
            }
        }
        return maxDay
    }

    ///////////////////////////////////////////////////////////////////////////
    // generate date
    ///////////////////////////////////////////////////////////////////////////
    private fun initData() {
        for (i in MIN_START_YEAR..2099) {
            mYearDatas.add("$i")
        }

        for (i in 1..12) {
            mMonthDatas.add("$i")
        }
        LogUtil.i(TAG, "initData: mCurrentMonth = $mCurrentMonth")
        initDays(getMaxDayByMonthAndMonth(mCurrentYear, mCurrentMonth + 1))

    }

    private fun initDays(maxDay: Int) {
        LogUtil.i(TAG, "initDays: maxDay = $maxDay")
        mDayDatas?.clear()
        for (i in 1..maxDay) {
            mDayDatas.add("$i")
        }
        mCurrentDay = Math.min(mCurrentDay, maxDay)
    }

    internal class DateAdapter : PTAbstractWheelTextAdapter {

        private val mDateDatas: List<String>

        constructor(context: Context, dateDatas: List<String>, currentItem: Int,
                    maxTextSize: Int = DEFAULT_SELECTED_TEXT_SIZE, minTextSize: Int = DEFAULT_NORMAL_TEXT_SIZE)
            : super(context, R.layout.item_wheel_date, NO_RESOURCE, currentItem, maxTextSize, minTextSize) {
            mDateDatas = dateDatas
            itemTextResource = R.id.tv_date
        }

        override fun getItemsCount(): Int {
            return mDateDatas.size
        }

        override fun getItemText(index: Int): CharSequence {
            return mDateDatas[index]
        }

    }

}