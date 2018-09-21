package com.redhelmet.alert2me.ui.base;

import android.arch.lifecycle.ViewModel;
import android.support.design.widget.Snackbar;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.AddObservationModel;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Observations;

import org.json.JSONArray;

import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    protected boolean isLoading = false;
    public JSONArray wz_notification_selection;
    protected AppConfig appConfig;
    Snackbar snackbar=null;
    Observations observations;
    AddObservationModel addObservation;
    private DataManager dataManager;

    protected BaseViewModel(DataManager dataManager) {
        this.dataManager = dataManager;
        wz_notification_selection=new JSONArray();
        appConfig = AppConfig.getInstance();
        observations= Observations.getInstance();
        addObservation = AddObservationModel.getInstance();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    protected void onCleared() {
        disposeBag.clear();
        super.onCleared();
    }
}
