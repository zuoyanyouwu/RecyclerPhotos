package com.valxu.recycler.photos.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.valxu.recycler.photos.Data;
import com.valxu.recycler.photos.R;

/**
 * Author: zuoyanyouwu
 * Date: 2015-09-18
 */
public class GalleryRecyclerAdapter extends BaseGalleryAdapter<GalleryRecyclerAdapter.GalleryRecyclerViewHolder> {

    private boolean mIsMultiChoice = false;

    public GalleryRecyclerAdapter(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public GalleryRecyclerAdapter.GalleryRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_recycler_photo_item, parent, false);
        return new GalleryRecyclerViewHolder(this, v);
    }

    @Override
    public void onBindViewHolder(GalleryRecyclerAdapter.GalleryRecyclerViewHolder holder, int position) {

        if (mIsMultiChoice) {
            holder.mCheckableImageView.setVisibility(View.VISIBLE);
            holder.mCheckableImageView.setImageResource(
                    getItemSelected(position) ? R.mipmap.ic_photo_corner_checked : R.mipmap.ic_photo_corner_normal);
        } else {
            holder.mCheckableImageView.setImageResource(R.mipmap.ic_photo_corner_normal);
            holder.mCheckableImageView.setVisibility(View.GONE);
        }

        playImageScaleAnimatorSetImmediately(holder.mPhotoView, position);
        Picasso.with(mContext)
                .load(Data.URLS[position])
                .resize((int) (targetWidth * 1.2), (int) (targetHeight * 1.2))
                .into(holder.mPhotoView);

    }

    @Override
    public int getItemCount() {
        return Data.URLS.length;
    }

    public boolean isMultiChoice() {
        return mIsMultiChoice;
    }

    public void setMultiChoice(boolean isChoice) {
        this.mIsMultiChoice = isChoice;
        notifyDataSetChanged();
    }
    
    public static class GalleryRecyclerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        public GalleryRecyclerAdapter mGalleryRecyclerAdapter;
        public View mPhotoContainer;
        public ImageView mPhotoView;
        public ImageView mCheckableImageView;

        public GalleryRecyclerViewHolder(GalleryRecyclerAdapter adapter, View itemView) {
            super(itemView);
            mGalleryRecyclerAdapter = adapter;
            mPhotoContainer = itemView.findViewById(R.id.grid_recycler_photo_item_container);
            mPhotoView = (ImageView) itemView.findViewById(R.id.grid_recycler_photo_item_image);
            mCheckableImageView = (ImageView) itemView.findViewById(R.id.grid_recycler_photo_item_check);

            ViewGroup.LayoutParams params = mPhotoContainer.getLayoutParams();
            params.width = adapter.targetWidth;
            params.height = adapter.targetHeight;
            mPhotoContainer.setLayoutParams(params);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mGalleryRecyclerAdapter.mItemClickListener != null) {
                mGalleryRecyclerAdapter.mItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return mGalleryRecyclerAdapter.mItemClickListener != null && mGalleryRecyclerAdapter.mItemClickListener.onItemLongClick(v, getLayoutPosition());
        }
    }

}
