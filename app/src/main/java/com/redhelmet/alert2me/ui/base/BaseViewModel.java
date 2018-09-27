package com.redhelmet.alert2me.ui.base;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.AddObservationModel;
import com.redhelmet.alert2me.data.model.Observations;
import com.redhelmet.alert2me.global.Event;

import org.json.JSONArray;

import java.io.Serializable;

import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel implements Serializable{

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    public boolean isLoading = false;
    public JSONArray wz_notification_selection;
    protected Observations observations;
    protected AddObservationModel addObservation;
    public DataManager dataManager;
    protected MutableLiveData<Event<NavigationType>> navigationEvent = new MutableLiveData<>();

    public BaseViewModel(DataManager dataManager) {
        this.dataManager = dataManager;
        wz_notification_selection=new JSONArray();
        observations= Observations.getInstance();
        addObservation = AddObservationModel.getInstance();
    }

    @Override
    protected void onCleared() {
        disposeBag.clear();
        super.onCleared();
    }
}
