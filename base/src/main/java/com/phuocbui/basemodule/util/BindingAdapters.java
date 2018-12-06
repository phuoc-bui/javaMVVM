package com.phuocbui.basemodule.util;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.phuocbui.basemodule.global.LambdaInterface;
import com.phuocbui.basemodule.ui.base.adapter.BaseRecyclerViewAdapter;

import java.util.regex.Pattern;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class BindingAdapters {

    @BindingAdapter("android:visibility")
    public static void bindVisibility(View view, Boolean visible) {
        if (visible == null) view.setVisibility(View.GONE);
        else {
            int visibility = visible ? View.VISIBLE : View.GONE;
            view.setVisibility(visibility);
        }
    }

    @BindingAdapter(value = {"bind:adapter", "bind:itemClick", "bind:itemLongClick"}, requireAll = false)
    public static <T extends BaseRecyclerViewAdapter> void setRecyclerViewAdapter(RecyclerView recyclerView, T adapter,
                                                                                  BaseRecyclerViewAdapter.ItemClickListener itemClick,
                                                                                  BaseRecyclerViewAdapter.ItemLongClickListener itemLongClick) {
        recyclerView.setAdapter(adapter);
        if (itemClick != null) adapter.setItemClickListener(itemClick);
        if (itemLongClick != null) adapter.setItemLongClickListener(itemLongClick);
    }

    @BindingAdapter("bind:onRefresh")
    public static void onSwipeToRefreshFunction(SwipeRefreshLayout refreshLayout, LambdaInterface.Function runnable) {
        refreshLayout.setOnRefreshListener(runnable::apply);
    }

    @BindingAdapter("android:src")
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter("android:textColor")
    public static void setTextColor(TextView textView, String color) {
        if (color == null || color.isEmpty()) return;
        int eventColor = Color.parseColor(color);
        textView.setTextColor(eventColor);
    }

    @BindingAdapter("android:tint")
    public static void seImageTintColor(ImageView imageView, String color) {
        if (color == null || color.isEmpty()) return;
        int eventColor = Color.parseColor(color);
        imageView.setColorFilter(eventColor, PorterDuff.Mode.SRC_IN);
    }

    @BindingAdapter("android:text")
    public static void setTextWithId(TextView textView, int stringId) {
        textView.setText(stringId);
    }

    @BindingAdapter("android:hint")
    public static void setHintWithId(TextInputLayout inputLayout, int stringId) {
        String str = inputLayout.getContext().getString(stringId);
        inputLayout.setHint(str);
    }

    @BindingAdapter("bind:link")
    public static void setTextviewLink(TextView textview, String link) {
        Pattern pattern = Pattern.compile(link);
        Linkify.addLinks(textview, pattern, "http://");
    }

    @BindingAdapter("bind:errorText")
    public static void setErrorText(TextInputLayout textInputLayout, int error) {
        if (error <= 0) textInputLayout.setError("");
        else textInputLayout.setError(textInputLayout.getContext().getString(error));
    }
}
