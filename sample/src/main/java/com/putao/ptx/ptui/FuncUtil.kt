package com.putao.ptx.ptui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.putao.ptx.app.PTDialog
import com.putao.ptx.util.dp2px
import com.putao.ptx.util.toast
import com.putao.ptx.widget.PTButton
import java.util.*

/**
 * <p>package        : com.putao.ptx.ptui.component
 * <br/>
 * <br/>Description  :
 * <br/>
 * <br/>Author       : Victor<liuhe556@126.com>
 * <br/>
 * <br/>Created date : 2017-07-12</p>
 */
fun genPTButton(ctx: Context) = generateView<View>(ctx, R.layout.ptbutton, -1, -1) { v ->
    val random = Random()
    var toast: Toast? = null
    val it = v.findViewById(R.id.cs_size) as PTButton
    val value: Runnable = object : Runnable {
        override fun run() {
            it.isEnabled = !it.isEnabled;
            it.layoutParams?.width = 300 + random.nextInt(150)
            it.layoutParams?.height = 100 + random.nextInt(50)
            it.invalidate()
            toast?.cancel()
            toast = ctx.toast("${it::class.java.name} enable:${it.isEnabled}")
            it.postDelayed(this, 10000)
        }
    }
    var cnt = 1
    it.text = """自定义属性：color_normal，color_press，color_unable，
color_text_normal，color_text_press，color_text_unable，
color_stroke_normal，color_stroke_press，width_stroke，radius_corner，
is_half_circle（默认true,两边是否为半圆形形状，true时radius_corner无效，）
默认文字居中，默认文字大小为18sp。"""
//    (v as ViewGroup).getChre
    it.setOnClickListener { v ->
        toast?.cancel()
        it.isHalfCircle = random.nextBoolean()
        toast = ctx.toast("${it::class.java.simpleName} click:${cnt++} isHalfCircle:${it.isHalfCircle}")
    }
    //it.postDelayed(value, 5000)
}

var cnt = 0
fun genPTDialog(ctx: Context) = run {
    cnt++
    val msg = """自定义对话框，主要特点：
    1.设置圆角对话框，圆角可调默认${PTDialog.DEF_CORNER_RADIUS_DP}dp,
    2.对话框宽高可调，默认${PTDialog.DEF_WIDTH_DP}dp*${PTDialog.DEF_HEIGHT_DP}dp
    3.有3中样式:a.添加自定义的View，b.添加消息内容，生成1个按钮的弹框，c.添加消息内容，生成2个按钮的弹框"""
    when (cnt % 4) {
        1 -> {
            PTDialog(ctx, msg + "\n此对话框类型：1个按钮风格", null, null, 50, null, null, ctx.dp2px(PTDialog.DEF_WIDTH_DP * 1.2f).toInt(),
                    ctx.dp2px(PTDialog.DEF_HEIGHT_DP * 1.2f).toInt())
        }
        2 -> PTDialog(ctx, msg + "\n此对话框类型：2个按钮风格",
                "leftBtn", View.OnClickListener { ctx.toast("click leftBtn") }, 80,
                "rightBtn", View.OnClickListener { ctx.toast("click rightBtn") },
                ctx.dp2px(PTDialog.DEF_WIDTH_DP * 1.3f).toInt(), ctx.dp2px(PTDialog.DEF_HEIGHT_DP * 1.3f).toInt())
        0 -> PTDialog(ctx, TextView(ctx).apply {
            text = msg + "\n此对话框类型：自定义View"
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(600, 400)
        }, /*cornerRadius = */160)
        3-> PTDialog(ctx, R.layout.main_view, 20, 1500, 800)
        else -> PTDialog(ctx, msg)
    }.show()
    null;
}

fun genPTClearEditText(ctx: Context) = generateView<View>(ctx, R.layout.ptclear_edit_text, 500, 300) {
    ctx.toast("只在你需要的时候才显示清除的图片;不对当前的监听产生干扰.", false)
}