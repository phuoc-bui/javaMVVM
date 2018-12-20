package com.phuocbui.basemodule.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.phuocbui.basemodule.R;
import com.phuocbui.basemodule.data.DataManager;
import com.phuocbui.basemodule.data.preference.PreferenceStorage;
import com.phuocbui.basemodule.data.remote.NetworkError;
import com.phuocbui.basemodule.global.Event;
import com.phuocbui.basemodule.global.NavigationItem;
import com.phuocbui.basemodule.global.ResourceProvider;
import com.phuocbui.basemodule.global.RetrofitException;
import com.phuocbui.basemodule.global.RxProperty;

import java.io.Serializable;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class BaseViewModel extends ViewModel implements Destroyable, Serializable {

    protected final String TAG = this.getClass().getSimpleName();

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    protected RxProperty<Boolean> isLoading = new RxProperty<>();
    MutableLiveData<Event<NavigationItem>> navigationEvent = new MutableLiveData<>();

    protected DataManager dataManager;

    protected PreferenceStorage preferenceStorage;

    protected ResourceProvider resourceProvider;

    public BaseViewModel() {
        this(null, null, null);
    }

    public BaseViewModel(DataManager dataManager) {
        this(dataManager, null, null);
    }

    public BaseViewModel(PreferenceStorage pref) {
        this(null, pref, null);
    }

    public BaseViewModel(DataManager dataManager, ResourceProvider resourceProvider) {
        this(dataManager, null, resourceProvider);
    }

    public BaseViewModel(PreferenceStorage pref, ResourceProvider resourceProvider) {
        this(null, pref, resourceProvider);
    }

    public BaseViewModel(DataManager dataManager, PreferenceStorage pref) {
        this(dataManager, pref, null);
    }

    public BaseViewModel(DataManager dataManager, PreferenceStorage pref, ResourceProvider resourceProvider) {
        this.dataManager = dataManager;
        this.preferenceStorage = pref;
        this.resourceProvider = resourceProvider;
    }

    /**
     * Handle network error. If want to handle session expired, should override handleSessionExpired methods, eg: logout,..
     *
     * @param error : network error
     */
    protected void handleError(Throwable error) {
        if (error == null) return;
        if (error instanceof RetrofitException) {
            switch (((RetrofitException) error).getKind()) {
                case HTTP:
                    NetworkError errorData = ((RetrofitException) error).getErrorData();
                    String message = (errorData == null || errorData.errorMessage == null) ? error.getMessage() : errorData.errorMessage;
                    showToast(message);
                    return;
                case HTTP_403:
                    showToast(R.string.session_expired);
                    handleSessionExpired();
                    return;
                case NETWORK:
                    showToast(R.string.no_internet);
                    return;
                case JSON_SYNTAX:
                case UNEXPECTED:
                    showToast(R.string.time_out);
                default:
                    showToast(R.string.time_out);
            }
        } else {
            showToast(error.getMessage());
            Timber.d(error);
        }
    }

    protected void handleSessionExpired() {

    }

    private void navigateTo(NavigationItem item) {
        navigationEvent.setValue(new Event<>(item));
    }

    protected void startActivity(Class clazz) {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, clazz));
    }

    protected void startActivity(Intent intent) {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, intent));
    }

    protected void startActivity(Class clazz, Bundle data) {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, clazz, data));
    }

    protected void startActivity(Class clazz, boolean clearTask) {
        navigateTo(new NavigationItem(clearTask ? NavigationItem.START_ACTIVITY_AND_CLEAR_TASK :
                NavigationItem.START_ACTIVITY, clazz));
    }

    protected void startActivity(Class clazz, Bundle data, boolean clearTask) {
        navigateTo(new NavigationItem(clearTask ? NavigationItem.START_ACTIVITY_AND_CLEAR_TASK :
                NavigationItem.START_ACTIVITY, clazz, data));
    }

    protected void startActivityForResult(Class clazz, int requestCode) {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY_FOR_RESULT, clazz, requestCode));
    }

    protected void startActivityForResult(Class clazz, Bundle data, int requestCode) {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY_FOR_RESULT, clazz, requestCode, data));
    }

    protected void finishActivity() {
        navigateTo(new NavigationItem(NavigationItem.FINISH));
    }

    protected void finishActivity(Intent data) {
        navigateTo(new NavigationItem(NavigationItem.FINISH_AND_RETURN, data));
    }

    protected void startWebview(String url) {
        navigateTo(new NavigationItem(NavigationItem.START_WEB_VIEW, url));
    }

    protected void showToast(String message) {
        navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, message));
    }

    protected void showToast(@StringRes int messageId) {
        navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, messageId));
    }

    protected void changeFragment(Fragment fragment) {
        changeFragment(fragment, false, false);
    }

    protected void changeFragmentAndClearTask(Fragment fragment) {
        changeFragment(fragment, true, false);
    }

    protected void changeFragment(Fragment fragment, boolean addToTask) {
        changeFragment(fragment, false, addToTask);
    }

    protected void changeFragment(Fragment fragment, boolean clearTask, boolean addToTask) {
        int type = NavigationItem.CHANGE_FRAGMENT;
        if (clearTask) type = NavigationItem.CHANGE_FRAGMENT_AND_CLEAR_BACK_STACK;
        else if (addToTask) type = NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK;
        navigateTo(new NavigationItem(type, fragment));
    }

    protected void popFragmentBack() {
        navigateTo(new NavigationItem(NavigationItem.POP_FRAGMENT_BACK));
    }

    protected void showLoadingDialog() {
        navigateTo(new NavigationItem(NavigationItem.SHOW_LOADING_DIALOG));
    }

    protected void dismissLoadingDialog() {
        navigateTo(new NavigationItem(NavigationItem.DISMISS_LOADING_DIALOG));
    }

    protected void makeDial(String phoneNumber) {
        navigateTo(new NavigationItem(NavigationItem.DIAL, phoneNumber));
    }

    protected void makeDial(@StringRes int phoneNumberId) {
        navigateTo(new NavigationItem(NavigationItem.DIAL, phoneNumberId));
    }

    protected void showDialog(BaseDialogFragment dialog) {
        navigateTo(new NavigationItem(NavigationItem.SHOW_DIALOG, dialog));
    }

    protected void showDialog(BaseDialogFragment dialog, View.OnClickListener positiveClickListener) {
        navigateTo(new NavigationItem(NavigationItem.SHOW_DIALOG, dialog, positiveClickListener));
    }

    protected void showDialog(BaseDialogFragment dialog, String tag) {
        navigateTo(new NavigationItem(NavigationItem.SHOW_DIALOG, dialog, tag));
    }

    protected void showDialog(BaseDialogFragment dialog, String tag, View.OnClickListener positiveClickListener) {
        navigateTo(new NavigationItem(NavigationItem.SHOW_DIALOG, dialog, tag, positiveClickListener));
    }

    protected void dismissDialog(String tag) {
        navigateTo(new NavigationItem(NavigationItem.DISMISS_DIALOG, tag));
    }

    protected void dismissDialog(BaseDialogFragment dialog) {
        navigateTo(new NavigationItem(NavigationItem.DISMISS_DIALOG, dialog));
    }

    protected void dismissDialog() {
        navigateTo(new NavigationItem(NavigationItem.DISMISS_DIALOG));
    }

    @Override
    protected void onCleared() {
        disposeBag.dispose();
        super.onCleared();
    }

    @Override
    public void onDestroy() {
        onCleared();
    }
}
