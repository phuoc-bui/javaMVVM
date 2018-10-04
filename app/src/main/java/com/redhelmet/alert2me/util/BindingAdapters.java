package com.redhelmet.alert2me.util;

import android.app.Activity;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.redhelmet.alert2me.global.EventIcon;
import com.redhelmet.alert2me.ui.base.BindableAdapter;

import java.util.Collection;

public class BindingAdapters {

    @BindingAdapter("android:visibility")
    public static void bindVisibility(View view, boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        view.setVisibility(visibility);
    }

    @BindingAdapter("data")
    public static <T extends Collection> void setRecyclerViewData(RecyclerView recyclerView, T data) {
        if (recyclerView.getAdapter() instanceof BindableAdapter) {
            ((BindableAdapter<T>) recyclerView.getAdapter()).setData(data);
        }
    }

    @BindingAdapter("onRefresh")
    public static void onSwipeToRefresh(SwipeRefreshLayout refreshLayout, Runnable runnable) {
        refreshLayout.setOnRefreshListener(runnable::run);
    }

    @BindingAdapter("android:src")
    public static void setImageviewBitmap(ImageView imageView, EventIcon icon) {
        Activity activity = (Activity) imageView.getContext();
        imageView.setImageBitmap(icon.createIcon(activity));
    }

    @BindingAdapter("android:src")
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }
}
