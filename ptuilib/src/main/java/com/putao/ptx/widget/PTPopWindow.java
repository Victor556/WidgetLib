package com.putao.ptx.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;


/**
 * 自定义PopWindow类，封装了PopWindow的一些常用属性，用Builder模式支持链式调用
 *
 * @author WhiteTec 2017/7/18
 */
public class PTPopWindow {
    private static final String TAG = "PTPopWindow";
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private boolean mIsFocusable = true;
    private boolean mIsOutside = true;
    private int mResLayoutId = -1;
    private View mContentView;
    private PopupWindow mPopupWindow;
    private int mAnimationStyle = -1;

    private boolean mClippEnable = true;//default is true
    private boolean mIgnoreCheekPress = false;
    private int mInputMode = -1;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private int mSoftInputMode = -1;
    private boolean mTouchable = true;//default is ture
    private View.OnTouchListener mOnTouchListener;
    /**
     * 弹出PopWindow 背景是否变暗，默认不会变暗。
     */
    private boolean mIsBackgroundDark = false;

    private float mBackgroundDrakValue = 0;// 背景变暗的值，0 - 1
    private static final float DEFAULT_ALPHA = 0.7f;

    private Window mWindow;//当前Activity 的窗口

    private PTPopWindow(Context context) {
        mContext = context;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public PTPopWindow showAsUp(View anchor) {
        if (mPopupWindow != null) {
            //获取需要在其上方显示的控件的位置信息
            int[] location = new int[2];
            anchor.getLocationInWindow(location);
            mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] + (anchor.getWidth() - getWidth()) / 2, location[1] - mHeight);
        }
        return this;
    }

    /**
     * @param anchor
     * @param xOff
     * @param yOff
     * @return
     */
    public PTPopWindow showAsDropDown(View anchor, int xOff, int yOff) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor, xOff, yOff);
        }
        return this;
    }


    public PTPopWindow showAsDropDown(View anchor) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor, (anchor.getWidth() - getWidth()) / 2, 0);
        }
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public PTPopWindow showAsDropDown(View anchor, int xOff, int yOff, int gravity) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor, xOff, yOff, gravity);
        }
        return this;
    }


    /**
     * 相对于父控件的位置（通过设置Gravity.CENTER，下方Gravity.BOTTOM等 ），可以设置具体位置坐标
     *
     * @param parent  父控件
     * @param gravity
     * @param x       the popup's x location offset
     * @param y       the popup's y location offset
     * @return
     */
    public PTPopWindow showAtLocation(View parent, int gravity, int x, int y) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(parent, gravity, x, y);
        }
        return this;
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    /**
     * 添加一些属性设置
     *
     * @param popupWindow
     */
    private void apply(PopupWindow popupWindow) {
        popupWindow.setClippingEnabled(mClippEnable);
        if (mIgnoreCheekPress) {
            popupWindow.setIgnoreCheekPress();
        }
        if (mInputMode != -1) {
            popupWindow.setInputMethodMode(mInputMode);
        }
        if (mSoftInputMode != -1) {
            popupWindow.setSoftInputMode(mSoftInputMode);
        }
        if (mOnDismissListener != null) {
            popupWindow.setOnDismissListener(mOnDismissListener);
        }
        if (mOnTouchListener != null) {
            popupWindow.setTouchInterceptor(mOnTouchListener);
        }
        popupWindow.setTouchable(mTouchable);
    }

    private PopupWindow build() {

        if (mContentView == null) {
            mContentView = LayoutInflater.from(mContext).inflate(mResLayoutId, null);
        }

        Activity activity = (Activity) mContentView.getContext();
        if(activity!=null && mIsBackgroundDark){
            //如果设置的值在0 - 1的范围内，则用设置的值，否则用默认值
            final  float alpha = (mBackgroundDrakValue > 0 && mBackgroundDrakValue < 1) ? mBackgroundDrakValue : DEFAULT_ALPHA;
            mWindow = activity.getWindow();
            WindowManager.LayoutParams params = mWindow.getAttributes();
            params.alpha = alpha;
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mWindow.setAttributes(params);
        }

        if (mWidth != 0 && mHeight != 0) {
            mPopupWindow = new PopupWindow(mContentView, mWidth, mHeight);
        } else {
            mPopupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                    .WRAP_CONTENT);
        }
        if (mAnimationStyle != -1) {
            mPopupWindow.setAnimationStyle(mAnimationStyle);
        }

        apply(mPopupWindow);//设置一些属性

        mPopupWindow.setFocusable(mIsFocusable);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(mIsOutside);

        if (mWidth <= 0 || mHeight <= 0) {
            mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            //如果外面没有设置宽高的情况下，计算宽高并赋值
            mWidth = mPopupWindow.getContentView().getMeasuredWidth();
            mHeight = mPopupWindow.getContentView().getMeasuredHeight();
            mPopupWindow.setWidth(mWidth);
            mPopupWindow.setHeight(mHeight);
        }

        mPopupWindow.update();

        return mPopupWindow;
    }

    /**
     * 关闭popWindow
     */
    public void dissmiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    public static class PopupWindowBuilder {
        private PTPopWindow mPTPopWindow;

        public PopupWindowBuilder(Context context) {
            mPTPopWindow = new PTPopWindow(context);
        }

        public PopupWindowBuilder size(int width, int height) {
            mPTPopWindow.mWidth = width;
            mPTPopWindow.mHeight = height;
            return this;
        }


        public PopupWindowBuilder setFocusable(boolean focusable) {
            mPTPopWindow.mIsFocusable = focusable;
            return this;
        }


        public PopupWindowBuilder setView(int resLayoutId) {
            mPTPopWindow.mResLayoutId = resLayoutId;
            mPTPopWindow.mContentView = null;
            return this;
        }

        public PopupWindowBuilder setView(View view) {
            mPTPopWindow.mContentView = view;
            mPTPopWindow.mResLayoutId = -1;
            return this;
        }

        public PopupWindowBuilder setOutsideTouchable(boolean outsideTouchable) {
            mPTPopWindow.mIsOutside = outsideTouchable;
            return this;
        }

        /**
         * 设置背景变暗是否可用
         * @param isDark
         * @return
         */
        public PopupWindowBuilder enableBackgroundDark(boolean isDark){
            mPTPopWindow.mIsBackgroundDark = isDark;
            return this;
        }

        /**
         * 设置背景变暗的值
         * @param darkValue
         * @return
         */
        public PopupWindowBuilder setBgDarkAlpha(float darkValue){
            mPTPopWindow.mBackgroundDrakValue = darkValue;
            return this;
        }

        /**
         * 设置弹窗动画
         *
         * @param animationStyle
         * @return
         */
        public PopupWindowBuilder setAnimationStyle(int animationStyle) {
            mPTPopWindow.mAnimationStyle = animationStyle;
            return this;
        }


        public PopupWindowBuilder setClippingEnable(boolean enable) {
            mPTPopWindow.mClippEnable = enable;
            return this;
        }


        public PopupWindowBuilder setIgnoreCheekPress(boolean ignoreCheekPress) {
            mPTPopWindow.mIgnoreCheekPress = ignoreCheekPress;
            return this;
        }

        public PopupWindowBuilder setInputMethodMode(int mode) {
            mPTPopWindow.mInputMode = mode;
            return this;
        }

        public PopupWindowBuilder setOnDissmissListener(PopupWindow.OnDismissListener onDissmissListener) {
            mPTPopWindow.mOnDismissListener = onDissmissListener;
            return this;
        }


        public PopupWindowBuilder setSoftInputMode(int softInputMode) {
            mPTPopWindow.mSoftInputMode = softInputMode;
            return this;
        }


        public PopupWindowBuilder setTouchable(boolean touchable) {
            mPTPopWindow.mTouchable = touchable;
            return this;
        }

        public PopupWindowBuilder setTouchIntercepter(View.OnTouchListener touchIntercepter) {
            mPTPopWindow.mOnTouchListener = touchIntercepter;
            return this;
        }


        public PTPopWindow create() {
            //构建PopWindow
            mPTPopWindow.build();
            return mPTPopWindow;
        }

    }
}
