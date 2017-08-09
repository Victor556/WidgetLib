package com.putao.ptx.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.SoftReference;

/**
 * <p>package        : com.putao.ptx.util
 * <br/>
 * <br/>Description  :屏幕尺寸计算相关算法及其他较通用方法
 * <br/>
 * <br/>Author       : Victor<liuhe556@126.com>
 * <br/>
 * <br/>Created date : 2017-08-04</p>
 */
public class Util {
    @Nullable
    private static SoftReference toast;


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     * 如果dp<=0 如：ViewGroup.LayoutParams.WRAP_CONTENT，则原值返回
     */
    public static final float dp2px(@NonNull Context context, float dp) {
        return dp > (float) 0 ? dp * context.getResources().getDisplayMetrics().density : dp;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     * 如果px<=0 如：ViewGroup.LayoutParams.WRAP_CONTENT，则原值返回
     */
    public static final float px2dp(@NonNull Context context, float px) {
        return px > (float) 0 ? px / context.getResources().getDisplayMetrics().density : px;
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素) 
     * 如果sp<=0 如：ViewGroup.LayoutParams.WRAP_CONTENT，则原值返回
     */
    public static final int sp2px(@NonNull Context context, float sp) {
        return sp > (float) 0 ? (int) (0.5F + sp * context.getResources().getDisplayMetrics().scaledDensity) : (int) sp;
    }

    @NonNull
    public static final LayoutInflater getLayoutInflater(@NonNull Context context) {
        return LayoutInflater.from(context);
    }

    public static final <T> T find(@NonNull View view, @IdRes int id) {
        return (T) view.findViewById(id);
    }

    /**
     * 裁剪圆形图片
     */
    @NonNull
    public static final Bitmap tailorRoundBitmap(@NonNull Bitmap bitmap) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float radius = (float) Math.min(srcWidth, srcHeight) / 2.0F;
        Bitmap output = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(1);
        canvas.drawCircle((float) srcWidth / 2.0F, (float) srcHeight / 2.0F, radius, paint);
        paint.setXfermode((Xfermode) (new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)));
        canvas.drawBitmap(bitmap, 0.0F, 0.0F, paint);
        return output;
    }

    /**
     * Drawable转Bitmap
     */
    @NonNull
    public static final Bitmap toBitmap(@NonNull Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return bitmap;
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            return bitmap;
        }
    }

    public static final int getScreenHeight(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static final int getScreenWidth(@NonNull Context $receiver) {
        return $receiver.getResources().getDisplayMetrics().widthPixels;
    }

    @NonNull
    public static final Toast toast(@NonNull Context context, @NonNull String str, boolean isShort) {
        Toast toast = Toast.makeText(context, str, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        SoftReference softReference = Util.toast;
        if (Util.toast != null) {
            Toast toast1 = (Toast) softReference.get();
            if (toast1 != null) {
                toast1.cancel();
            }
        }

        Util.toast = new SoftReference(toast);
        toast.show();
        return toast;
    }


    @NonNull
    public static final Toast toast(@NonNull Context context, @NonNull String str) {
        return toast(context, str, true);
    }

    @NonNull
    public static final Toast toast(@NonNull Context context, int id, boolean isShort) {
        return toast(context, context.getResources().getString(id), isShort);
    }

    @NonNull
    public static final Toast toast(@NonNull Context context, int id) {
        return toast(context, id, true);
    }
}
