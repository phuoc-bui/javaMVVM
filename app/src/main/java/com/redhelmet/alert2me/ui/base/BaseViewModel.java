package com.redhelmet.alert2me.ui.base;

import android.arch.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    protected boolean isLoading = false;

    @Override
    protected void onCleared() {
        disposeBag.clear();
        super.onCleared();
    }
}
