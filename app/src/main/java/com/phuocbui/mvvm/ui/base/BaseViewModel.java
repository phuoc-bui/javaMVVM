package com.phuocbui.mvvm.ui.base;

import android.content.Intent;
import android.os.Bundle;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.mvvm.data.PreferenceStorage;
import com.phuocbui.mvvm.data.remote.NetworkError;
import com.phuocbui.mvvm.global.Event;
import com.phuocbui.mvvm.global.NavigationItem;
import com.phuocbui.mvvm.global.ResourceProvider;
import com.phuocbui.mvvm.global.RetrofitException;
import com.phuocbui.mvvm.global.RxProperty;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected final String TAG = this.getClass().getSimpleName();

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    public RxProperty<Boolean> isLoading = new RxProperty<>();
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

    protected void showLoadingDialog(boolean show) {
        NavigationItem item = new NavigationItem(show ? NavigationItem.SHOW_LOADING_DIALOG : NavigationItem.DISMISS_LOADING_DIALOG);
        navigateTo(item);
    }

    /**
     * Handle network error. If want to handle session expired, should override handleSessionExpired methods, eg: logout,..
     *
     * @param error : network error
     */
    protected void handleError(Throwable error) {
        Object message = null;
        if (error instanceof RetrofitException) {
            switch (((RetrofitException) error).getKind()) {
                case HTTP:
                    NetworkError errorData = ((RetrofitException) error).getErrorData();
                    message = (errorData == null || errorData.errorMessage == null) ? error.getMessage() : errorData.errorMessage;
                    break;
                case HTTP_403:
                    showToast(R.string.session_expired);
                    handleSessionExpired();
                    return;
                case NETWORK:
                    message = R.string.no_internet;
                    break;
                case JSON_SYNTAX:
                case UNEXPECTED:
                    message = R.string.time_out;
                    break;
                default:
                    message = R.string.time_out;
            }
        }
        navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, message));
    }

    protected void handleSessionExpired() {

    }

    private void navigateTo(NavigationItem item) {
        navigationEvent.setValue(new Event<>(item));
    }

    protected void startActivity(Class clazz) {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, clazz));
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

    protected void dissmissLoadingDialog() {
        navigateTo(new NavigationItem(NavigationItem.DISMISS_LOADING_DIALOG));
    }

    @Override
    protected void onCleared() {
        disposeBag.dispose();
        super.onCleared();
    }
}
