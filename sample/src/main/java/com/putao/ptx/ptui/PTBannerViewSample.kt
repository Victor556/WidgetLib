package com.putao.ptx.ptui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.putao.ptx.holder.BannerViewHolder
import com.putao.ptx.widget.PTBannerView

/**
 * Created by WhiteTec on 2017/7/13.
 */

object PTBannerViewSample{

    fun getPTBannerView(context: Context): View? = generateView(context,R.layout.ptbannerview)?.also {
        it.let {
            val bannerView = it.findViewById(R.id.banner) as PTBannerView<Int>
            val data = mutableListOf<Int>()
            data.add(R.mipmap.banner1)
            data.add(R.mipmap.banner2)
            data.add(R.mipmap.banner3)
            data.add(R.mipmap.banner4)
            data.add(R.mipmap.banner4)
            data.add(R.mipmap.banner4)
            bannerView.setPages(data) { MyBannerViewHolder() }
            bannerView.setIndicatorVisible(false)
            bannerView.start()
            val narmalBannerView = it.findViewById(R.id.banner_normal) as PTBannerView<Int>
            narmalBannerView.setPages(data) { MyBannerViewHolder() }
            narmalBannerView.setIndicatorVisible(true)
            it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1000)
        }
    }

    class MyBannerViewHolder : BannerViewHolder<Int> {


        private var mImageView: ImageView? = null
        override fun createView(context: Context): View {
            // 返回页面布局文件
            val view = LayoutInflater.from(context).inflate(R.layout.banner_item, null)
            mImageView = view.findViewById(R.id.banner_image) as ImageView
            return view
        }

        override fun onBind(context: Context, position: Int, data: Int?) {
            // 数据绑定
            mImageView!!.setImageResource(data!!)
        }
    }



}

