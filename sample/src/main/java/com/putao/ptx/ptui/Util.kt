package com.putao.ptx.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.IdRes
import android.view.View
import android.widget.Toast
import java.lang.ref.SoftReference


///////////////////////////////////////////////////////////////////////////
// Context extend
///////////////////////////////////////////////////////////////////////////
/**
 * <p><br/>ClassName : {@link ScreenCompileUtil}
 * <br/>Description : 屏幕尺寸计算相关算法
 * <br/>　
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-06 12:12:14</p>
 */

/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
 * 如果dp<=0 如：ViewGroup.LayoutParams.WRAP_CONTENT，则原值返回
 */
fun Context.dp2px(dp: Float): Float {
    return if (dp > 0) dp * resources.displayMetrics.density else dp
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
 * 如果px<=0 如：ViewGroup.LayoutParams.WRAP_CONTENT，则原值返回
 */
fun Context.px2dp(px: Float): Float {
    return if (px > 0) px / resources.displayMetrics.density else px
}

/**
 * 根据手机的分辨率从 sp 的单位 转成为 px(像素) 
 * 如果sp<=0 如：ViewGroup.LayoutParams.WRAP_CONTENT，则原值返回
 */
fun Context.sp2px(sp: Float): Int {
    return if (sp > 0) (0.5f + sp * resources.displayMetrics.scaledDensity).toInt() else sp.toInt()
}

inline val Context.layoutInflater: android.view.LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater

inline fun <reified T : View> View.find(@IdRes id: Int): T = findViewById(id) as T


///////////////////////////////////////////////////////////////////////////
// Bitmap extend
///////////////////////////////////////////////////////////////////////////
/**
 * 裁剪圆形图片
 */
fun Bitmap.tailorRoundBitmap(): Bitmap {
    val srcWidth = width
    val srcHeight = height
    val radius = Math.min(srcWidth, srcHeight) / 2f
    val output = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    canvas.drawCircle(srcWidth / 2f, srcHeight / 2f, radius, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, 0f, 0f, paint)
    return output
}

///////////////////////////////////////////////////////////////////////////
// Drawable extend
///////////////////////////////////////////////////////////////////////////
/**
 * Drawable转Bitmap
 */
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return bitmap
    }
    val width = intrinsicWidth
    val height = intrinsicHeight
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, width, height)
    draw(canvas)
    return bitmap
}

inline val Context.screenHeight get() = resources.displayMetrics.heightPixels
inline val Context.screenWidth get() = resources.displayMetrics.widthPixels

var toast: SoftReference<Toast>? = null
@JvmOverloads
fun Context.toast(str: String, isShort: Boolean = true): Toast = Toast.makeText(this, str, if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).apply {
    toast?.get()?.cancel();toast = SoftReference(this); show()
}

@JvmOverloads
fun Context.toast(id: Int, isShort: Boolean = true): Toast = toast(resources.getString(id), isShort)
