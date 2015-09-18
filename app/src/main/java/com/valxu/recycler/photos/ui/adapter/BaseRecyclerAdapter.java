package com.valxu.recycler.photos.ui.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Author: zuoyanyouwu
 * Date: 2015-09-18
 */
public abstract class BaseRecyclerAdapter<VH extends android.support.v7.widget.RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private static final String TAG = "BaseRecyclerAdapter";

    protected OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        boolean onItemLongClick(View view, int position);
    }

    public final void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public AnimatorSet getViewScaleAnimatorSet(View view, float scaleFrom, float scaleTo, long duration) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scaleFrom, scaleTo);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scaleFrom, scaleTo);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.setDuration(duration);

        return animatorSet;
    }


}
