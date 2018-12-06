package com.phuocbui.basemodule.ui.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.phuocbui.basemodule.BR;
import com.phuocbui.basemodule.R;
import com.phuocbui.basemodule.global.NavigationItem;
import com.phuocbui.basemodule.ui.dialog.LoadingDialog;
import com.phuocbui.basemodule.util.PermissionUtils;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
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

    protected NavigationFragment currentFragment;

    private LoadingDialog loadingDialog;

    @LayoutRes
    protected abstract int getLayoutId();

    protected void configWindow() {
    }

    @IdRes
    protected int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configWindow();
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

    public void showHomeButton(boolean show) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(show);
        }
    }

    public void hideToolbar(boolean isHide) {
        if (getSupportActionBar() != null)
            if (isHide) getSupportActionBar().hide();
            else getSupportActionBar().show();
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
    public void requestPermissionsSafe(String permission, int requestCode, String dialogMessage, String toastMessage) {
        PermissionUtils.requestPermission(this, requestCode, permission, false, dialogMessage, toastMessage);
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

    public void changeFragment(NavigationFragment fragment) {
        changeFragment(fragment, false, false);
    }

    public void changeFragment(NavigationFragment fragment, boolean addToBackStack, boolean clearBackStack) {
        if (fragment instanceof Fragment) {
            hideKeyboard();
            currentFragment = fragment;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (addToBackStack)
                transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right);

            if (clearBackStack)
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            transaction.replace(getFragmentContainer(), (Fragment) fragment);
            if (addToBackStack) transaction.addToBackStack(null);
            transaction.commit();
        }
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
        if (show && !loadingDialog.isAdded()) {
            loadingDialog.show(getSupportFragmentManager(), "loading");
        } else {
            loadingDialog.dismiss();
        }
    }

    public VM getViewModel() {
        return viewModel;
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        super.onBackPressed();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
