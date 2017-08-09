package com.putao.ptx.ptui.component;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putao.ptx.ptui.R;
import com.putao.ptx.util.UtilKt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liw on 2017/5/4.
 */

public class MainView extends LinearLayout {
    private Context mContext;

    @BindView(R.id.tv_screen)
    TextView tvScreen;

    @BindView(R.id.tv_app_limit)
    TextView tvLimit;

    @BindView(R.id.tv_app_synchronous)
    TextView tvSynchronous;

    @BindView(R.id.tv_lock)
    TextView tvLock;

    @BindView(R.id.tv_exit)
    TextView tvExit;

    public MainView(Context context) {
        super(context);
        mContext = context;
        initUi();
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initUi();
    }

    private void initUi() {
        inflate(mContext, R.layout.main_view,this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.rl_parent)
    public void close() {
        AssistTouchManager.createIconView(mContext);
    }

    @OnClick(R.id.tv_screen)
    public void clickScreen() {
        tvScreen.setSelected(true);
        getForegroundApp();
        UtilKt.toast(mContext,"执行屏幕投射");
        //Toast.makeText(mContext, "执行屏幕投射", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.tv_app_limit)
    public void clickAppControl() {
        tvLimit.setSelected(true);
        //Toast.makeText(mContext, "执行APP限制", Toast.LENGTH_LONG).show();
        UtilKt.toast(mContext,"执行APP限制");
    }

    @OnClick(R.id.tv_app_synchronous)
    public void clickSynchronous() {
        tvSynchronous.setSelected(true);
//        Toast.makeText(mContext, "执行APP同步", Toast.LENGTH_LONG).show();
        UtilKt.toast(mContext,"执行APP同步");
    }

    @OnClick(R.id.tv_lock)
    public void clickLock() {
        tvLock.setSelected(true);
//        Toast.makeText(mContext, "执行一键锁定", Toast.LENGTH_LONG).show();
        UtilKt.toast(mContext,"执行一键锁定");
    }

    @OnClick(R.id.tv_exit)
    public void clickExit() {
        tvExit.setSelected(true);
//        Toast.makeText(mContext, "执行一键退出", Toast.LENGTH_LONG).show();
        UtilKt.toast(mContext,"执行一键退出");
    }

    private void getForegroundApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<String> result = new ArrayList<String>();
            UsageStatsManager usm = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
            long now = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(
                    UsageStatsManager.INTERVAL_BEST, now - 1000,
                    now);
            if (appList.size() > 0) {
                Collections.sort(appList, new Comparator<UsageStats>() {
                    @Override
                    public int compare(UsageStats lhs, UsageStats rhs) {
                        if (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) {
                            return -1;
                        } else if (lhs.getLastTimeUsed() < rhs.getLastTimeUsed()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
                if (appList.size() > 0) {
                    for (int i = 0; i < appList.size(); i++) {
                        UsageStats us = appList.get(i);
                        if (!result.contains(us.getPackageName())) {
                            result.add(us.getPackageName());
                        }
                    }
                }
//                Toast.makeText(mContext, result.get(0), Toast.LENGTH_LONG).show();
                UtilKt.toast(mContext,result.get(0));
            }
        }
    }
}
