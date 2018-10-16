package com.redhelmet.alert2me.ui.base;

import android.annotation.TargetApi;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;

import com.redhelmet.alert2me.BR;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.ui.dialog.LoadingDialog;
import com.redhelmet.alert2me.util.PermissionUtils;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by inbox on 27/11/17.
 */

public abstract class BaseActivity<VM extends BaseViewModel, VDB extends ViewDataBinding> extends DaggerAppCompatActivity {

    public static final String BUNDLE_EXTRA = "BUNDLE_EXTRA";

    protected VM viewModel;
    protected VDB binder;

    private Bundle bundle;

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    Fragment currentFragment;

    private LoadingDialog loadingDialog;

    @LayoutRes
    protected abstract int getLayoutId();

    protected void configWindow() {
    }

    protected int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configWindow();
//        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        binder = DataBindingUtil.setContentView(this, getLayoutId());
        bundle = getIntent().getBundleExtra(BUNDLE_EXTRA);
        loadingDialog = new LoadingDialog();
    }

    @SuppressWarnings("unchecked")
    protected void obtainViewModel(ViewModelProvider.Factory factory, Class<VM> clazz) {
        viewModel = ViewModelProviders.of(this, factory).get(clazz);
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
     * @param item type of navigation (with data from viewModel)
     */
    protected void onNavigationEvent(NavigationItem item) {
        item.navigation(this);
    }

    protected Bundle getBundle() {
        return bundle;
    }

    @Override
    protected void onStart() {
        super.onStart();
        binder.setVariable(getBindingVariable(), viewModel);
        binder.executePendingBindings();
    }

    public void updateToolbarTitle(String heading) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setTitle(heading);
        }
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

    @Override
    protected void onDestroy() {
        disposeBag.dispose();
        super.onDestroy();
    }

    @IdRes
    protected int getFragmentContainer() {
        throw new Error("Activity should override this method to support change fragment");
    }

    public void changeFragment(Fragment fragment) {
        currentFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(getFragmentContainer(), fragment);
        transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void popBack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
            invalidateOptionsMenu();
        } else finish();
    }

    public void showDialog(DialogFragment dialog, String tag) {
        dialog.show(getSupportFragmentManager(), tag);
    }

    public void dismissDialog(String tag) {
        DialogFragment dialog = (DialogFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void showLoadingDialog(boolean show) {
        if (show) {
            loadingDialog.show(getSupportFragmentManager(), "loading");
        } else {
            loadingDialog.dismiss();
        }
    }
}
