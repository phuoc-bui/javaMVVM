package com.phuocbui.mvvm.util;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.util.Linkify;
import android.util.Pair;
import android.widget.TextView;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.ui.widget.HelpItemView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.regex.Pattern;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class BindingAdapters {


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
