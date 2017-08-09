package com.putao.ptx.ptui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.putao.ptx.app.PTDialog
import com.putao.ptx.ptui.PTBannerViewSample.getPTBannerView
import com.putao.ptx.ptui.PTFlowBannerSample.generatePTFlowBanner
import com.putao.ptx.ptui.PTFlowLayoutSample.generatePTFlowLayout
import com.putao.ptx.ptui.PTPopupWindowSample.genPTPopupWindow
import com.putao.ptx.ptui.PTWheelViewSample.generatePTWheelView
import com.putao.ptx.ptui.component.AssistTouchSettingActivity
import com.putao.ptx.ptui.component.MainView
import com.putao.ptx.util.LogUtil
import com.putao.ptx.util.dp2px
import com.putao.ptx.util.toast
import com.putao.ptx.widget.*
import java.util.*

/**
 * <p>package        : com.putao.ptx.ptui
 * <br/>
 * <br/>Description  :
 * <br/>
 * <br/>Author       : Victor<liuhe556@126.com>
 * <br/>
 * <br/>Created date : 2017-07-05</p>
 */
enum class EmWidget(val project: String, val clazz: Class<out Any>, val id: Int = -1, val genView: ((Context) -> View?)? = null) {

    PTResource_PTButton("PTUiLib", PTButton::class.java, ::genPTButton),
    PTDialog_("PTUiLib", PTDialog::class.java, ::genPTDialog),
    PTClearEdiText_("PTUiLib", PTClearEditText::class.java, ::genPTClearEditText),
    PTResource_CircleProgressView("PTResource", PTCircleProgressView::class.java, ::genCircleProgressView),
    PTSearchWidget_RecogniseAnimView("PTSearchWidget", PTFreshView::class.java, ::genRecogniseAnimView),
    PTTecAdmin_PTCustomSeekBar("PTTecAdmin", PTCustomSeekBar::class.java, ::genPTCustomSeekBar),
    PTTecAdmin_MainView("PTTecAdmin—悬浮按钮", MainView::class.java, ::genFloatView),
    PTGallery2_PTCheckBoxButton("PTGallery2", PTCheckBoxButton::class.java, ::genPTCheckBoxButton),
    PTGallery2_PTShareView("PTGallery2", PTShareView::class.java, ::genPTShareView),
    PTGallery2_PTVerticalSeekBar("PTGallery2", PTVerticalSeekBar::class.java, ::genPTVerticalSeekBar),
    PTMusic_PTBannerView("PTMusic", PTBannerView::class.java, ::getPTBannerView),
    PTAcademy_PTReadMoreTextView("PTAcademy", PTReadMoreTextView::class.java, ::getPTReadMoreTextView),
    PTAcademy_PTPopWindow("PTAcademy", PTPopWindow::class.java, ::genPTPopupWindow),

    PTCALENDAR_PTWHELLVIEW("PTCalendar2", PTWheelView::class.java, ::generatePTWheelView),
    PTAPPSTORE_PTFLOWBANNER("PTAppStore", PTFlowBanner::class.java, ::generatePTFlowBanner),
    PTTALK_PTWAVEVIEW("PTTalk", PTWaveView::class.java, ::generatePTWaveView),
    PTTALK_PTCIRCLE_IMAGEVIEW("PTTalk", PTCircleImageView::class.java, ::generatePTCircleImageView),
    PTTALK_PTFLOW_LAYOUT("Add", PTFlowLayout::class.java, ::generatePTFlowLayout),
    ;

    val widgetName: String by lazy { clazz.simpleName!! }
    val fullName by lazy { "$project------$widgetName" }

    constructor(project: String, clazz: Class<out Any>, genView: ((Context) -> View?)? = null) : this(project, clazz, -1, genView)

    fun getView(ctx: Context/*, vararg args: Any?*/): View? {
        try {
            return genView?.invoke(ctx) ?: generateView(ctx, id) //?: clazz.getConstructor(Context::class.java).newInstance(ctx)))
        } catch(e: Exception) {
            LogUtil.e(name, "EmWidget#getView: Exception", e)
            return null
        }
    }
}

val defWidthDp = 200
val defHeightDp = 200
val notSetPara = 0

private operator fun <T : View?> T.invoke(func: T.(T) -> Unit): T {
    if (this != null) {
        func(this)
    }
    return this
}

fun generateView(ctx: Context, id: Int, widthdp: Int = defWidthDp, heightdp: Int = defHeightDp): View? = if (id <= 0) null else LayoutInflater.from(ctx).inflate(id, null)?.also {
    if (widthdp != notSetPara || heightdp != notSetPara) it.layoutParams = ViewGroup.LayoutParams(ctx.dp2px(widthdp.toFloat()).toInt(), ctx.dp2px(heightdp.toFloat()).toInt())
}

inline fun <T : View> generateView(ctx: Context, id: Int, widthdp: Int = defWidthDp, heightdp: Int = defHeightDp, crossinline func: (T) -> Unit): T? = ((generateView(ctx, id, widthdp, heightdp) as T)){
    func.invoke(it)
}

inline fun <T : View> generateView(ctx: Context, id: Int, crossinline func: ((T) -> Unit)): T? {
    return generateView(ctx, id, defWidthDp, defHeightDp, func)
}

fun genCircleProgressView(ctx: Context): PTCircleProgressView? = generateView<PTCircleProgressView>(ctx, R.layout.ptresource_circleprogress, 0, 0) {
    it.postDelayed(object : Runnable {
        override fun run() {
            it.progress += 1
            if (it.progress >= it.maxProgress) it.progress = 0
            it.postDelayed(this, 20)
        }
    }, 10)
}

fun genRecogniseAnimView(ctx: Context) = (generateView(ctx, R.layout.ptfreshview, -1, -1)) {
    val layout = it as LinearLayout
    (0..layout.childCount - 1).forEach {
        (layout.getChildAt(it) as PTFreshView).animStart()
    }
    layout.addView(PTFreshView(ctx).also { it.postDelayed({ it.animStart() }, 500) },
            LinearLayout.LayoutParams(300, 300))
}

fun genPTCustomSeekBar(ctx: Context) = generateView<PTCustomSeekBar>(ctx, R.layout.custom_seek_bar, 500, 300) {
    val list = arrayListOf("最小", "小", "正常", "大", "最大")
    it.initData(list);
    it.setProgress(Random().nextInt(list.size));
    it.setResponseOnTouch {
        ctx.toast("$it")
    }
}

fun generatePTFlowBanner(context: Context): PTFlowBanner?
        = (generateView(context, R.layout.view_flow_banner) as? PTFlowBanner).also {
    val flowBannerAdapter = PTFlowBannerSample.FlowBannerAdapter(context, R.layout.item_flow_banner)
    val bannerData = mutableListOf<Int>()
    bannerData += (R.mipmap.banner1)
    bannerData.add(R.mipmap.banner2)
    bannerData.add(R.mipmap.banner3)
    bannerData.add(R.mipmap.banner4)
    flowBannerAdapter.refresh(bannerData)
    it?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400)
    it?.setAdapter(flowBannerAdapter, bannerData.size shl 10)
}

fun genFloatView(ctx: Context): View? = run { ctx.startActivity(Intent(ctx, AssistTouchSettingActivity::class.java));null }

fun genPTCheckBoxButton(ctx: Context) = generateView(ctx, R.layout.layout_pt_checkbox, 240, 120)

fun genPTShareView(ctx: Context) = generateView(ctx, R.layout.layout_pt_shareview, 210, ViewGroup.LayoutParams.WRAP_CONTENT)

fun genPTVerticalSeekBar(ctx: Context) = generateView(ctx, R.layout.layout_pt_vertical_seekbar, ViewGroup.LayoutParams.WRAP_CONTENT, 200)

fun getPTReadMoreTextView(ctx: Context) = generateView(ctx, R.layout.layout_readmoretext, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

fun generatePTWaveView(ctx: Context) = generateView(ctx, R.layout.view_wave, ViewGroup.LayoutParams.MATCH_PARENT, 1000)

fun generatePTCircleImageView(ctx: Context) = generateView(ctx, R.layout.view_circle_image_view,
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT)