package com.valxu.recycler.photos.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Author: zuoyanyouwu
 * Date: 2015-09-18
 */
public class PhotoItemSpacesDecoration extends RecyclerView.ItemDecoration {

    private int mSpanCount;
    private int mSpace;

    public PhotoItemSpacesDecoration(int spanCount, int space) {
        this.mSpanCount = spanCount;
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position < mSpanCount) {
            outRect.top = mSpace;
        }
        if ((position + 1) % mSpanCount == 1) {
            outRect.left = 0;
            outRect.right = mSpace / 2;
        } else if ((position + 1) % mSpanCount == 0){
            outRect.left = mSpace / 2;
            outRect.right = 0;
        } else {
            outRect.left = mSpace / 2;
            outRect.right = mSpace / 2;
        }
        outRect.bottom = mSpace;
    }



}
