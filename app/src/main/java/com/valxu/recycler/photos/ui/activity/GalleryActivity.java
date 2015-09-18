package com.valxu.recycler.photos.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;
import com.valxu.recycler.photos.Data;
import com.valxu.recycler.photos.R;
import com.valxu.recycler.photos.ui.adapter.BaseRecyclerAdapter.OnItemClickListener;
import com.valxu.recycler.photos.ui.adapter.GalleryRecyclerAdapter;
import com.valxu.recycler.photos.ui.adapter.GalleryRecyclerAdapter.GalleryRecyclerViewHolder;
import com.valxu.recycler.photos.recycler.PhotoItemSpacesDecoration;

/**
 * Author: zuoyanyouwu
 * Date: 2015-09-18
 */
public class GalleryActivity extends AppCompatActivity implements OnItemClickListener, ActionMode.Callback{

    private RecyclerView mPhotoGrid;
    private GalleryRecyclerAdapter mAdapter;

    private ActionMode mSelectActionMode;
    private boolean mSelectAllMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initViews();

        initValues();

    }

    private void initViews() {
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mActionBarToolbar != null) {
            mActionBarToolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_text_color_black));
            setSupportActionBar(mActionBarToolbar);
        }

        mPhotoGrid = (RecyclerView) findViewById(R.id.gallery_photos);
    }

    private void initValues() {
        int spanCount = getResources().getInteger(R.integer.gallery_span_count);
        mPhotoGrid.setHasFixedSize(true);
        mPhotoGrid.setLayoutManager(new GridLayoutManager(this, spanCount));
        int space = getResources().getDimensionPixelSize(R.dimen.photo_item_margin);
        mPhotoGrid.addItemDecoration(new PhotoItemSpacesDecoration(spanCount, space));
        mPhotoGrid.addOnScrollListener(new GalleryScrollListener(this));
        mAdapter = new GalleryRecyclerAdapter(GalleryActivity.this, spanCount);
        mPhotoGrid.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gallery, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_gallery_select: {
                mAdapter.setMultiChoice(!mAdapter.isMultiChoice());
                performSelectModeChange();
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_gallery_select, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuItem selectItem = menu.findItem(R.id.action_gallery_select_all);
        selectItem.setIcon(mSelectAllMode ? R.mipmap.ic_photo_all_cancel : R.mipmap.ic_photo_all_select);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_gallery_select_all: {
                mSelectAllMode = !mSelectAllMode;
                if (mSelectAllMode) {
                    performSelectAllAction();
                    mSelectActionMode.setTitle(String.valueOf(mAdapter.getSelectedItemsCount()));
                } else {
                    clearSelectionWithAnimator(true);
                    mSelectActionMode.setTitle("");
                }
                mSelectActionMode.invalidate();
                return true;
            }

            default: {
                return false;
            }
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mSelectAllMode = false;
        mAdapter.setMultiChoice(false);
        clearSelectionWithAnimator(false);
    }


    @Override
    public void onItemClick(View view, int position) {
        if (mAdapter.isMultiChoice()) {
            performGalleryItemCheck(position);
        } else {
           // do something
        }
    }

    @Override
    public boolean onItemLongClick(View view, int position) {

        if (!mAdapter.isMultiChoice()) {
            mAdapter.setMultiChoice(true);
            mAdapter.notifyDataSetChanged();
            performSelectModeChange();
        }

        performGalleryItemCheck(position);

        return true;
    }

    private void performSelectModeChange() {
        if (mAdapter != null && mAdapter.isMultiChoice()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
            }

            mSelectActionMode = startSupportActionMode(this);
            mSelectActionMode.setTitle("");

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            if (mSelectActionMode != null) {
                mSelectActionMode.finish();
            }
        }
    }

    private void performGalleryItemCheck(int position) {
        mAdapter.toggleItemChoice(position);
        GalleryRecyclerViewHolder viewHolder = (GalleryRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            playHolderAnimatorSet(viewHolder, mAdapter.getItemSelected(position));
        }

        int count = mAdapter.getSelectedItemsCount();
        if (count == 0) {
            mSelectActionMode.setTitle("");
        } else {
            mSelectActionMode.setTitle(String.valueOf(count));
        }
        mSelectActionMode.invalidate();

    }

    private void performSelectAllAction() {

        int size = Data.URLS.length;
        for (int i = 0; i < size; i++) {
            GalleryRecyclerViewHolder viewHolder = (GalleryRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                viewHolder.mCheckableImageView.setImageResource(R.mipmap.ic_photo_corner_checked);
                viewHolder.mCheckableImageView.setVisibility(View.VISIBLE);
                if (!mAdapter.getItemSelected(i)) {
                    mAdapter.toggleItemChoice(i);
                    playHolderAnimatorSet(viewHolder, true);
                }
            } else {
                if (!mAdapter.getItemSelected(i)) {
                    mAdapter.toggleItemChoice(i);
                }
            }
        }

    }

    private void clearSelectionWithAnimator(boolean showCheckImage) {
        SparseArray<Integer> mSelections = mAdapter.getMultiSelections();
        int size = mSelections.size();
        for(int i = 0; i < size; i++) {
            int position = mSelections.valueAt(i);
            GalleryRecyclerViewHolder viewHolder = (GalleryRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                viewHolder.mCheckableImageView.setImageResource(R.mipmap.ic_photo_corner_normal);
                viewHolder.mCheckableImageView.setVisibility(showCheckImage ? View.VISIBLE : View.GONE);
                playHolderAnimatorSet(viewHolder, false);
            }
        }
        mAdapter.clearMultiSelections();
    }

    private void playHolderAnimatorSet(final GalleryRecyclerViewHolder holder, final boolean selected) {
        ObjectAnimator scaleX;
        ObjectAnimator scaleY;
        holder.mCheckableImageView.setImageResource(selected ? R.mipmap.ic_photo_corner_checked : R.mipmap.ic_photo_corner_normal);
        holder.mCheckableImageView.setVisibility(View.VISIBLE);
        if (selected) {
            scaleX = ObjectAnimator.ofFloat(holder.mPhotoView, "scaleX", 1.0f, 0.8F);
            scaleY = ObjectAnimator.ofFloat(holder.mPhotoView, "scaleY", 1.0f, 0.8F);
        } else {
            scaleX = ObjectAnimator.ofFloat(holder.mPhotoView, "scaleX", 0.8f, 1.0F);
            scaleY = ObjectAnimator.ofFloat(holder.mPhotoView, "scaleY", 0.8f, 1.0F);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.setDuration(150);
        animatorSet.start();
    }

    private class GalleryScrollListener extends RecyclerView.OnScrollListener {

        private final Context context;

        public GalleryScrollListener(Context context) {
            this.context = context;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
            final Picasso picasso = Picasso.with(context);
            if (scrollState == RecyclerView.SCROLL_STATE_IDLE || scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {
                picasso.resumeTag(context);
            } else {
                picasso.pauseTag(context);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }
    }


}
