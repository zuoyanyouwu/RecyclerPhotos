package com.valxu.recycler.photos.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.valxu.recycler.photos.R;

/**
 * Author: zuoyanyouwu
 * Date: 2015-09-18
 */
public class BitmapUtil {

    // 获取相册缩略图的加载尺寸
    public static int[] getThumbnailSize(Context context, int spanCount) {

        int size[] = new int[2];

        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        size[0] = (dm.widthPixels - res.getDimensionPixelSize(R.dimen.photo_item_margin) * (spanCount - 1)) / spanCount;
        size[1] = size[0];

        return size;
    }

}
