package com.putao.ptx.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.putao.ptx.ptuilib.R;
import com.putao.ptx.util.Util;
import com.putao.ptx.widget.PTButton;


/**
 * <p>package        : com.putao.ptx.app
 * <br/>
 * <br/>Description  :
 * <br/>
 * <br/>Author       : Victor<liuhe556@126.com>
 * <br/>
 * <br/>Created date : 2017-08-04</p>
 */
public class PTDialog extends Dialog {
    public static final int DEF_INVALID_WIDTH = 0;
    public static final int DEF_INVALID_HEIGHT = 0;
    @Nullable
    private View layout;
    public static final float DEF_WIDTH_DP = 460.0F;
    public static final float DEF_HEIGHT_DP = 250.0F;
    public static final float DEF_CORNER_RADIUS_DP = 10.0F;
    private int mCornerRadius;

    @Nullable
    public final View getLayout() {
        return this.layout;
    }

    private final void setLayout(View var1) {
        this.layout = var1;
    }

    public final int getWidth() {
        Window window = this.getWindow();
        return window.getAttributes().width;
    }

    public final void setWidth(int w) {
        this.setWidthAndHeight(w, this.getHeight());
    }

    public final int getHeight() {
        return this.getWindow().getAttributes().height;
    }

    public final void setHeight(int h) {
        this.setWidthAndHeight(this.getWidth(), h);
    }

    public final float getCornerRadius() {
        return mCornerRadius;
    }

    public final void setWidthAndHeight(int width, int height) {
        Window window = this.getWindow();
        LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = width;
        params.height = height;
        window.setAttributes(params);
    }

    public final boolean setCornerRadius(float radius) {
        Drawable bg = this.getWindow().getDecorView().getBackground();
        GradientDrawable gd = null;
        if (bg == null) {
            gd = new GradientDrawable();
            gd.setColor(0xFFFFFFFF);
        } else if (bg instanceof GradientDrawable) {
            gd = (GradientDrawable) bg;
        }

        if (gd != null) {
            gd.setCornerRadius(radius);
            mCornerRadius = (int) radius;
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public final PTButton getSingleButton() {
        View view = this.layout != null ? this.layout.findViewById(R.id.btn) : null;
        if (!(view instanceof PTButton)) {
            view = null;
        }

        return (PTButton) view;
    }

    @Nullable
    public final PTButton getLeftButton() {
        View view = this.layout != null ? this.layout.findViewById(R.id.btn) : null;
        if (!(view instanceof PTButton)) {
            view = null;
        }

        return (PTButton) view;
    }

    @Nullable
    public final PTButton getRightButton() {
        View view = this.layout != null ? this.layout.findViewById(R.id.btn) : null;
        if (!(view instanceof PTButton)) {
            view = null;
        }

        return (PTButton) view;
    }

    @Nullable
    public final TextView getContentTextView() {
        View view = this.layout != null ? this.layout.findViewById(R.id.msg) : null;
        if (!(view instanceof TextView)) {
            view = null;
        }

        return (TextView) view;
    }

    private int getDefaultWidth(@NonNull Context ctx, @Nullable View layout) {
        if (layout != null && layout.getLayoutParams() != null) {
            return layout.getLayoutParams().width;
        } else {
            return (int) Util.dp2px(ctx, DEF_WIDTH_DP);
        }
    }

    private int getDefaultHeight(@NonNull Context ctx, @Nullable View layout) {
        if (layout != null && layout.getLayoutParams() != null) {
            return layout.getLayoutParams().height;
        } else {
            return (int) Util.dp2px(ctx, DEF_HEIGHT_DP);
        }
    }

    public PTDialog(@NonNull Context ctx, @Nullable View layout, int cornerRadius, int width, int height, int style) {
        super(ctx, style);
        this.layout = layout;
        width = isWidthHeightValid(width) ? width : getDefaultWidth(ctx, layout);
        height = isWidthHeightValid(height) ? height : getDefaultHeight(ctx, layout);
        mCornerRadius = cornerRadius;
        if (layout != null) {
            android.widget.FrameLayout.LayoutParams window = new android.widget.FrameLayout.LayoutParams(width, height);
            window.gravity = Gravity.CENTER;
            this.setContentView(layout, (android.view.ViewGroup.LayoutParams) window);
            View decorView = this.getWindow().getDecorView();
            GradientDrawable params = new GradientDrawable();
            params.setColor(0xFFFFFFFF);
            params.setCornerRadius((float) mCornerRadius);
            decorView.setBackground((Drawable) params);
        }

        Window window2 = this.getWindow();
        window2.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        LayoutParams params1 = window2.getAttributes();
        params1.gravity = Gravity.CENTER;
        params1.width = width;
        params1.height = height;
        window2.setAttributes(params1);
    }

    public PTDialog(@NonNull Context ctx, @Nullable View layout, int cornerRadius, int width, int height) {
        this(ctx, layout, cornerRadius, width, height, R.style.ptdialog);
    }

    public PTDialog(@NonNull Context ctx, @Nullable View layout, int cornerRadius) {
        this(ctx, layout, cornerRadius, DEF_INVALID_WIDTH, DEF_INVALID_HEIGHT);
    }

    public PTDialog(@NonNull Context ctx, @Nullable View layout) {
        this(ctx, layout, (int) Util.dp2px(ctx, DEF_CORNER_RADIUS_DP));
    }


    public PTDialog(@NonNull final Context ctx, final int layoutId, int cornerRadius, int width, int height, int style) {
        this(ctx, Util.getLayoutInflater(ctx).inflate(layoutId, null), cornerRadius, width, height, style);
    }

    public PTDialog(@NonNull Context ctx, int layoutId, int cornerRadius, int width, int height) {
        this(ctx, layoutId, cornerRadius, width, height, R.style.ptdialog);
    }


    public PTDialog(@NonNull Context ctx, int layoutId, int cornerRadius) {
        this(ctx, layoutId, cornerRadius, (int) Util.dp2px(ctx, DEF_WIDTH_DP), (int) Util.dp2px(ctx, DEF_HEIGHT_DP));
    }


    public PTDialog(@NonNull Context ctx, int layoutId) {
        this(ctx, layoutId, (int) Util.dp2px(ctx, DEF_CORNER_RADIUS_DP));
    }

    public final TextView doWithTextView(@NonNull View v, int id, @NonNull String msg, @Nullable final View.OnClickListener click) {
        TextView tv = Util.find(v, id);
        tv.setText(msg);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click != null) {
                    click.onClick(v);
                }
                cancel();
            }
        });
        return tv;
    }

    public PTDialog(@NonNull Context ctx, @NonNull String msg, @Nullable String leftBtnMsg,
                    @Nullable View.OnClickListener onClickLeft, int cornerRadius,
                    @Nullable String rightBtnMsg,
                    @Nullable View.OnClickListener onClickRight, int width, int height) {
        this(ctx, (View) null, cornerRadius, width, height);
        View view;
        Integer id;
        String str;
        if (rightBtnMsg == null) {
            view = Util.getLayoutInflater(ctx).inflate(R.layout.single_msg, null);
            this.layout = view;
            ((TextView) Util.find(view, R.id.msg)).setText(msg);
            id = R.id.btn;
            str = leftBtnMsg;
            if (leftBtnMsg == null) {
                str = "确定";
            }

            doWithTextView(view, id, str, onClickLeft);
        } else {
            view = Util.getLayoutInflater(ctx).inflate(R.layout.two_button_btn, null);
            this.layout = view;
            ((TextView) Util.find(view, R.id.msg)).setText(msg);
            id = R.id.btnLeft;
            str = leftBtnMsg;
            if (leftBtnMsg == null) {
                str = "确定";
            }

            doWithTextView(view, id, str, onClickLeft);
            doWithTextView(view, R.id.btnRight, rightBtnMsg, onClickRight);
        }

        if (this.layout != null) {
            //android.view.ViewGroup.LayoutParams layoutParams = this.layout != null ? this.layout.getLayoutParams() : null;
            if (!isWidthHeightValid(width)) {
                width = getDefaultWidth(ctx, this.layout);
            }

            if (!isWidthHeightValid(height)) {
                height = getDefaultHeight(ctx, this.layout);
            }
            this.setContentView(this.layout);

            setWidthAndHeight(width, height);
        }

        View decorView = this.getWindow().getDecorView();
        if (decorView != null) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(0xFFFFFFFF);
            gradientDrawable.setCornerRadius((float) cornerRadius);
            decorView.setBackground(gradientDrawable);
        }
    }

    private boolean isWidthHeightValid(int para) {
        return para >= -2 && para != 0;
    }

    public PTDialog(@NonNull Context ctx, @NonNull String msg, @Nullable String leftBtnMsg, @Nullable View.OnClickListener onClickLeft, int cornerRadius,
                    @Nullable String rightBtnMsg, @Nullable View.OnClickListener onClickRight) {
        this(ctx, msg, leftBtnMsg, onClickLeft, cornerRadius, rightBtnMsg, onClickRight, -3, -3);
    }

    public PTDialog(@NonNull Context ctx, @NonNull String msg, @Nullable String leftBtnMsg, @Nullable View.OnClickListener onClickLeft, int cornerRadius) {
        this(ctx, msg, leftBtnMsg, onClickLeft, cornerRadius, null, null);
    }

    public PTDialog(@NonNull Context ctx, @NonNull String msg, @Nullable String leftBtnMsg, @Nullable View.OnClickListener onClickLeft) {
        this(ctx, msg, leftBtnMsg, onClickLeft, (int) Util.dp2px(ctx, DEF_CORNER_RADIUS_DP));
    }

    public PTDialog(@NonNull Context ctx, @NonNull String msg, int cornerRadius) {
        this(ctx, msg, "确定", null, cornerRadius, null, null);
    }

    public PTDialog(@NonNull Context ctx, @NonNull String msg) {
        this(ctx, msg, "确定", null);
    }
}