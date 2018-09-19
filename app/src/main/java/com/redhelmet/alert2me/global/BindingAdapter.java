package com.redhelmet.alert2me.global;

import android.view.View;

public class BindingAdapter {

    @android.databinding.BindingAdapter("android:visibility")
    void bindVisibility(View view, boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        view.setVisibility(visibility);
    }
}
