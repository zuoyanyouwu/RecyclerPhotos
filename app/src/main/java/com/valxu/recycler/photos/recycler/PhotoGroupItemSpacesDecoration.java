package com.valxu.recycler.photos.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.valxu.recycler.photos.model.AdapterItemType;

/**
 * Author: zuoyanyouwu
 * Date: 2015-09-18
 */
public class PhotoGroupItemSpacesDecoration extends RecyclerView.ItemDecoration {

    private int mSpanCount;
    private int mSpace;
    private GroupItemDecorationListener mDecorationListener;

    public PhotoGroupItemSpacesDecoration(int spanCount, int space, GroupItemDecorationListener listener) {
        this.mSpanCount = spanCount;
        this.mSpace = space;
        mDecorationListener = listener;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        AdapterItemType type = mDecorationListener.getAdapterItemType(position);

        if (type == AdapterItemType.GROUP) {
            outRect.left = 0;
            outRect.top = 0;
            outRect.right = 0;
        } else {
            outRect.left = mSpace / 2;
            outRect.right = mSpace / 2;

        }
        outRect.bottom = mSpace;
    }

    public interface GroupItemDecorationListener {

        AdapterItemType getAdapterItemType(int position);

        int getPositionInGroup(int position);

    }

}
