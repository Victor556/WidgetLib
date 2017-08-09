package com.putao.ptx.widget;

import android.content.Context;

/**
 * <p><br/>ClassName : {@link PTArrayWheelAdapter}
 * <br/>Description :
 * <br/>
 * <br/>Author : xiaosw<xiaosw0802@163.com>
 * <br/>Create date : 2017-07-07 11:11:19</p>
 */

public class PTArrayWheelAdapter<T> extends PTAbstractWheelTextAdapter {

    // mDatas
    private T[] mDatas;

    public PTArrayWheelAdapter(Context context, T[] datas) {
        super(context);
        // setEmptyItemResource(TEXT_VIEW_ITEM_RESOURCE);
        this.mDatas = datas;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (index >= 0 && index < mDatas.length) {
            T item = mDatas[index];
            if (item instanceof CharSequence) {
                return (CharSequence) item;
            }
            return item.toString();
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return mDatas.length;
    }

}
