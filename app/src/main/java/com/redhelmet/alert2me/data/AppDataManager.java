package com.redhelmet.alert2me.data;

import android.util.Log;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.local.database.DBHelper;
import com.redhelmet.alert2me.data.local.pref.PreferenceHelper;
import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.data.remote.ApiHelper;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AppDataManager implements DataManager {
    private final String TAG = AppDataManager.class.getSimpleName();

    private CompositeDisposable disposeBag = new CompositeDisposable();

    private PreferenceHelper pref;
    private ApiHelper api;
    private DBHelper database;

    public AppDataManager(PreferenceHelper pref, DBHelper db, ApiHelper apiHelper) {
        this.pref = pref;
        this.database = db;
        this.api = apiHelper;
    }

    @Override
    public void saveConfig(ConfigResponse config) {

        pref.saveAppConfig(config.appConfig);
        database.saveCategories(config.categories);
        database.saveEventGroups(config.eventGroups);
    }

    @Override
    public Observable<ConfigResponse> loadConfig() {
        return api.getConfig()
                .doOnNext(this::saveConfig)
                .doOnError(this::handleError)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void setInitialLaunch(boolean isInitial) {
        pref.setInitialLaunch(isInitial);
    }

    @Override
    public boolean getInitialLaunch() {
        return pref.isInitialLaunch();
    }

    @Override
    public void setAccepted(boolean accepted) {
        pref.setAccepted(accepted);
    }

    @Override
    public boolean getAccepted() {
        return pref.isAccepted();
    }

    @Override
    public Observable<ApiInfo> getUserId(String firebaseToken) {
        return api.registerDevice(firebaseToken)
                .subscribeOn(Schedulers.io())
                .doOnNext(apiInfo -> pref.saveDeviceInfo(apiInfo))
                .doOnError(error -> {
                    ApiInfo apiInfo = new ApiInfo();
                    apiInfo.setUserId("0");
                    apiInfo.setApiToken("");
                    pref.saveDeviceInfo(apiInfo);
                    handleError(error);
                });
    }

    @Override
    public Observable<ProximityLocationResponse> putProximityLocation(double lat, double lng) {
        ProximityLocationRequest request = new ProximityLocationRequest(lat, lng);
        String userId = pref.getDeviceInfo().getUserId();
        return api.putProximityLocation(userId, request)
                .doOnNext(response -> {
//                    PreferenceUtils.saveToPrefs(getApplicationContext(),
//                            Constants.KEY_LASTUPDATEDUSERLATITUDE,
//                            String.valueOf(
//                                    Double.valueOf((String) PreferenceUtils.getFromPrefs(getApplicationContext(),
//                                            Constants.KEY_USERLATITUDE, "0"))));
//                    PreferenceUtils.saveToPrefs(getApplicationContext(),Constants.KEY_LASTUPDATEDUSERLONGITUDE,String.valueOf(Double.valueOf((String ) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLONGITUDE, "0"))));
//                    appPreferences.put(Constants.PROXIMITY_MOVEMENT, 3);
                });
    }

    @Override
    public Observable<List<Event>> getAllEvents() {
        return api.getAllEvents().subscribeOn(Schedulers.io())
                .doOnError(this::handleError);
    }

    @Override
    public Observable<List<Category>> getCategories() {
        return database.getCategories().subscribeOn(Schedulers.computation());
    }

    @Override
    public Observable<List<EventGroup>> getEventGroups() {
        return database.getEventGroups().subscribeOn(Schedulers.computation());
    }

    @Override
    public Observable<List<Event>> getEventsWithDefaultFilter() {
        List<Event> result = new ArrayList<>();
        disposeBag.add(Observable.combineLatest(getAllEvents().flatMap(Observable::fromIterable),
                getEventGroups().flatMap(eventGroups -> Observable.fromIterable(eventGroups)
                        .flatMap(eventGroup -> Observable.fromIterable(eventGroup.getDisplayFilter()))
                        .flatMap(eventGroupDisplayFilter -> Observable.fromArray(eventGroupDisplayFilter.getLayers()))),
                (event, s) -> {
                    if (s.equals(event.getGroup())) return event;
                    return null;
                }).subscribe(event -> {
            if (event != null) {
                result.add(event);
            }
        }));

        return Observable.just(result);
    }

    @Override
    public Observable<List<Event>> getEventsWithCustomFilter() {
        return null;
    }

    @Override
    public List<Hint> getHintData() {
        List<Hint> hints = new ArrayList<>();

        Hint hint1 = new Hint();
        hint1.setTitle("");
        hint1.setDesc("EmergencyAUS keeps you<br>updated with current<br>emergency information and<br>alerts in Australia.");
        hint1.setUrl(R.drawable.hint1);
        hint1.setLast(true);

        Hint hint2 = new Hint();
        hint2.setTitle("Observation");
        hint2.setDesc("Share what you know<br>share what you see, hear and feel.");
        hint2.setUrl(R.drawable.hint1);

        Hint hint3 = new Hint();
        hint3.setTitle("Watch Zones");
        hint3.setDesc("Monitor the risk all day<br>all night, all year");
        hint3.setUrl(R.drawable.hint2);

        Hint hint4 = new Hint();
        hint4.setTitle("Warning & Incidents");
        hint4.setDesc("Be aware of your environment<br>your risk, your safety");
        hint4.setUrl(R.drawable.hint3);

        hints.add(hint1);
        hints.add(hint2);
        hints.add(hint3);
        hints.add(hint4);
        return hints;
    }

    private void handleError(Throwable error) {
        Log.e(TAG, error.getMessage());
    }
}
