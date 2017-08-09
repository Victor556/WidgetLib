package com.putao.ptx.ptui.component;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.putao.ptx.ptui.R;
import com.putao.ptx.widget.PTCustomSeekBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class AssistTouchSettingActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;

    @BindView(R.id.cs_size)
    PTCustomSeekBar sizeSeekBar;

    @BindView(R.id.cs_alpha)
    PTCustomSeekBar alphaSeekBar;

    @BindView(R.id.tb_switch)
    ToggleButton tbSwitch;

    @BindView(R.id.ll_touch_setting)
    LinearLayout llSetting;

    private ArrayList<String> mSizeStr = new ArrayList<>();
    private ArrayList<String> mAlphaStr = new ArrayList<>();
    private Intent mIntent;
    private PreferenceManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assist_touch);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        pm = new PreferenceManager(this);
        mIntent = new Intent(AssistTouchSettingActivity.this, AssistTouchService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
        initData();
        initUi();
    }

    private void initData() {
        mSizeStr.add("小");
        mSizeStr.add("正常");
        mSizeStr.add("大");
        sizeSeekBar.initData(mSizeStr);
        sizeSeekBar.setProgress(pm.getTouchSize());
        mAlphaStr.add("20%");
        mAlphaStr.add("");
        mAlphaStr.add("");
        mAlphaStr.add("");
        mAlphaStr.add("100%");
        alphaSeekBar.initData(mAlphaStr);
        alphaSeekBar.setProgress(pm.getTouchAlpha());
        tbSwitch.setChecked(pm.getTouchSwitchState());
    }

    private void initUi() {
        if (pm.getTouchSwitchState()) {
            llSetting.setVisibility(View.VISIBLE);
            if (AssistTouchManager.mIconView == null) {
                startService(mIntent);
            }
        } else {
            llSetting.setVisibility(View.GONE);
        }
        sizeSeekBar.setResponseOnTouch(new PTCustomSeekBar.TouchResponse() {
            @Override
            public void onTouchResponse(int volume) {
                if (pm.getTouchSwitchState()) {
                    AssistTouchManager.updateIconViewSize(volume);
                }
                pm.putTouchSize(volume);
            }
        });
        alphaSeekBar.setResponseOnTouch(new PTCustomSeekBar.TouchResponse() {
            @Override
            public void onTouchResponse(int volume) {
                if (pm.getTouchSwitchState()) {
                    AssistTouchManager.updateIconViewAlpha(volume);
                }
                pm.putTouchAlpha(volume);
            }
        });
    }

    @OnCheckedChanged(R.id.tb_switch)
    public void checkedSwitch(boolean b) {
        pm.putTouchSwitchState(b);
        if (b) {
            llSetting.setVisibility(View.VISIBLE);
            if (AssistTouchManager.mIconView == null) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(AssistTouchSettingActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1);
                    } else {
                        startService(mIntent);
                    }
                } else {
                    startService(mIntent);
                }
            }
        } else {
            llSetting.setVisibility(View.GONE);
            stopService(mIntent);
        }
    }

    @OnClick(R.id.bt_sure)
    public void btSure() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            startService(mIntent);
        } else if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!hasPermission()) {
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }

    //检测用户是否对本app开启了“Apps with usage access”权限
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
