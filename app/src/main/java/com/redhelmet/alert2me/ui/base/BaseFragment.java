package com.redhelmet.alert2me.ui.base;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redhelmet.alert2me.BR;
import com.redhelmet.alert2me.ViewModelFactory;
import com.redhelmet.alert2me.domain.ExceptionHandler;

public abstract class BaseFragment<VM extends BaseViewModel, VDB extends ViewDataBinding> extends Fragment{
    protected VM viewModel;
    protected VDB binder;

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract Class<VM> obtainViewModel();

    protected int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binder = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binder.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(obtainViewModel());
        binder.setVariable(getBindingVariable(), viewModel);
        binder.executePendingBindings();
    }
}
