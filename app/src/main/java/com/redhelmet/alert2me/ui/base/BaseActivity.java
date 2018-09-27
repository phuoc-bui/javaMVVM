package com.redhelmet.alert2me.ui.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.redhelmet.alert2me.BR;
import com.redhelmet.alert2me.ViewModelFactory;
import com.redhelmet.alert2me.domain.ExceptionHandler;
import com.redhelmet.alert2me.util.PermissionUtils;

/**
 * Created by inbox on 27/11/17.
 */

public abstract class BaseActivity<VM extends BaseViewModel, VDB extends ViewDataBinding> extends AppCompatActivity {

    protected VM viewModel;
    protected VDB binder;

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract Class<VM> obtainViewModel();

    protected void configWindow() {
    }

    protected int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configWindow();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        binder = DataBindingUtil.setContentView(this, getLayoutId());
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(obtainViewModel());
        viewModel.navigationEvent.observe(this, event -> {
            if (event != null) {
                onNavigationEvent(event.getContentIfNotHandled());
            }
        });
    }

    /**
     * Handle navigation event from viewModel, modified this function if any custom navigation
     * such as add bundle, flag, animation, ...
     *
     * @param type type of navigation (with data from viewModel)
     */
    protected void onNavigationEvent(NavigationType type) {
        type.navigation(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        binder.setVariable(getBindingVariable(), viewModel);
        binder.executePendingBindings();
    }

    public boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED;
    }

    public boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                String permission) {
        return PermissionUtils.isPermissionGranted(grantPermissions, grantResults, permission);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafe(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(permissions, requestCode);
    }
}
