package com.putao.ptx.assistant.utils

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.putao.ptx.ptui.R

class UiDialog(context: Context,
               layout: View,
               width: Int = layout.layoutParams?.width ?: 200,
               height: Int = layout.layoutParams?.height ?: 200,
               style: Int = R.style.pano_dialog) : Dialog(context, style) {

    init {
        setContentView(layout)
        val window = window
        window!!.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        val params = window.attributes
        params.gravity = Gravity.CENTER
        params.width = width
        params.height = height
        window.attributes = params
    }
}