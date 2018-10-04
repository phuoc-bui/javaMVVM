package com.redhelmet.alert2me.ui.base;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.AddObservationModel;
import com.redhelmet.alert2me.data.model.Observations;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.global.RxProperty;

import org.json.JSONArray;

import java.io.Serializable;

import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    public RxProperty<Boolean> isLoading = new RxProperty<>();
    public JSONArray wz_notification_selection;
    protected Observations observations;
    protected AddObservationModel addObservation;
    protected MutableLiveData<Event<NavigationItem>> navigationEvent = new MutableLiveData<>();
    protected DataManager dataManager;

    public BaseViewModel(DataManager dataManager) {
        wz_notification_selection = new JSONArray();
        observations = Observations.getInstance();
        addObservation = AddObservationModel.getInstance();
        this.dataManager = dataManager;
    }

    public BaseViewModel() {
        wz_notification_selection = new JSONArray();
        observations = Observations.getInstance();
        addObservation = AddObservationModel.getInstance();
    }

    @Override
    protected void onCleared() {
        disposeBag.dispose();
        super.onCleared();
    }
}
