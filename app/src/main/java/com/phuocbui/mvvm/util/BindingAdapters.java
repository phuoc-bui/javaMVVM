package com.phuocbui.mvvm.util;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.util.Linkify;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.global.LambdaInterface;
import com.phuocbui.mvvm.ui.base.adapter.BaseRecyclerViewAdapter;
import com.phuocbui.mvvm.ui.widget.HelpItemView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

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

    @BindingAdapter("bind:errorText")
    public static void setErrorText(TextInputLayout textInputLayout, int error) {
        if (error <= 0) textInputLayout.setError("");
        else textInputLayout.setError(textInputLayout.getContext().getString(error));
    }

    @BindingAdapter("binding:onRefresh")
    public static void onSwipeToRefresh(SwipeRefreshLayout refreshLayout, Runnable runnable) {
        refreshLayout.setOnRefreshListener(runnable::run);
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
