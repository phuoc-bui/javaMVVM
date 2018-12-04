package com.phuocbui.basemodule.util;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;

import com.phuocbui.basemodule.R;

public class DrawableUtils {

    private DrawableUtils() {
    }

    public static GradientDrawable createRoundedBackgroundStroke(Activity context, int color) {
        GradientDrawable shape = (GradientDrawable) context.getResources().getDrawable(R.drawable.bg_rounded_red_stroke);
        shape.setStroke(1, color);
        return shape;
    }

    public static GradientDrawable createRoundedBackground(Activity context, int color) {
        GradientDrawable shape = (GradientDrawable) context.getResources().getDrawable(R.drawable.bg_rounded_white);
        shape.setColor(color);
        return shape;
    }

    public static GradientDrawable createTopRoundedBackground(Activity context, int color) {
        GradientDrawable shape = (GradientDrawable) context.getResources().getDrawable(R.drawable.bg_top_rounded_white);
        shape.setColor(color);
        return shape;
    }
}
