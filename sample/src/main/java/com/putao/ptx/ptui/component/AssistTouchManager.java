package com.putao.ptx.ptui.component;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

import com.putao.ptx.ptui.R;


/**
 * Created by liw on 2017/4/10.
 */

public class AssistTouchManager {
    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams mIconParams;
    private static WindowManager.LayoutParams mMainParams;
    public static PTIconView mIconView;
    public static MainView mMainView;
    public static int mScreenWidth;
    public static int mScreenHeight;
    public static PreferenceManager pm;
    private static Context mContext;

    /**
     * 创建悬浮图标
     */
    public static void createIconView(Context context) {
        pm = new PreferenceManager(context);
        mContext = context;
        mWindowManager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();
        if (mMainView != null) {
            removeMainView();
        }
        if (mIconView == null) {
            mIconView = new PTIconView(context);
            if (mIconParams == null) {
                mIconParams = new WindowManager.LayoutParams();
                mIconParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                mIconParams.format = PixelFormat.RGBA_8888;
                mIconParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mIconParams.gravity = Gravity.LEFT | Gravity.TOP;
                mIconParams.alpha = (float) 2 * (pm.getTouchAlpha() + 1) / 10;
                chooseSize(pm.getTouchSize());
                mIconParams.x = pm.getTouchX(mScreenWidth);
                mIconParams.y = pm.getTouchY(mScreenHeight / 2);
            }
            mIconParams.windowAnimations = R.style.icon_anim_style;
            mIconView.setParams(mIconParams);
            mWindowManager.addView(mIconView, mIconParams);
            // IconView.countDownTimer.start();
        }
    }

    public static void createMainView() {
        if (mIconView != null) {
            removeIconView();
        }
        if (mMainView == null) {
            mMainView = new MainView(mContext);
            if (mMainParams == null) {
                mMainParams = new WindowManager.LayoutParams();
                mMainParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                mMainParams.format = PixelFormat.RGBA_8888;
                mMainParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                mMainParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//                mMainParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
            mMainParams.windowAnimations = R.style.window_anim_style;
            mWindowManager.addView(mMainView, mMainParams);
        }
    }

    /**
     * 修改悬浮图标大小
     */
    public static void updateIconViewSize(int sizeId) {
        chooseSize(sizeId);
        mWindowManager.updateViewLayout(mIconView, mIconParams);
    }

    /**
     * 根据sizeId给悬浮图标设置对应大小
     */
    private static void chooseSize(int sizeId) {
        if (sizeId == 0) {
            mIconParams.width = 112;
            mIconParams.height = 112;
            mIconView.setImageResource(R.mipmap.icon_touch_small);
        } else if (sizeId == 1) {
            mIconParams.width = 160;
            mIconParams.height = 160;
            mIconView.setImageResource(R.mipmap.icon_touch);
        } else {
            mIconParams.width = 208;
            mIconParams.height = 208;
            mIconView.setImageResource(R.mipmap.icon_touch_big);
        }
    }

    /**
     * 修改悬浮窗透明度
     */
    public static void updateIconViewAlpha(int alpha) {
        mIconParams.alpha = (float) 2 * (alpha + 1) / 10;
        mWindowManager.updateViewLayout(mIconView, mIconParams);
    }


    /**
     * 移除悬浮图标
     */
    public static void removeIconView() {
        if (mIconView != null && mWindowManager != null) {
            mWindowManager.removeView(mIconView);
            mIconParams = null;
            mIconView = null;
        }
    }

    public static void removeMainView() {
        if (mMainView != null && mWindowManager != null) {
            mWindowManager.removeView(mMainView);
            mMainParams = null;
            mMainView = null;
        }
    }
}
