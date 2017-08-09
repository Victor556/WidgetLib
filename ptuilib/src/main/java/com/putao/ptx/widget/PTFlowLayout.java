package com.putao.ptx.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.putao.ptx.ptuilib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><br/>ClassName : {@link PTFlowLayout}
 * <br/>Description : 流式布局控件
 * <br/>
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-13 11:11:02</p>
 */

public class PTFlowLayout extends RadioGroup {

    /** @see PTFlowLayout#getClass().getSimpleName() */
    private static final String TAG = "xiaosw-PTFlowRadioGroup";

    ///////////////////////////////////////////////////////////////////////////
    // default values
    ///////////////////////////////////////////////////////////////////////////
    public static final int DEF_LINE_GAVITY_TOP = 0;
    public static final int DEF_LINE_GAVITY_CENTER_VERTICAL = 1;
    public static final int DEF_LINE_GAVITY_BOTTOM = 2;

    ///////////////////////////////////////////////////////////////////////////
    // attrs
    ///////////////////////////////////////////////////////////////////////////
    private int mGravity = Gravity.START | Gravity.TOP;
    private int mLineGraviey = DEF_LINE_GAVITY_TOP;

    /**
     * 存储所有的子View，按行记录
     */
    private List<List<View>> mAllChilds = new ArrayList<List<View>>();

    /**
     * 记录每一行的最大高度
     */
    private List<LineRect> mLineRect = new ArrayList<LineRect>();

    public PTFlowLayout(Context context) {
        super(context);
    }

    public PTFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PTFlowLayout);
        mGravity = ta.getInt(R.styleable.PTFlowLayout_android_gravity, mGravity);
        mLineGraviey = ta.getInt(R.styleable.PTFlowLayout_lineGravity, DEF_LINE_GAVITY_TOP);
        ta.recycle();
    }

    @Override
    public void setGravity(int gravity) {
        mGravity = gravity;
        super.setGravity(mGravity);
    }

    public void setLineGraviey(int lineGraviey) {
        if (mLineGraviey != lineGraviey) {
            mLineGraviey = lineGraviey;
            requestLayout();
        }
    }

    public int getLineGraviey() {
        return mLineGraviey;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;

        // 记录每一行的宽度，width不断取最大宽度
        int lineWidth = 0;
        // 每一行的高度，累加至height
        int lineHeight = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 得到child的lp
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
            // 当前子空间实际占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                + lp.rightMargin;
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                + lp.bottomMargin;
            // 如果加入当前child，超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
            if (lineWidth + childWidth > sizeWidth) {
                width = Math.max(lineWidth, childWidth);// 取最大的
                lineWidth = childWidth; // 重新开启新行，开始记录
                // 叠加当前高度，
                height += lineHeight;
                // 开启记录下一行的高度
                lineHeight = childHeight;
            } else {
                // 否则累加值lineWidth,lineHeight取最大高度
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            // 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
            if (i == childCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
            : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
            : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        calculateChilds();
        onLayoutChild();
    }

    /**
     * 遍历所有的子view,计算相关信息
     */
    private void calculateChilds() {
        mAllChilds.clear();
        mLineRect.clear();
        int width = getWidth();
        LineRect lineRect = new LineRect();
        // 存储每一行所有的childView
        List<View> lineChilds = new ArrayList<View>();
        int childCount = getChildCount();
        // 遍历所有的子view,计算相关信息
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            // 如果已经需要换行
            if (childWidth + lineRect.width > width) {
                // 记录这一行所有的View以及最大高度
                mLineRect.add(lineRect);
                // 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
                mAllChilds.add(lineChilds);
                lineRect = new LineRect();// 重置行宽
                lineChilds = new ArrayList<View>();
            }
            /**
             * 如果不需要换行，则累加
             */
            lineRect.width += childWidth;
            lineRect.height = Math.max(lineRect.height, childHeight);
            lineChilds.add(child);
        }
        // 记录最后一行
        mLineRect.add(lineRect);
        mAllChilds.add(lineChilds);
    }

    /**
     * 摆放子view
     */
    private void onLayoutChild() {
        int top = calculateTopWithGravity();
        int lineNums = mAllChilds.size();
        for (int i = 0; i < lineNums; i++) {
            // 每一行的所有的views
            List<View> lineViews = mAllChilds.get(i);
            // 当前行的最大高度
            LineRect lineRect = mLineRect.get(i);
            int left = calculateLeftWithGravity(lineRect);
            for (View child : lineViews) {
                if (child.getVisibility() == GONE) {
                    continue;
                }
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
                //计算childView的left,top,right,bottom
                int l = left + lp.leftMargin;
                int t = top + calculateChildTopWithLineGravity(lineRect, child);
                int r = l + child.getMeasuredWidth();
                int b = t + child.getMeasuredHeight();
                child.layout(l, t, r, b);
                left += (lp.leftMargin + child.getMeasuredWidth() + lp.rightMargin);
            }
            top += lineRect.height;
        }
    }

    /**
     * 根据行高及单行居中模式子view排放位置
     * @param lineRect 当前行相关信息
     * @param child 待摆放的view
     * @return
     */
    private int calculateChildTopWithLineGravity(LineRect lineRect, View child) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
        int childTop = lp.topMargin;
        // 如果控件高度等于当前行高度，则不需处理
        if (lineRect.height > (child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin)) {
            switch (mLineGraviey) {
                case DEF_LINE_GAVITY_CENTER_VERTICAL:
                    childTop = (lineRect.height - child.getMeasuredHeight()) >> 1;
                    break;

                case DEF_LINE_GAVITY_BOTTOM:
                    childTop = lineRect.height - child.getMeasuredHeight() - lp.bottomMargin;
                    break;
                
                default:
                    // TODO: 2017/7/13
            }
        }
        return childTop;
    }

    /**
     * 计算每行的起始位置
     * @param lineRect
     * @return
     */
    private int calculateLeftWithGravity(LineRect lineRect) {
        int left = 0;
        if (lineRect.width >= getWidth()) {
            return left;
        }
        switch (mGravity) {
            case Gravity.RIGHT:
            case Gravity.RIGHT | Gravity.TOP:
            case Gravity.RIGHT | Gravity.CENTER:
            case Gravity.RIGHT | Gravity.BOTTOM:
                left = getWidth() - lineRect.width;
                break;

            case Gravity.CENTER:
            case Gravity.CENTER_HORIZONTAL:
            case Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM:
                left = (getWidth() - lineRect.width) >> 1;
                break;
        }
        return left;
    }

    /**
     * 计算摆放初始顶点位置
     * @return
     */
    private int calculateTopWithGravity() {
        int top = 0;
        int totalHeight = 0;
        for (LineRect lineRect : mLineRect) {
            totalHeight += lineRect.height;
        }
        if (getHeight() <= totalHeight) {
            return top;
        }
        switch (mGravity) {
            case Gravity.CENTER:
            case Gravity.CENTER | Gravity.LEFT:
            case Gravity.CENTER | Gravity.RIGHT:
            case Gravity.CENTER_VERTICAL:
                top = (getHeight() - totalHeight) >> 1;
                break;

            case Gravity.BOTTOM:
            case Gravity.BOTTOM | Gravity.LEFT:
            case Gravity.BOTTOM | Gravity.CENTER:
            case Gravity.BOTTOM | Gravity.RIGHT:
                top = getHeight() - totalHeight;
                break;

            default:
                // TODO: 2017/7/13
        }
        return top;
    }

    private class LineRect {
        int width;
        int height;
    }
}
