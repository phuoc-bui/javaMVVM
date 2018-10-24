package com.redhelmet.alert2me.ui.base;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redhelmet.alert2me.BR;
import com.redhelmet.alert2me.domain.ExceptionHandler;

import dagger.android.support.DaggerFragment;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseFragment<VM extends BaseViewModel, VDB extends ViewDataBinding> extends DaggerFragment implements NavigationFragment {

    protected static final String TAG = BaseFragment.class.getSimpleName();

    protected VM viewModel;
    protected VDB binder;

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    @LayoutRes
    protected abstract int getLayoutId();

    protected int getBindingVariable() {
        return BR.viewModel;
    }

    private BaseActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            activity = (BaseActivity) context;
        }
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

    }

    @SuppressWarnings("unchecked")
    protected void obtainViewModel(ViewModelProvider.Factory factory, Class<VM> vmClass) {
        viewModel = ViewModelProviders.of(getBaseActivity(), factory).get(vmClass);
        binder.setVariable(getBindingVariable(), viewModel);
        binder.executePendingBindings();

        viewModel.navigationEvent.observe(this, event -> {
            if (event != null) {
                onNavigationEvent(event.getContentIfNotHandled());
            }
        });
    }

    public BaseActivity getBaseActivity() {
        return activity;
    }

    public VM getViewModel() {
        return viewModel;
    }

    /**
     * Handle navigation event from viewModel, modified this function if any custom navigation
     * such as add bundle, flag, animation, ...
     *
     * @param item type of navigation (with data from viewModel)
     */
    protected void onNavigationEvent(@Nullable NavigationItem item) {
        if (item != null) {
            item.navigation(getBaseActivity());
        }
    }

    @Override
    public void onDestroy() {
        disposeBag.dispose();
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onUserInteraction() {

    }
}