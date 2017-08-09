package com.putao.ptx.ptui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.putao.ptx.assistant.utils.UiDialog
import kotlinx.android.synthetic.main.layout_sample.*


/**
 * Created by WhiteTec on 2017/7/4.
 */

class SampleActivity : Activity() {

    private val mData: List<EmWidget> by lazy { EmWidget.values().toList() }
    private val mAdapter by lazy { HomeAdapter(this@SampleActivity, mData, onClick) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_sample)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = mAdapter
    }

    private val TAG: String = "SampleActivity "
    private var uiDialog: UiDialog? = null
    private val onClick = View.OnClickListener {
        val type = it.tag as? EmWidget
        Log.d(TAG, "onClick type:$type")

        val view = type?.getView(this@SampleActivity) ?: return@OnClickListener

        uiDialog = UiDialog(this, view)
        uiDialog?.show()
    }

    class MyViewHolder(val tv: TextView) : ViewHolder(tv)

    internal class HomeAdapter(val ctx: Context, val mData: List<EmWidget>, val onClick: View.OnClickListener) : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
            val tv = MyViewHolder(LayoutInflater.from(ctx).
                    inflate(android.R.layout.simple_spinner_item, null) as TextView)
            with(tv.tv) {
                gravity = android.view.Gravity.CENTER_VERTICAL
                width = 1000
                height = 100
                setOnClickListener(onClick)
            }
            return tv
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            with(holder.tv) {
                val emWidget = mData[position]
                tag = emWidget
                text = emWidget.fullName
            }

        }

        override fun getItemCount() = mData.size

    }

}


