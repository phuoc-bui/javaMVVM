package com.phuocbui.basemodule.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phuocbui.basemodule.BR;
import com.phuocbui.basemodule.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public abstract class BaseDialogFragment<VDB extends ViewDataBinding> extends DialogFragment {

    protected VDB binder;
    public View.OnClickListener positiveClick = v -> dismiss();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle();
    }

    protected void setStyle() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_MVVMDemo_Dialog);
    }

    protected void setBackground(@DrawableRes int drawableId) {
        binder.getRoot().setBackground(getContext().getDrawable(drawableId));
    }

    @LayoutRes
    protected abstract int getLayoutId();

    @IdRes
    protected int getVariableId() {
        return BR.dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        binder.setVariable(getVariableId(), this);
        setBackground(R.drawable.bg_rounded_corner_white);
        return binder.getRoot();
    }

    public void show(FragmentManager manager, String tag, View.OnClickListener positiveClick) {
        if (positiveClick != null) this.positiveClick = positiveClick;
        super.show(manager, tag);
    }


}
