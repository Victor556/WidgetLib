package com.putao.ptx.ptui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.putao.ptx.ptui.adapter.AbsBaseAdapter
import com.putao.ptx.ptui.adapter.ViewHolder
import com.putao.ptx.widget.PTFlowBanner

/**
 * <p><br/>ClassName : {@link PTFlowBannerSample}
 * <br/>Description :
 * <br/>　
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-07 11:11:44</p>
 */
object PTFlowBannerSample {

    fun generatePTFlowBanner(context: Context): PTFlowBanner?
            = (generateView(context, R.layout.view_flow_banner) as? PTFlowBanner).also {
        val flowBannerAdapter = FlowBannerAdapter(context, R.layout.item_flow_banner)
        val bannerData = mutableListOf<Int>()
        bannerData.add(R.mipmap.banner1)
        bannerData.add(R.mipmap.banner2)
        bannerData.add(R.mipmap.banner3)
        bannerData.add(R.mipmap.banner4)
        flowBannerAdapter.refresh(bannerData)
        it?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400)
        it?.setAdapter(flowBannerAdapter, bannerData.size shl 10)
    }

    internal class FlowBannerAdapter(context: Context, layoutARes: Int) : AbsBaseAdapter<Int>(context, layoutARes) {

        override fun bindData(convertView: View?, position: Int, itemData: Int) {
            with(ViewHolder.getView<ImageView>(convertView, R.id.iv_banner)) {
                setBackgroundResource(itemData)
                setOnClickListener {
                    Toast.makeText(context, "" + (position % data.size), Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun getCount(): Int {
            return Int.MAX_VALUE
        }

        override fun getItem(position: Int): Int {
            return with(data) {
                get(position % data.size)
            }
        }
    }
}