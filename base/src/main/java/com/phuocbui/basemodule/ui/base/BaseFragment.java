package com.phuocbui.basemodule.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phuocbui.basemodule.BR;
import com.phuocbui.basemodule.global.NavigationItem;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseFragment<VM extends BaseViewModel, VDB extends ViewDataBinding> extends DaggerFragment implements NavigationFragment {

    protected static final String TAG = BaseFragment.class.getSimpleName();

    protected VM viewModel;
    protected VDB binder;

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    @LayoutRes
    protected abstract int getLayoutId();

    @IdRes
    protected int getBindingVariable() {
        return BR.viewModel;
    }

    private BaseActivity activity;

    private FragmentCallback fragmentCallback;

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

        if (fragmentCallback != null) fragmentCallback.onObtainedViewModel();
    }

    public BaseActivity getBaseActivity() {
        return activity;
    }

    public VM getViewModel() {
        return viewModel;
    }

    public void setFragmentCallback(FragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
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

    protected int getColor(@ColorRes int colorId) {
        return getBaseActivity().getResources().getColor(colorId);
    }

    public interface FragmentCallback {
        void onObtainedViewModel();
    }
}