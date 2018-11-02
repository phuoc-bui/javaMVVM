package com.redhelmet.alert2me.util;

import android.app.Activity;
import androidx.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.global.LambdaInterface;
import com.redhelmet.alert2me.ui.base.BaseRecyclerViewAdapter;
import com.redhelmet.alert2me.ui.base.BindableAdapter;
import com.redhelmet.alert2me.ui.widget.EventIcon;
import com.redhelmet.alert2me.ui.widget.HelpItemView;

import java.util.Collection;

public class BindingAdapters {

    @BindingAdapter("android:visibility")
    public static void bindVisibility(View view, Boolean visible) {
        if (visible == null) view.setVisibility(View.GONE);
        else {
            int visibility = visible ? View.VISIBLE : View.GONE;
            view.setVisibility(visibility);
        }
    }

    @BindingAdapter("binding:data")
    public static <T extends Collection> void setRecyclerViewData(RecyclerView recyclerView, T data) {
        if (recyclerView.getAdapter() instanceof BindableAdapter) {
            ((BindableAdapter<T>) recyclerView.getAdapter()).setData(data);
        }
    }

    @BindingAdapter("binding:adapter")
    public static <T extends BaseRecyclerViewAdapter> void setRecyclerViewAdapter(RecyclerView recyclerView, T adapter) {
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter("binding:onRefresh")
    public static void onSwipeToRefresh(SwipeRefreshLayout refreshLayout, Runnable runnable) {
        refreshLayout.setOnRefreshListener(runnable::run);
    }

    @BindingAdapter("binding:onRefresh")
    public static void onSwipeToRefreshFunction(SwipeRefreshLayout refreshLayout, LambdaInterface.Function runnable) {
        refreshLayout.setOnRefreshListener(runnable::apply);
    }

    @BindingAdapter("android:src")
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter("binding:rounded_background_stroke_color")
    public static void setRoundedBackgroundStrokeColor(View view, String color) {
        if (color == null || color.isEmpty()) return;
        int eventColor = Color.parseColor(color);
        GradientDrawable drawable = IconUtils.createRoundedBackgroundStroke((Activity) view.getContext(), eventColor);
        view.setBackground(drawable);
    }

    @BindingAdapter("binding:rounded_background_color")
    public static void setRoundedBackgroundColor(View view, String color) {
        if (color == null || color.isEmpty()) return;
        int eventColor = Color.parseColor(color);
        GradientDrawable drawable = IconUtils.createRoundedBackground((Activity) view.getContext(), eventColor);
        view.setBackground(drawable);
    }

    @BindingAdapter("binding:top_rounded_background_color")
    public static void setTopRoundedBackgroundColor(View view, String color) {
        if (color == null || color.isEmpty()) return;
        int eventColor = Color.parseColor(color);
        GradientDrawable drawable = IconUtils.createTopRoundedBackground((Activity) view.getContext(), eventColor);
        view.setBackground(drawable);
    }

    @BindingAdapter("android:textColor")
    public static void setEventTextColor(TextView textView, String color) {
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

    @BindingAdapter("binding:event")
    public static void setEventforIcon(EventIcon eventIcon, Event event) {
        if (event == null) return;
        eventIcon.setEvent(event);
    }

    @BindingAdapter("android:text")
    public static void setTextWithId(TextView textView, int stringId) {
        textView.setText(stringId);
    }

    @BindingAdapter("android:hint")
    public static void setHintWithId(TextInputLayout inputLayout, Integer stringId) {
        if (stringId == null) return;
        String str = inputLayout.getContext().getString(stringId);
        inputLayout.setHint(str);
    }

    @BindingAdapter("bind:ringtoneUri")
    public static void setRingtoneFromUri(TextView textView, String ringtoneUri) {
        if (ringtoneUri == null) return;
        Uri uri = Uri.parse(ringtoneUri);
        Ringtone ringtone = RingtoneManager.getRingtone(textView.getContext(), uri);
        String ringtoneName = ringtone.getTitle(textView.getContext());
        textView.setText(ringtoneName);
    }

    @BindingAdapter("bind:supportCode")
    public static void setSupportCode(HelpItemView textView, Pair<String, String> pair) {
        if (pair == null) return;
        String supportCode = textView.getContext().getString(R.string.help_support_code_desc, pair.first, pair.second);
        textView.setDescription(supportCode);
    }
}
