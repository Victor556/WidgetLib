package com.putao.ptx.ptui.component;

import android.content.Context;
import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.putao.ptx.ptui.R;

import java.util.Map;
import java.util.TreeMap;


/**
 * Created by liw on 2017/4/7.
 */

public class PTIconView extends /*ImageView */android.support.v7.widget.AppCompatImageView {
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int TOP = 2;
    private static final int BOTTOM = 3;
    //图标内起始坐标
    private float startX = 0;
    private float startY = 0;
    //图标位移动的起始坐标
    private float startRawX = 0;
    private float startRawY = 0;
    //屏幕宽高
    private int width;
    private int height;
    //图标当前靠的哪个边
    private int currentDock = 1;
    //图标最终停靠的坐标，默认是屏幕右边中间位置
    private float endRawX = width;
    private float endRawY = height / 2;
    //图标位置占屏幕宽的比例
    private float floatX = 1f;
    //图标位置占屏幕高的比例
    private float floatY = 0.5f;
    // 状态栏高度
    private double stateHeight;
    WindowManager mWindowManager;
    WindowManager.LayoutParams mParams;
    public static MyCountDownTimer countDownTimer;
    CountDownTimer updateViewCountDownTimer;

    public PTIconView(Context context) {
        super(context);
        init(context);
    }

    public PTIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setParams(WindowManager.LayoutParams params) {
        if (mParams == null)
            mParams = params;
    }

    public void init(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        long millisInFuture = 5 * 1000;
        long countDownInterval = 500;
        countDownTimer = new MyCountDownTimer(millisInFuture, countDownInterval);
        setImageResource(R.mipmap.icon_touch);
        setOnTouchListener(mTouchListener);
        width = AssistTouchManager.mScreenWidth;
        height = AssistTouchManager.mScreenHeight;
        stateHeight = Math
                .ceil(25 * context.getResources().getDisplayMetrics().density);
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            float rawX = event.getRawX();
            float rawY = (float) (event.getRawY() - stateHeight);
            int sumX = (int) (rawX - startRawX);
            int sumY = (int) (event.getRawY() - startRawY);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setAlpha(1f);
                    // countDownTimer.cancel();
                    startX = mParams.width / 2;
                    startY = mParams.height / 2;
                    startRawX = event.getRawX();
                    startRawY = event.getRawY();
                    mParams.alpha = 1f;
                    mWindowManager.updateViewLayout(PTIconView.this, mParams);
                    break;
                case MotionEvent.ACTION_UP:
                    // countDownTimer.start();
                    mWindowManager.updateViewLayout(PTIconView.this, mParams);
                    endRawX = event.getRawX();
                    endRawY = event.getRawY();
                    floatX = endRawX / width;
                    floatY = endRawY / height;
                    if (sumX > -10 && sumX < 10 && sumY > -10 && sumY < 10) {
                        AssistTouchManager.createMainView();
                    } else {
                        float[] scores = {endRawX, width - endRawX, endRawY, height - endRawY};
                        currentDock = sortAndOriginalIndex(scores);
                        switchEdge();
                        startX = 0;
                        startY = 0;
                        startRawX = 0;
                        startRawY = 0;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (sumX < -10 || sumX > 10 || sumY < -10 || sumY > 10) {
                        updateIconViewPosition(rawX - startX, rawY - startY);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    /**
     *
     */
    private void switchEdge() {
        switch (currentDock) {
            case LEFT:
                updateIconViewPosition(LEFT, endRawX - mParams.width / 2, 0,
                        endRawY, endRawY - (float) (mParams.height * 0.8));
                break;
            case RIGHT:
                updateIconViewPosition(RIGHT, endRawX, width, endRawY, endRawY - (float)
                        (mParams.height * 0.8));
                break;
            case TOP:
                updateIconViewPosition(TOP, endRawX, endRawX - mParams.width / 2, endRawY, 0);
                break;
            case BOTTOM:
                updateIconViewPosition(BOTTOM, endRawX, endRawX - mParams.width / 2, endRawY, height);
                break;
            default:
                break;
        }
    }

    public class UpdateViewCountDownTimer extends CountDownTimer {
        private int type;
        private float startX;
        private float endX;
        private float startY;
        private float endY;
        private long countDownInterval;
        private long count;

        UpdateViewCountDownTimer(int type, float startX, float endX, float startY, float endY, long
                millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.type = type;
            this.startX = startX;
            this.endX = endX;
            this.startY = startY;
            this.endY = endY;
            this.countDownInterval = countDownInterval;
            this.count = millisInFuture / countDownInterval;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            switch (type) {
                case LEFT:
                    mParams.y = (int) endY;
                    mParams.x = (int) (startX - ((startX - endX) / count) * (count - millisUntilFinished /
                            countDownInterval));
                    break;
                case RIGHT:
                    mParams.y = (int) endY;
                    mParams.x = (int) (startX + ((endX - startX) / count) * (count - millisUntilFinished /
                            countDownInterval));
                    break;
                case TOP:
                    mParams.x = (int) endX;
                    mParams.y = (int) (startY - ((startY - endY) / count) * (count - millisUntilFinished /
                            countDownInterval));
                    break;
                case BOTTOM:
                    mParams.x = (int) endX;
                    mParams.y = (int) (startY + ((endY - startY) / count) * (count - millisUntilFinished /
                            countDownInterval));
                    break;
                default:
                    break;
            }
            mWindowManager.updateViewLayout(PTIconView.this, mParams);
        }

        @Override
        public void onFinish() {
            mParams.x = (int) endX;
            mParams.y = (int) endY;
            mWindowManager.updateViewLayout(PTIconView.this, mParams);
            AssistTouchManager.pm.putTouchX(mParams.x);
            AssistTouchManager.pm.putTouchY(mParams.y);
            this.cancel();
        }
    }

    /**
     * 返回最小值索引,图标应该停靠哪边
     */
    public int sortAndOriginalIndex(float[] arr) {
        int[] sortedIndex = new int[arr.length];
        TreeMap<Float, Integer> map = new TreeMap<>();
        for (int i = 0; i < arr.length; i++) {
            map.put(arr[i], i); // 将arr的“值-索引”关系存入Map集合
        }
        int n = 0;
        for (Map.Entry<Float, Integer> me : map.entrySet()) {
            sortedIndex[n++] = me.getValue();
        }
        return sortedIndex[0];
    }

    private void updateIconViewPosition(float x, float y) {
        mParams.x = (int) x;
        mParams.y = (int) y;
        mWindowManager.updateViewLayout(this, mParams);
        AssistTouchManager.pm.putTouchX(mParams.x);
        AssistTouchManager.pm.putTouchY(mParams.y);
    }

    /***
     * @param type 左：0 右：1 上：2 下：3
     */
    private void updateIconViewPosition(int type, float startX, float endX, float startY, float endY) {
        if (updateViewCountDownTimer != null) {
            updateViewCountDownTimer.cancel();
        }
        updateViewCountDownTimer = new UpdateViewCountDownTimer(type, startX, endX, startY, endY, 260, 1).start();
    }

    public class MyCountDownTimer extends CountDownTimer {

        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            setAlpha(0.4f);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        width = mWindowManager.getDefaultDisplay().getWidth();
        height = mWindowManager.getDefaultDisplay().getHeight();
        switch (currentDock) {
            case LEFT:
                updateIconViewPosition(0, height * floatY);
                break;
            case RIGHT:
                updateIconViewPosition(width, height * floatY);
                break;
            case TOP:
                updateIconViewPosition(width * floatX - 48, 0);
                break;
            case BOTTOM:
                updateIconViewPosition(width * floatX - 48, height);
                break;
            default:
                break;
        }
    }
}
