package com.phuocbui.basemodule.global;

import android.content.Context;

import javax.inject.Inject;

import androidx.annotation.StringRes;

public class ResourceProvider {

    private Context context;

    @Inject
    public ResourceProvider(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getString(@StringRes int resId) {
        return context.getString(resId);
    }

    public String getString(@StringRes int formatId, Object... formatArgs) {
        return context.getString(formatId, formatArgs);
    }
}
