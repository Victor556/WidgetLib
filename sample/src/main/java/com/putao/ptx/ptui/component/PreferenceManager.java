package com.putao.ptx.ptui.component;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liw on 2017/4/20.
 */

public class PreferenceManager {
    private static final String FILENAME = "ptTouch";
    private SharedPreferences sp;
    private static final String TOUCH_SWITCH = "touch_switch";
    private static final String TOUCH_SIZE = "touch_size";
    private static final String TOUCH_ALPHA = "touch_alpha";
    private static final String TOUCH_X = "touch_x";
    private static final String TOUCH_Y = "touch_y";

    public PreferenceManager(Context context) {
        sp = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    public void putTouchSwitchState(boolean state) {
        sp.edit().putBoolean(TOUCH_SWITCH, state).apply();
    }

    public boolean getTouchSwitchState() {
        return sp.getBoolean(TOUCH_SWITCH, false);
    }

    public void putTouchSize(int size) {
        sp.edit().putInt(TOUCH_SIZE, size).apply();
    }

    public int getTouchSize() {
        return sp.getInt(TOUCH_SIZE, 0);
    }

    public void putTouchAlpha(int alpha) {
        sp.edit().putInt(TOUCH_ALPHA, alpha).apply();
    }

    public int getTouchAlpha() {
        return sp.getInt(TOUCH_ALPHA, 0);
    }

    public void putTouchX(int x) {
        sp.edit().putInt(TOUCH_X, x).apply();
    }

    public int getTouchX(int defaultX) {
        return sp.getInt(TOUCH_X, defaultX);
    }

    public void putTouchY(int y) {
        sp.edit().putInt(TOUCH_Y, y).apply();
    }

    public int getTouchY(int defaultY) {
        return sp.getInt(TOUCH_Y, defaultY);
    }
}
