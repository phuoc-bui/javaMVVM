package com.redhelmet.alert2me.ui.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.data.model.AddObservationModel;
import com.redhelmet.alert2me.data.model.Observations;
import com.redhelmet.alert2me.data.remote.NetworkError;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.global.RetrofitException;
import com.redhelmet.alert2me.global.RxProperty;

import org.json.JSONArray;

import io.reactivex.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected DataManager dataManager;

    protected PreferenceStorage preferenceStorage;

    protected CompositeDisposable disposeBag = new CompositeDisposable();

    public RxProperty<Boolean> isLoading = new RxProperty<>();
    public JSONArray wz_notification_selection;
    protected Observations observations;
    protected AddObservationModel addObservation;
    protected MutableLiveData<Event<NavigationItem>> navigationEvent = new MutableLiveData<>();

    public BaseViewModel() {
        this(null, null);
    }

    public BaseViewModel(DataManager dataManager) {
        this(dataManager, null);
    }

    public BaseViewModel(PreferenceStorage pref) {
        this(null, pref);
    }

    public BaseViewModel(DataManager dataManager, PreferenceStorage pref) {
        wz_notification_selection = new JSONArray();
        observations = Observations.getInstance();
        addObservation = AddObservationModel.getInstance();
        this.dataManager = dataManager;
        this.preferenceStorage = pref;
    }

    protected void showLoadingDialog(boolean show) {
        NavigationItem item = new NavigationItem(show ? NavigationItem.SHOW_LOADING_DIALOG : NavigationItem.DISMISS_LOADING_DIALOG);
        navigateTo(item);
    }

    protected void handleError(Throwable error) {
        Object message = null;
        if (error instanceof RetrofitException) {
            switch (((RetrofitException) error).getKind()) {
                case HTTP_422_WITH_DATA:
                case HTTP:
                    NetworkError errorData = ((RetrofitException) error).getErrorData();
                    message = errorData == null ? error.getMessage() : errorData.errorMessage;
                    break;
                case NETWORK:
                    message = R.string.noInternet;
                    break;
                case JSON_SYNTAX:
                case UNEXPECTED:
                    message = R.string.timeOut;
                    break;
                default:
                    message = R.string.timeOut;
            }
        }
        navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, message));
    }

    protected void navigateTo(NavigationItem item) {
        navigationEvent.setValue(new Event<>(item));
    }

    @Override
    protected void onCleared() {
        disposeBag.dispose();
        super.onCleared();
    }
}
