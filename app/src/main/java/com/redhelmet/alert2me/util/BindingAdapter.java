package com.redhelmet.alert2me.util;

import android.view.View;

public class BindingAdapter {

    @android.databinding.BindingAdapter("android:visibility")
    public static void bindVisibility(View view, boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        view.setVisibility(visibility);
    }
}
