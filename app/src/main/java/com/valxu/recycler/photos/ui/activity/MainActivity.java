package com.valxu.recycler.photos.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.valxu.recycler.photos.R;
import com.valxu.recycler.photos.recycler.MarginDecoration;
import com.valxu.recycler.photos.ui.adapter.BaseRecyclerAdapter;

/**
 * Author: xuke
 * Date: 2015-11-17
 */
public class MainActivity extends AppCompatActivity implements BaseRecyclerAdapter.OnItemClickListener {

    private Demo[] mDemos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mActionBarToolbar != null) {
            mActionBarToolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_text_color_black));
            setSupportActionBar(mActionBarToolbar);
        }

        mDemos = new Demo[]{
                new Demo(this, GalleryActivity.class, R.string.gallery_activity),
                new Demo(this, GalleryGroupActivity.class, R.string.gallery_group_activity),
        };

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new DemoAdapter(mDemos));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onItemClick(View view, int position) {
        startActivity(new Intent(this, mDemos[position].activityClass));
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        return false;
    }

    private static class DemoAdapter extends BaseRecyclerAdapter<DemoAdapter.DemoAdapterViewHolder> {
        private final Demo[] demos;

        public DemoAdapter(Demo[] demos) {
            this.demos = demos;
        }

        @Override
        public DemoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);
            return new DemoAdapterViewHolder(this, view);
        }

        @Override
        public void onBindViewHolder(final DemoAdapterViewHolder holder, final int position) {
            final Demo demo = demos[position];
            holder.textView.setText(demo.title);
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = holder.textView.getContext();
                    context.startActivity(new Intent(context, demo.activityClass));
                }
            });
        }

        @Override
        public int getItemCount() {
            return demos.length;
        }

        public static class DemoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public DemoAdapter mDemoAdapter;
            public TextView textView;

            public DemoAdapterViewHolder(DemoAdapter adapter, View itemView) {
                super(itemView);
                mDemoAdapter = adapter;
                textView = (TextView) itemView.findViewById(R.id.main_list_item_text);

            }

            @Override
            public void onClick(View v) {
                if (mDemoAdapter.mItemClickListener != null) {
                    mDemoAdapter.mItemClickListener.onItemClick(v, getLayoutPosition());
                }
            }
        }

    }

    public static class Demo {
        public final Class<?> activityClass;
        public final String title;

        public Demo(Context context, Class<?> activityClass, int titleId) {
            this.activityClass = activityClass;
            this.title = context.getString(titleId);
        }
    }

}
