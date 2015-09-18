package com.valxu.recycler.photos.ui.adapter;

import android.animation.AnimatorSet;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import com.valxu.recycler.photos.util.BitmapUtil;

/**
 * Author: zuoyanyouwu
 * Date: 2015-09-18
 */
public abstract class BaseGalleryAdapter<VH extends android.support.v7.widget.RecyclerView.ViewHolder> extends BaseRecyclerAdapter<VH> {

    protected final Context mContext;
    protected SparseArray<Integer> mMultiSelections;

    protected int targetWidth, targetHeight;

    public BaseGalleryAdapter(Context context, int spanCount) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        mContext = context;
        mMultiSelections = new SparseArray<>();
        int size[] = BitmapUtil.getThumbnailSize(mContext, spanCount);
        targetWidth = size[0];
        targetHeight = size[1];
    }

    public final boolean getItemSelected(int position) {
        return mMultiSelections.indexOfKey(position) >= 0;
    }

    public final void toggleItemChoice(int position) {
        if (mMultiSelections.indexOfKey(position) >= 0) {
            mMultiSelections.remove(position);
        } else {
            mMultiSelections.put(position, position);
        }
    }

    public final int getSelectedItemsCount() {
        return mMultiSelections.size();
    }

    public final SparseArray<Integer> getMultiSelections() {
        return mMultiSelections;
    }

    public final void clearMultiSelections() {
        mMultiSelections.clear();
    }

    public final boolean hasSelectedItems() {
        return getSelectedItemsCount() > 0;
    }

    protected void playImageScaleAnimatorSetImmediately(View view, int position) {
        AnimatorSet animatorSet;
        if (getItemSelected(position)) {
            animatorSet = getViewScaleAnimatorSet(view, 1.0f, 0.8f, 0);
        } else {
            animatorSet = getViewScaleAnimatorSet(view, 0.8f, 1.0f, 0);
        }
        animatorSet.start();
    }

}
