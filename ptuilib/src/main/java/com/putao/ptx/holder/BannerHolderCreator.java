package com.putao.ptx.holder;


/**
 * @author WhiteTec 2017/7/13
 */
public interface BannerHolderCreator<V extends BannerViewHolder> {
    /**
     * 创建ViewHolder
     * @return
     */
    public V createViewHolder();
}
