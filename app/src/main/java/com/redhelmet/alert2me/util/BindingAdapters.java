package com.redhelmet.alert2me.util;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.redhelmet.alert2me.ui.base.BindableAdapter;

import java.util.Collection;

public class BindingAdapters {

    @BindingAdapter("android:visibility")
    public static void bindVisibility(View view, boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        view.setVisibility(visibility);
    }

    @BindingAdapter("binding:data")
    public static <T extends Collection> void setRecyclerViewData(RecyclerView recyclerView, T data) {
        if (recyclerView.getAdapter() instanceof BindableAdapter) {
            ((BindableAdapter<T>) recyclerView.getAdapter()).setData(data);
        }
    }

    @BindingAdapter("binding:onRefresh")
    public static void onSwipeToRefresh(SwipeRefreshLayout refreshLayout, Runnable runnable) {
        refreshLayout.setOnRefreshListener(runnable::run);
    }

    @BindingAdapter("binding:bitmap")
    public static void setImageviewBitmap(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
