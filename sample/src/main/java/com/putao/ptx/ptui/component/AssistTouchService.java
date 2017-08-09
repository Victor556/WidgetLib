package com.putao.ptx.ptui.component;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Random;

/**
 * Created by liw on 2017/4/7.
 */

public class AssistTouchService extends Service {
    private static final int NOTIFICATION_ID = new Random(System
            .currentTimeMillis()).nextInt() + 1000;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        showView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AssistTouchManager.removeIconView();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void showView() {
        try {
            AssistTouchManager.createIconView(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
