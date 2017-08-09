package com.putao.ptx.ptui

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.putao.ptx.util.find
import com.putao.ptx.widget.PTFlowLayout
import com.putao.ptx.widget.PTFlowLayout.*
import kotlinx.android.synthetic.main.view_flow_layout.view.*
import java.util.*

/**
 * <p><br/>ClassName : {@link PTFlowLayoutSample}
 * <br/>Description : 流式布局demo
 * <br/>　
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-13 12:12:51</p>
 */
object PTFlowLayoutSample {

    fun generatePTFlowLayout(context: Context) =
            generateView<View>(context, R.layout.view_flow_layout,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT) {
                val strs = arrayOf("葡萄科技", "科技陪伴成长", "测试文字", "PTFlowLayout",
                        "Test", "12435634524", "小派", "再来一瓶", "休息一下，马上撸码", "流式布局控件使用demo",
                        "点击改变居中模式可改变当前排列")
                val childParams = RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                            ViewGroup.LayoutParams.WRAP_CONTENT).apply {

                    setMargins(8, 18, 8, 18)
                }
                val random = Random()
                val pt_flow_layout = it.find<PTFlowLayout>(R.id.pt_flow_layout)
                it.find<Button>(R.id.bt_change_modle).setOnClickListener {
                    with(pt_flow_layout) {
                        when(lineGraviey) {
                            DEF_LINE_GAVITY_TOP -> {
                                lineGraviey = DEF_LINE_GAVITY_CENTER_VERTICAL
                                gravity = Gravity.CENTER
                            }

                            DEF_LINE_GAVITY_CENTER_VERTICAL -> {
                                lineGraviey = DEF_LINE_GAVITY_BOTTOM
                                gravity = (Gravity.BOTTOM or Gravity.RIGHT)
                            }


                            DEF_LINE_GAVITY_BOTTOM -> {
                                lineGraviey = DEF_LINE_GAVITY_TOP
                                gravity = Gravity.LEFT
                            }

                        }


                    }
                }
                for (i in 0 until 30) {
                    val view = generateRadioButton(context).apply {
                        id = i
                        text = strs[random.nextInt(strs.size)]
                    }
                    if (i == 0) {
                        view.isChecked = true
                        view.setPadding(view.paddingLeft, 60, view.paddingRight, 60)
                    }
                    pt_flow_layout.
                            pt_flow_layout.addView(view, childParams)
                }
                pt_flow_layout.setOnCheckedChangeListener { group, checkedId ->
                    val radioButton = group.findViewById(checkedId) as? RadioButton
                    Toast.makeText(context, radioButton?.text ?: "Null", Toast.LENGTH_SHORT).show()
                }
            }

    fun generateRadioButton(context: Context) : RadioButton {
        return LayoutInflater.from(context).inflate(R.layout.view_radio_button, null) as RadioButton
    }
}