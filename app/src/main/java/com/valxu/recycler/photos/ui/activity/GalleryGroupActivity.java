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
import com.valxu.recycler.photos.R;
import com.valxu.recycler.photos.model.AdapterItemType;
import com.valxu.recycler.photos.model.Photo;
import com.valxu.recycler.photos.PhotoData;
import com.valxu.recycler.photos.recycler.PhotoGroupItemSpacesDecoration;
import com.valxu.recycler.photos.recycler.PhotoGroupItemSpacesDecoration.GroupItemDecorationListener;
import com.valxu.recycler.photos.ui.adapter.BaseRecyclerAdapter.OnItemClickListener;
import com.valxu.recycler.photos.ui.adapter.GalleryGroupRecyclerAdapter;
import com.valxu.recycler.photos.ui.adapter.GalleryGroupRecyclerAdapter.GalleryChildRecyclerViewHolder;
import com.valxu.recycler.photos.ui.adapter.GalleryGroupRecyclerAdapter.GalleryGroupRecyclerViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Author: zuoyanyouwu
 * Date: 2015-10-28
 */
public class GalleryGroupActivity extends AppCompatActivity
        implements OnItemClickListener, ActionMode.Callback, GroupItemDecorationListener {

    private RecyclerView mPhotoGrid;
    private GalleryGroupRecyclerAdapter mAdapter;

    private ActionMode mSelectActionMode;
    private boolean mSelectAllMode;

    private int mSpanCount;

    private HashMap<String, Integer> mPhotoGroupMap;
    private HashMap<String, Integer> mPhotoGroupSelectMap;
    private HashMap<String, Integer> mPhotoGroupPosition;
    private int mSelectedGroupCount;

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
        mSpanCount = getResources().getInteger(R.integer.gallery_span_count);
        mPhotoGrid.setHasFixedSize(true);

        GridLayoutManager manager = new GridLayoutManager(this, mSpanCount);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (isGroup(position)) {
                    return mSpanCount;
                } else {
                    return 1;
                }
            }
        });

        mPhotoGrid.setLayoutManager(manager);
        int space = getResources().getDimensionPixelSize(R.dimen.photo_item_margin);
        mPhotoGrid.addItemDecoration(new PhotoGroupItemSpacesDecoration(mSpanCount, space, this));
        mPhotoGrid.addOnScrollListener(new GalleryScrollListener(this));
        mAdapter = new GalleryGroupRecyclerAdapter(GalleryGroupActivity.this, mSpanCount);
        mPhotoGrid.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        PhotoData.resetPhotoGroups();

        initPhotoGroupMap();

    }

    private void initPhotoGroupMap() {
        mPhotoGroupMap = new HashMap<>();
        mPhotoGroupSelectMap = new HashMap<>();
        mPhotoGroupPosition = new HashMap<>();
        mSelectedGroupCount = 0;

        int size = PhotoData.mPhotoGroups.size();
        int currentGroupCount = 0;
        for (int i = size - 1; i >= 0; i--) {

            Photo photo = PhotoData.mPhotoGroups.get(i);
            if (photo.type == Photo.PHOTO_TYPE_TITLE) {

                // 存储当前分组包含的图片数量
                mPhotoGroupMap.put(photo.date, currentGroupCount);
                mPhotoGroupPosition.put(photo.date, i);
                // 初始化选择 map
                mPhotoGroupSelectMap.put(photo.date, 0);

                currentGroupCount = 0;
            } else {
                currentGroupCount++;
            }

        }

    }

    private int getItemType(int position) {
        return PhotoData.mPhotoGroups.get(position).type == Photo.PHOTO_TYPE_TITLE ?
                GalleryGroupRecyclerAdapter.ITEM_VIEW_TYPE_GROUP : GalleryGroupRecyclerAdapter.ITEM_VIEW_TYPE_CHILD;
    }

    private boolean isGroup(int position) {
        return getItemType(position) == GalleryGroupRecyclerAdapter.ITEM_VIEW_TYPE_GROUP;
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
        MenuItem delItem = menu.findItem(R.id.action_gallery_select_del);
        selectItem.setIcon(mSelectAllMode ? R.mipmap.ic_photo_all_cancel : R.mipmap.ic_photo_all_select);
        delItem.setEnabled(mAdapter.getSelectedItemsCount() > 0);
        delItem.setIcon(mAdapter.getSelectedItemsCount() > 0 ? R.mipmap.ic_photo_del : R.mipmap.ic_photo_del_disable);
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

            case R.id.action_gallery_select_del: {
                performDeleteAction();
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
            if (isGroup(position)) {
                performGalleryGroupCheck(position);
            } else {
                performGalleryItemCheck(position);
            }
        }
    }

    @Override
    public boolean onItemLongClick(View view, int position) {

        if (!mAdapter.isMultiChoice() && !isGroup(position)) {
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

    private void performGalleryGroupCheck(int groupPos) {
        mAdapter.toggleItemChoice(groupPos);

        boolean mGroupSelected = mAdapter.getItemSelected(groupPos);
        if (mGroupSelected) {
            mSelectedGroupCount++;
        } else {
            mSelectedGroupCount--;
        }

        GalleryGroupRecyclerViewHolder groupViewHolder = (GalleryGroupRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(groupPos);
        if (groupViewHolder != null) {
            groupViewHolder.mGalleryGroupCheckView.setVisibility(View.VISIBLE);
            groupViewHolder.mGalleryGroupCheckView.setImageResource(mGroupSelected ? R.mipmap.ic_photo_corner_checked : R.mipmap.ic_photo_corner_normal);
        }

        Photo groupPhoto = PhotoData.mPhotoGroups.get(groupPos);

        int size = PhotoData.mPhotoGroups.size();
        for (int i = groupPos + 1; i < size; i++) {
            Photo photo = PhotoData.mPhotoGroups.get(i);
            if (!photo.date.equals(groupPhoto.date)) {
                break;
            }

            GalleryChildRecyclerViewHolder childViewHolder = (GalleryChildRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(i);
            if (childViewHolder != null) {
                childViewHolder.mCheckableImageView.setVisibility(View.VISIBLE);
                if (mGroupSelected) {
                    childViewHolder.mCheckableImageView.setImageResource(R.mipmap.ic_photo_corner_checked);
                    if (!mAdapter.getItemSelected(i)) {
                        mAdapter.toggleItemChoice(i);
                        int selectCount = mPhotoGroupSelectMap.get(photo.date) + 1;
                        mPhotoGroupSelectMap.put(photo.date, selectCount);
                        playHolderAnimatorSet(childViewHolder, true);
                    }
                } else {
                    childViewHolder.mCheckableImageView.setImageResource(R.mipmap.ic_photo_corner_normal);
                    if (mAdapter.getItemSelected(i)) {
                        mAdapter.toggleItemChoice(i);
                        int selectCount = mPhotoGroupSelectMap.get(photo.date) - 1;
                        mPhotoGroupSelectMap.put(photo.date, selectCount);
                        playHolderAnimatorSet(childViewHolder, false);
                    }
                }
            } else {

                if (mGroupSelected) {
                    if (!mAdapter.getItemSelected(i)) {
                        mAdapter.toggleItemChoice(i);
                        int selectCount = mPhotoGroupSelectMap.get(photo.date) + 1;
                        mPhotoGroupSelectMap.put(photo.date, selectCount);
                    }
                } else {
                    if (mAdapter.getItemSelected(i)) {
                        mAdapter.toggleItemChoice(i);
                        int selectCount = mPhotoGroupSelectMap.get(photo.date) - 1;
                        mPhotoGroupSelectMap.put(photo.date, selectCount);
                    }
                }
            }
        }

        updateSelectActionModeTitle();

    }

    private void performGalleryItemCheck(int position) {
        mAdapter.toggleItemChoice(position);

        boolean selected = mAdapter.getItemSelected(position);

        Photo photo = PhotoData.mPhotoGroups.get(position);
        int selectCount = mPhotoGroupSelectMap.get(photo.date);
        int groupItemCount = mPhotoGroupMap.get(photo.date);

        int groupPos = mPhotoGroupPosition.get(photo.date);
        GalleryGroupRecyclerViewHolder groupViewHolder = (GalleryGroupRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(groupPos);

        if (selected) {
            selectCount++;
            if (selectCount == groupItemCount) {
                mSelectedGroupCount++;
                if (groupViewHolder != null) {
                    groupViewHolder.mGalleryGroupCheckView.setImageResource(R.mipmap.ic_photo_corner_checked);
                }
                if (!mAdapter.getItemSelected(groupPos)) {
                    mAdapter.toggleItemChoice(groupPos);
                }
            }
        } else {

            if (selectCount == groupItemCount) {
                mSelectedGroupCount--;
                if (groupViewHolder != null) {
                    groupViewHolder.mGalleryGroupCheckView.setImageResource(R.mipmap.ic_photo_corner_normal);
                }
                if (mAdapter.getItemSelected(groupPos)) {
                    mAdapter.toggleItemChoice(groupPos);
                }
            }
            selectCount--;
        }

        mPhotoGroupSelectMap.put(photo.date, selectCount);

        GalleryChildRecyclerViewHolder viewHolder = (GalleryChildRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            playHolderAnimatorSet(viewHolder, selected);
        }

        updateSelectActionModeTitle();

    }

    private void updateSelectActionModeTitle() {
        int count = mAdapter.getSelectedItemsCount();
        // 删除 Group 选择数
        count -= mSelectedGroupCount;
        if (count == 0) {
            mSelectActionMode.setTitle("");
        } else {
            mSelectActionMode.setTitle(String.valueOf(count));
        }
        mSelectActionMode.invalidate();
    }

    private void performSelectAllAction() {

        int size = PhotoData.mPhotoGroups.size();
        for (int i = 0; i < size; i++) {



            if (isGroup(i)) {

                if (!mAdapter.getItemSelected(i)) {
                    mAdapter.toggleItemChoice(i);
                    mSelectedGroupCount++;
                }

                GalleryGroupRecyclerViewHolder groupViewHolder = (GalleryGroupRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(i);
                if (groupViewHolder != null) {
                    groupViewHolder.mGalleryGroupCheckView.setImageResource(R.mipmap.ic_photo_corner_checked);
                    groupViewHolder.mGalleryGroupCheckView.setVisibility(View.VISIBLE);
                }
                continue;
            }

            GalleryChildRecyclerViewHolder childViewHolder = (GalleryChildRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(i);
            if (childViewHolder != null) {
                childViewHolder.mCheckableImageView.setImageResource(R.mipmap.ic_photo_corner_checked);
                childViewHolder.mCheckableImageView.setVisibility(View.VISIBLE);
                if (!mAdapter.getItemSelected(i)) {
                    playHolderAnimatorSet(childViewHolder, true);
                }
            }

            if (!mAdapter.getItemSelected(i)) {
                mAdapter.toggleItemChoice(i);
            }

        }

    }

    private void clearSelectionWithAnimator(boolean showCheckImage) {
        SparseArray<Integer> mSelections = mAdapter.getMultiSelections();
        int size = mSelections.size();
        for (int i = 0; i < size; i++) {
            int position = mSelections.valueAt(i);

            if (isGroup(position)) {
                mSelectedGroupCount--;
                GalleryGroupRecyclerViewHolder groupViewHolder = (GalleryGroupRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(position);
                if (groupViewHolder != null) {
                    groupViewHolder.mGalleryGroupCheckView.setImageResource(R.mipmap.ic_photo_corner_normal);
                    groupViewHolder.mGalleryGroupCheckView.setVisibility(showCheckImage ? View.VISIBLE : View.GONE);
                }
                continue;
            }

            Photo photo = PhotoData.mPhotoGroups.get(position);
            mPhotoGroupSelectMap.put(photo.date, 0);

            GalleryChildRecyclerViewHolder childViewHolder = (GalleryChildRecyclerViewHolder) mPhotoGrid.findViewHolderForAdapterPosition(position);
            if (childViewHolder != null) {
                childViewHolder.mCheckableImageView.setImageResource(R.mipmap.ic_photo_corner_normal);
                childViewHolder.mCheckableImageView.setVisibility(showCheckImage ? View.VISIBLE : View.GONE);
                playHolderAnimatorSet(childViewHolder, false);
            }
        }
        mAdapter.clearMultiSelections();
    }

    // 删除
    private void performDeleteAction() {
        SparseArray<Integer> mSelections = mAdapter.getMultiSelections();
        int size = mSelections.size();
        ArrayList<Integer> sortedList = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            sortedList.add(mSelections.valueAt(i));
        }
        Collections.sort(sortedList);
        for (int i = size - 1; i >= 0; i--) {
            int position = sortedList.get(i);
            mAdapter.notifyItemRemoved(position);
            PhotoData.mPhotoGroups.remove(position);
        }
        mAdapter.clearMultiSelections();
        mAdapter.setMultiChoice(false);
        mSelectAllMode = false;
        performSelectModeChange();
        invalidateOptionsMenu();
        initPhotoGroupMap();
    }

    private void playHolderAnimatorSet(final GalleryChildRecyclerViewHolder holder, final boolean selected) {
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

    @Override
    public AdapterItemType getAdapterItemType(int position) {


        return null;
    }

    @Override
    public int getPositionInGroup(int position) {
        return 0;
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
