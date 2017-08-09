package com.putao.ptx.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.putao.ptx.ptuilib.R;
import com.putao.ptx.util.LogUtil;
import com.putao.ptx.util.Util;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.LinkedList;
/**
 * <p><br/>ClassName : {@link PTFlowBanner}
 * <br/>Description : 广告轮播
 * <br/>　
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-06 16:12:14</p>
 */
public class PTFlowBanner extends AdapterView<Adapter> {

    /** @see PTFlowBanner#getClass().getSimpleName() */
    private static final String TAG = "PTFlowBanner";

    private static final int SNAP_VELOCITY = 1000;
    private static final int INVALID_SCREEN = -1;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static final int DEFAULT_OFFSCREEN_PAGES = 3;
    private static final int DEFAULT_BANNER_ITEM_WIDTH_DP = 528;
    private static final int DEFAUTL_AUTO_SKIP_DURATION = 5000;

    private LinkedList<View> mLoadedViews;
    private LinkedList<View> mRecycledViews;
    private int mCurrentBufferIndex;
    private int mCurrentAdapterIndex;
    private int mOffscreenPageLimit = DEFAULT_OFFSCREEN_PAGES;
    private float mBannerItemWidth;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchState = TOUCH_STATE_REST;
    private float mLastMotionX;
    private int mTouchSlop;
    private int mMaximumVelocity;
    private int mCurrentScreen;
    private int mNextScreen = INVALID_SCREEN;
    private boolean mFirstLayout = true;
    private ViewSwitchListener mViewSwitchListener;
    private ViewLazyInitializeListener mViewInitializeListener;
    private EnumSet<LazyInit> mLazyInit = EnumSet.allOf(LazyInit.class);
    private Adapter mAdapter;
    private int mLastScrollDirection;
    private AdapterDataSetObserver mDataSetObserver;
    private int mLastOrientation = -1;
    private boolean mLastObtainedViewWasRecycled = false;
    private boolean isTurning = false;
    private AutoSkipTask mAutoSkipTask;
    private int mAutoSkipDuration = DEFAUTL_AUTO_SKIP_DURATION;

    private OnGlobalLayoutListener orientationChangeListener = new OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            getViewTreeObserver().removeGlobalOnLayoutListener(
                    orientationChangeListener);
            setSelection(mCurrentAdapterIndex);
        }
    };

    public PTFlowBanner(Context context) {
        this(context, null);
    }

    public PTFlowBanner(Context context, int offscreenPageLimit) {
        this(context, null);
        mOffscreenPageLimit = offscreenPageLimit;
    }

    public PTFlowBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
        init();
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        mBannerItemWidth = Util.dp2px(context, DEFAULT_BANNER_ITEM_WIDTH_DP);
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.PTFlowBanner);
            mOffscreenPageLimit = Math.max(typedArray.getInt(R.styleable.PTFlowBanner_offscreen_page_limit, DEFAULT_OFFSCREEN_PAGES),
                DEFAULT_BANNER_ITEM_WIDTH_DP);
            mBannerItemWidth = typedArray.getDimension(R.styleable.PTFlowBanner_item_width, mBannerItemWidth);
            mAutoSkipDuration = typedArray.getInt(R.styleable.PTFlowBanner_android_duration, DEFAUTL_AUTO_SKIP_DURATION);
            typedArray.recycle();
        }
    }

    private void init() {
        mLoadedViews = new LinkedList();
        mRecycledViews = new LinkedList();
        mScroller = new Scroller(getContext());
        final ViewConfiguration configuration = ViewConfiguration .get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mAutoSkipTask = new AutoSkipTask(this);

        this.setClipToPadding(false);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int pading = ((int) (dm.widthPixels - mBannerItemWidth) >> 1);
        this.setPadding(pading, 0, pading, 0);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != mLastOrientation) {
            mLastOrientation = newConfig.orientation;
            getViewTreeObserver().addOnGlobalLayoutListener(orientationChangeListener);
        }
    }

    public int getViewsCount() {
        return mOffscreenPageLimit;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int childWidth = 0;
        int childHeight = 0;
        int childState = 0;

        final int widthPadding = getWidthPadding();
        final int heightPadding = getHeightPadding();

        int count = mAdapter == null ? 0 : mAdapter.getCount();
        if (count > 0) {
            final View child = obtainView(0);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();
            childState = child.getMeasuredState();
            mRecycledViews.add(child);
        }

        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                widthSize = childWidth + widthPadding;
                break;
            case MeasureSpec.AT_MOST:
                widthSize = (childWidth + widthPadding) | childState;
                break;
            case MeasureSpec.EXACTLY:
                if (widthSize < childWidth + widthPadding)
                    widthSize |= MEASURED_STATE_TOO_SMALL;
                break;
            default:
                break;
        }
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                heightSize = childHeight + heightPadding;
                break;
            case MeasureSpec.AT_MOST:
                heightSize = (childHeight + heightPadding) | (childState >> MEASURED_HEIGHT_STATE_SHIFT);
                break;
            case MeasureSpec.EXACTLY:
                if (heightSize < childHeight + heightPadding)
                    heightSize |= MEASURED_STATE_TOO_SMALL;
                break;
            default:
                break;
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = heightPadding + childHeight;
        } else {
            heightSize |= (childState & MEASURED_STATE_MASK);
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    private int getWidthPadding() {
        return getPaddingLeft() + getPaddingRight() + getHorizontalFadingEdgeLength() * 2;
    }

    public int getChildWidth() {
        return getWidth() - getWidthPadding();
    }

    private int getHeightPadding() {
        return getPaddingTop() + getPaddingBottom();
    }

    public int getChildHeight() {
        return getHeight() - getHeightPadding();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(getChildWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getChildHeight(), MeasureSpec.EXACTLY));
        }

        if (mFirstLayout) {
            mScroller.startScroll(0, 0, mCurrentScreen * getChildWidth(), 0, 0);
            mFirstLayout = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = getPaddingLeft() + getHorizontalFadingEdgeLength();

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, getPaddingTop(), childLeft + childWidth,
                        getPaddingTop() + child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        return 0.0f;
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return 0.0f;
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        // always do the fading edge
        return 1.0f;
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        // always do the fading edge
        return 1.0f;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            stopTurning();
        } else if (action == MotionEvent.ACTION_UP) {
            startTurning();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getChildCount() == 0)
            return false;
        if (mScrollView != null) {
            mScrollView.requestDisallowInterceptTouchEvent(true);
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionX = x;

                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;

                break;

            case MotionEvent.ACTION_MOVE:
                final int deltaX = (int) (mLastMotionX - x);

                boolean xMoved = Math.abs(deltaX) > mTouchSlop;

                if (xMoved) {
                    // Scroll if the user moved far enough along the X axis
                    mTouchState = TOUCH_STATE_SCROLLING;

                    if (mViewInitializeListener != null)
                        initializeView(deltaX);
                }

                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    // Scroll to follow the motion event

                    mLastMotionX = x;

                    final int scrollX = getScrollX();
                    if (deltaX < 0) {
                        if (scrollX > 0) {
                            scrollBy(Math.max(-scrollX, deltaX), 0);
                        }
                    } else if (deltaX > 0) {
                        final int availableToScroll = getChildAt(
                                getChildCount() - 1).getRight()
                                - getPaddingRight() - getHorizontalFadingEdgeLength()
                                - scrollX - getWidth();
                        if (availableToScroll > 0) {
                            scrollBy(Math.min(availableToScroll, deltaX), 0);
                        }
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();

                    if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
                        // Fling hard enough to move left
                        snapToScreen(mCurrentScreen - 1);
                    } else if (velocityX < -SNAP_VELOCITY
                            && mCurrentScreen < getChildCount() - 1) {
                        // Fling hard enough to move right
                        snapToScreen(mCurrentScreen + 1);
                    } else {
                        snapToDestination();
                    }

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }

                mTouchState = TOUCH_STATE_REST;

                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getChildCount() == 0)
            return false;
        if (mScrollView != null) {
            mScrollView.requestDisallowInterceptTouchEvent(true);
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionX = x;

                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;
                break;

            case MotionEvent.ACTION_MOVE:
                final int deltaX = (int) (mLastMotionX - x);

                boolean xMoved = Math.abs(deltaX) > mTouchSlop;

                if (xMoved) {
                    // Scroll if the user moved far enough along the X axis
                    mTouchState = TOUCH_STATE_SCROLLING;

                    if (mViewInitializeListener != null)
                        initializeView(deltaX);
                }

                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    // Scroll to follow the motion event

                    mLastMotionX = x;

                    final int scrollX = getScrollX();
                    if (deltaX < 0) {
                        if (scrollX > 0) {
                            scrollBy(Math.max(-scrollX, deltaX), 0);
                        }
                    } else if (deltaX > 0) {
                        final int availableToScroll = getChildAt(
                                getChildCount() - 1).getRight()
                                - getPaddingRight() - getHorizontalFadingEdgeLength()
                                - scrollX - getChildWidth();
                        if (availableToScroll > 0) {
                            scrollBy(Math.min(availableToScroll, deltaX), 0);
                        }
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();

                    if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
                        // Fling hard enough to move left
                        snapToScreen(mCurrentScreen - 1);
                    } else if (velocityX < -SNAP_VELOCITY
                            && mCurrentScreen < getChildCount() - 1) {
                        // Fling hard enough to move right
                        snapToScreen(mCurrentScreen + 1);
                    } else {
                        snapToDestination();
                    }

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }

                mTouchState = TOUCH_STATE_REST;
                startTurning();
                break;
            case MotionEvent.ACTION_CANCEL:
                snapToDestination();
                mTouchState = TOUCH_STATE_REST;
                break;
            default:
                break;
        }
        return true;
    }

    private void initializeView(final float direction) {
        if (direction > 0) {
            if (mLazyInit.contains(LazyInit.RIGHT)) {
                mLazyInit.remove(LazyInit.RIGHT);
                if (mCurrentBufferIndex + 1 < mLoadedViews.size())
                    mViewInitializeListener.onViewLazyInitialize(mLoadedViews.get(mCurrentBufferIndex + 1),
                            mCurrentAdapterIndex + 1);
            }
        } else {
            if (mLazyInit.contains(LazyInit.LEFT)) {
                mLazyInit.remove(LazyInit.LEFT);
                if (mCurrentBufferIndex > 0)
                    mViewInitializeListener.onViewLazyInitialize(mLoadedViews.get(mCurrentBufferIndex - 1),
                            mCurrentAdapterIndex - 1);
            }
        }
    }

    @Override
    protected void onScrollChanged(int h, int v, int oldh, int oldv) {
        super.onScrollChanged(h, v, oldh, oldv);
    }

    private void snapToDestination() {
        final int screenWidth = getChildWidth();
        final int whichScreen = (getScrollX() + (screenWidth / 2))
                / screenWidth;

        snapToScreen(whichScreen);
    }

    private void snapToScreen(int whichScreen) {
        mLastScrollDirection = whichScreen - mCurrentScreen;
        if (!mScroller.isFinished())
            return;

        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

        mNextScreen = whichScreen;

        final int newX = whichScreen * getChildWidth();
        final int delta = newX - getScrollX();
        mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else if (mNextScreen != INVALID_SCREEN) {
            mCurrentScreen = Math.max(0,
                    Math.min(mNextScreen, getChildCount() - 1));
            mNextScreen = INVALID_SCREEN;
            post(new Runnable() {
                @Override
                public void run() {
                    postViewSwitched(mLastScrollDirection);
                }
            });
        }
    }

    /**
     * Scroll to the {@link View} in the view buffer specified by the index.
     *
     * @param indexInBuffer Index of the view in the view buffer.
     */
    private void setVisibleView(int indexInBuffer, boolean uiThread) {
        mCurrentScreen = Math.max(0,
                Math.min(indexInBuffer, getChildCount() - 1));
        int dx = (mCurrentScreen * getChildWidth()) - mScroller.getCurrX();
        mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(), dx,
                0, 0);
        if (dx == 0)
            onScrollChanged(mScroller.getCurrX() + dx, mScroller.getCurrY(), mScroller.getCurrX() + dx,
                    mScroller.getCurrY());
        if (uiThread)
            invalidate();
        else
            postInvalidate();
    }

    /**
     * Set the listener that will receive notifications every time the {code
     * ViewFlow} scrolls.
     *
     * @param listener the scroll listener
     */
    public void setOnViewSwitchListener(ViewSwitchListener listener) {
        mViewSwitchListener = listener;
    }

    public void setOnViewLazyInitializeListener(ViewLazyInitializeListener listener) {
        mViewInitializeListener = listener;
    }

    public void setAutoSkipDuration(int autoSkipDuration) {
        this.mAutoSkipDuration = autoSkipDuration;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        setAdapter(adapter, 0);
    }

    public void setAdapter(Adapter adapter, int initialPosition) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mAdapter = adapter;

        if (mAdapter == null) {
            LogUtil.w(TAG, TAG + "#setAdapter() adapter = " + mAdapter);
            return;
        }
        mDataSetObserver = new AdapterDataSetObserver();
        mAdapter.registerDataSetObserver(mDataSetObserver);

        setSelection(initialPosition);
        if (mAdapter.getCount() > 1) {
            startTurning();
        }
    }

    @Override
    public View getSelectedView() {
        return (mCurrentBufferIndex < mLoadedViews.size() ? mLoadedViews
                .get(mCurrentBufferIndex) : null);
    }

    @Override
    public int getSelectedItemPosition() {
        return mCurrentAdapterIndex;
    }


    protected void recycleViews() {
        while (!mLoadedViews.isEmpty())
            recycleView(mLoadedViews.remove());
    }

    protected void recycleView(View v) {
        if (v == null)
            return;
        mRecycledViews.addFirst(v);
        detachViewFromParent(v);
    }

    protected View getRecycledView() {
        return (mRecycledViews.isEmpty() ? null : mRecycledViews.remove());
    }

    @Override
    public void setSelection(int position) {
        mNextScreen = INVALID_SCREEN;
        mScroller.forceFinished(true);
        if (mAdapter == null)
            return;

        position = Math.max(position, 0);
        position = Math.min(position, mAdapter.getCount() - 1);

        recycleViews();

        View currentView = makeAndAddView(position, true);
        mLoadedViews.addLast(currentView);

        if (mViewInitializeListener != null)
            mViewInitializeListener.onViewLazyInitialize(currentView, position);

        for (int offset = 1; mOffscreenPageLimit - offset >= 0; offset++) {
            int leftIndex = position - offset;
            int rightIndex = position + offset;
            if (leftIndex >= 0)
                mLoadedViews.addFirst(makeAndAddView(leftIndex, false));
            if (rightIndex < mAdapter.getCount())
                mLoadedViews.addLast(makeAndAddView(rightIndex, true));
        }

        mCurrentBufferIndex = mLoadedViews.indexOf(currentView);
        mCurrentAdapterIndex = position;

        requestLayout();
        setVisibleView(mCurrentBufferIndex, false);
        if (mViewSwitchListener != null) {
            mViewSwitchListener.onSwitched(currentView, mCurrentAdapterIndex);
        }
    }

    private void resetFocus() {
        logBuffer();
        recycleViews();
        removeAllViewsInLayout();
        mLazyInit.addAll(EnumSet.allOf(LazyInit.class));

        for (int i = Math.max(0, mCurrentAdapterIndex - mOffscreenPageLimit); i < Math
                .min(mAdapter.getCount(), mCurrentAdapterIndex + mOffscreenPageLimit
                        + 1); i++) {
            mLoadedViews.addLast(makeAndAddView(i, true));
            if (i == mCurrentAdapterIndex) {
                mCurrentBufferIndex = mLoadedViews.size() - 1;
                if (mViewInitializeListener != null)
                    mViewInitializeListener.onViewLazyInitialize(mLoadedViews.getLast(), mCurrentAdapterIndex);
            }
        }
        logBuffer();
        requestLayout();
    }

    private void postViewSwitched(int direction) {
        if (direction == 0)
            return;

        if (direction > 0) { // to the right
            mCurrentAdapterIndex++;
            mCurrentBufferIndex++;
            mLazyInit.remove(LazyInit.LEFT);
            mLazyInit.add(LazyInit.RIGHT);

            // Recycle view outside buffer range
            if (mCurrentAdapterIndex > mOffscreenPageLimit) {
                recycleView(mLoadedViews.removeFirst());
                mCurrentBufferIndex--;
            }

            // Add new view to buffer
            int newBufferIndex = mCurrentAdapterIndex + mOffscreenPageLimit;
            if (newBufferIndex < mAdapter.getCount())
                mLoadedViews.addLast(makeAndAddView(newBufferIndex, true));

        } else { // to the left
            mCurrentAdapterIndex--;
            mCurrentBufferIndex--;
            mLazyInit.add(LazyInit.LEFT);
            mLazyInit.remove(LazyInit.RIGHT);

            // Recycle view outside buffer range
            if (mAdapter.getCount() - 1 - mCurrentAdapterIndex > mOffscreenPageLimit) {
                recycleView(mLoadedViews.removeLast());
            }

            // Add new view to buffer
            int newBufferIndex = mCurrentAdapterIndex - mOffscreenPageLimit;
            if (newBufferIndex > -1) {
                mLoadedViews.addFirst(makeAndAddView(newBufferIndex, false));
                mCurrentBufferIndex++;
            }

        }

        requestLayout();
        setVisibleView(mCurrentBufferIndex, true);
        if (mViewSwitchListener != null) {
            mViewSwitchListener
                    .onSwitched(mLoadedViews.get(mCurrentBufferIndex),
                            mCurrentAdapterIndex);
        }
        logBuffer();
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        LayoutParams lp = child.getLayoutParams();
        final int childWidthSpec = getChildMeasureSpec(parentWidthMeasureSpec, getWidthPadding(), lp.width);
        final int childHeightSpec = getChildMeasureSpec(parentHeightMeasureSpec, getHeightPadding(), lp.height);
        child.measure(childWidthSpec, childHeightSpec);
    }

    private View setupChild(View child, boolean addToEnd, boolean recycle) {
        final LayoutParams lp = child.getLayoutParams();
        child.measure(MeasureSpec.makeMeasureSpec(getChildWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getChildHeight(), MeasureSpec.EXACTLY));
        if (recycle)
            attachViewToParent(child, (addToEnd ? -1 : 0), lp);
        else
            addViewInLayout(child, (addToEnd ? -1 : 0), lp, true);
        return child;
    }

    private View makeAndAddView(int position, boolean addToEnd) {
        View view = obtainView(position);
        return setupChild(view, addToEnd, mLastObtainedViewWasRecycled);
    }

    private View obtainView(int position) {
        View convertView = getRecycledView();
        View view = mAdapter.getView(position, convertView, this);
        if (view != convertView && convertView != null)
            mRecycledViews.add(convertView);
        mLastObtainedViewWasRecycled = (view == convertView);
        LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            view.setLayoutParams(p);
        }
        return view;
    }

    class AdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            View v = getChildAt(mCurrentBufferIndex);
            if (v != null) {
                for (int index = 0; index < mAdapter.getCount(); index++) {
                    if (v.equals(mAdapter.getItem(index))) {
                        mCurrentAdapterIndex = index;
                        break;
                    }
                }
            }
            resetFocus();
        }

        @Override
        public void onInvalidated() {
            // Not yet implemented!
        }

    }

    private void logBuffer() {
        LogUtil.d(TAG, "Size of mLoadedViews: " + mLoadedViews.size() +
            ", Size of mRecycledViews: " + mRecycledViews.size() +
            ", X: " + mScroller.getCurrX() + ", Y: " + mScroller.getCurrY());
        LogUtil.d(TAG, "IndexInAdapter: " + mCurrentAdapterIndex
                + ", IndexInBuffer: " + mCurrentBufferIndex);
    }

    private ScrollView mScrollView;


    public void setParentScollView(ScrollView srollView) {
        mScrollView = srollView;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTurning();
    }

    public void startTurning() {
        if (isTurning)
            return;
        isTurning = true;
        postDelayed(mAutoSkipTask, mAutoSkipDuration);
    }

    public void stopTurning() {
        isTurning = false;
        removeCallbacks(mAutoSkipTask);
    }

    static class AutoSkipTask implements Runnable {
        private final WeakReference<PTFlowBanner> reference;

        AutoSkipTask(PTFlowBanner viewFlow) {
            this.reference = new WeakReference(viewFlow);
        }

        @Override
        public void run() {
            PTFlowBanner viewFlow = reference.get();
            if (viewFlow != null) {
                viewFlow.removeCallbacks(viewFlow.mAutoSkipTask);
                if (viewFlow.isTurning) {
                    viewFlow.snapToScreen(viewFlow.mCurrentScreen + 1);
                    viewFlow.postDelayed(viewFlow.mAutoSkipTask, viewFlow.mAutoSkipDuration);
                }
            }
        }
    }


    enum LazyInit {

        LEFT(0),
        RIGHT(1);

        private int value;
        LazyInit(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public interface ViewSwitchListener {
        void onSwitched(View view, int position);
    }

    public interface ViewLazyInitializeListener {
        void onViewLazyInitialize(View view, int position);
    }
}
