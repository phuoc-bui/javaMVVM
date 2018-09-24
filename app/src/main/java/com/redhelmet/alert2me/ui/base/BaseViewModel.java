package com.redhelmet.alert2me.ui.base;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.AddObservationModel;
import com.redhelmet.alert2me.data.model.Observations;
import com.redhelmet.alert2me.global.Event;

import org.json.JSONArray;

import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    protected boolean isLoading = false;
    public JSONArray wz_notification_selection;
    protected Observations observations;
    protected AddObservationModel addObservation;
    protected DataManager dataManager;
    protected MutableLiveData<Event<Object>> navigationEvent = new MutableLiveData<>();

    public BaseViewModel(DataManager dataManager) {
        this.dataManager = dataManager;
        wz_notification_selection=new JSONArray();
        observations= Observations.getInstance();
        addObservation = AddObservationModel.getInstance();

        //TODO: improve navigationEvent with custom event data (maybe an enum with type of Intent)
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
