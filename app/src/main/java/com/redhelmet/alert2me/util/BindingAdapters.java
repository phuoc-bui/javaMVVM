package com.redhelmet.alert2me.util;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.util.Linkify;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.global.LambdaInterface;
import com.redhelmet.alert2me.ui.base.BaseRecyclerViewAdapter;
import com.redhelmet.alert2me.ui.base.BindableAdapter;
import com.redhelmet.alert2me.ui.widget.EventIcon;
import com.redhelmet.alert2me.ui.widget.HelpItemView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.Collection;
import java.util.regex.Pattern;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
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

    @BindingAdapter("bind:link")
    public static void setTextviewLink(TextView textview, String link) {
        Pattern pattern = Pattern.compile(link);
        Linkify.addLinks(textview, pattern, "http://");
    }

    @BindingAdapter("bind:radius")
    public static void setRadiusText(TextView textview, int radius) {
        String radiusString = textview.getContext().getString(R.string.wz_radius_title, radius);
        textview.setText(radiusString);
    }

    @BindingAdapter("bind:seekBarProgress")
    public static void setSeekBarValue(DiscreteSeekBar seekBar, int value) {
        if (seekBar.getProgress() != value)
            seekBar.setProgress(value);
    }

    @InverseBindingAdapter(attribute = "bind:seekBarProgress")
    public static int getSeekBarValue(DiscreteSeekBar seekBar) {
        return seekBar.getProgress();
    }

    @BindingAdapter("app:seekBarProgressAttrChanged")
    public static void setOnSeekBarValueChanged(DiscreteSeekBar seekBar, InverseBindingListener attrChange) {
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                attrChange.onChange();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }
}
