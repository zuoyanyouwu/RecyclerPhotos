package com.valxu.recycler.photos.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.valxu.recycler.photos.R;
import com.valxu.recycler.photos.model.Photo;
import com.valxu.recycler.photos.PhotoData;

/**
 * Author: zuoyanyouwu
 * Date: 2015-09-18
 */
public class GalleryGroupRecyclerAdapter extends BaseGalleryGroupAdapter<RecyclerView.ViewHolder> {

    public static final int ITEM_VIEW_TYPE_GROUP = 0;
    public static final int ITEM_VIEW_TYPE_CHILD = 1;

    private boolean mIsMultiChoice = false;

    public GalleryGroupRecyclerAdapter(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_VIEW_TYPE_GROUP) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_recycler_photo_group, parent, false);
            return new GalleryGroupRecyclerViewHolder(this, v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_recycler_photo_item, parent, false);
            return new GalleryChildRecyclerViewHolder(this, v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        Photo photo = PhotoData.mPhotoGroups.get(position);
        if (viewHolder instanceof GalleryGroupRecyclerViewHolder) {
            GalleryGroupRecyclerViewHolder holder = (GalleryGroupRecyclerViewHolder) viewHolder;
            holder.mGalleryTitleView.setText(photo.date);

            if (mIsMultiChoice) {
                holder.mGalleryGroupCheckView.setVisibility(View.VISIBLE);
                holder.mGalleryGroupCheckView.setImageResource(
                        getItemSelected(position) ? R.mipmap.ic_photo_corner_checked : R.mipmap.ic_photo_corner_normal);
            } else {
                holder.mGalleryGroupCheckView.setImageResource(R.mipmap.ic_photo_corner_normal);
                holder.mGalleryGroupCheckView.setVisibility(View.GONE);
            }

        } else if (viewHolder instanceof GalleryChildRecyclerViewHolder){
            GalleryChildRecyclerViewHolder holder = (GalleryChildRecyclerViewHolder) viewHolder;
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
                    .load(photo.url)
                    .resize((int) (targetWidth * 1.2), (int) (targetHeight * 1.2))
                    .into(holder.mPhotoView);
        }

    }

    @Override
    public int getItemCount() {
        return PhotoData.mPhotoGroups.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isGroup(position) ? ITEM_VIEW_TYPE_GROUP : ITEM_VIEW_TYPE_CHILD;
    }


    public boolean isGroup(int position) {
        return PhotoData.mPhotoGroups.get(position).type == Photo.PHOTO_TYPE_TITLE;
    }

    public boolean isMultiChoice() {
        return mIsMultiChoice;
    }

    public void setMultiChoice(boolean isChoice) {
        this.mIsMultiChoice = isChoice;
        notifyDataSetChanged();
    }

    public static class GalleryGroupRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public GalleryGroupRecyclerAdapter mGalleryRecyclerAdapter;
        public TextView mGalleryTitleView;
        public ImageView mGalleryGroupCheckView;

        public GalleryGroupRecyclerViewHolder(GalleryGroupRecyclerAdapter adapter, View itemView) {
            super(itemView);
            mGalleryRecyclerAdapter = adapter;
            mGalleryTitleView = (TextView) itemView.findViewById(R.id.grid_recycler_photo_group_text);
            mGalleryGroupCheckView = (ImageView) itemView.findViewById(R.id.grid_recycler_photo_group_check);

            mGalleryGroupCheckView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mGalleryRecyclerAdapter.mItemClickListener != null) {
                mGalleryRecyclerAdapter.mItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

    public static class GalleryChildRecyclerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        public GalleryGroupRecyclerAdapter mGalleryRecyclerAdapter;
        public View mPhotoContainer;
        public ImageView mPhotoView;
        public ImageView mCheckableImageView;

        public GalleryChildRecyclerViewHolder(GalleryGroupRecyclerAdapter adapter, View itemView) {
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
