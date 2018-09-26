package com.redhelmet.alert2me.data;

import android.util.Log;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.data.local.database.DBHelper;
import com.redhelmet.alert2me.data.local.pref.PreferenceHelper;
import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.data.remote.ApiHelper;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterResponse;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class AppDataManager implements DataManager {
    private final String TAG = AppDataManager.class.getSimpleName();
    private PreferenceHelper pref;
    private ApiHelper api;
    private DBHelper database;
    private ConfigResponse config;

    public AppDataManager(PreferenceHelper pref, DBHelper db, ApiHelper apiHelper) {
        this.pref = pref;
        this.database = db;
        this.api = apiHelper;
    }

    @Override
    public void saveConfig(ConfigResponse config) {
        this.config = config;
        pref.saveConfig(config);
        database.saveConfig(config);
    }

    @Override
    public Observable<ConfigResponse> loadConfig() {
        return Observable.concatArrayEager(
                Observable.just(pref.getConfig()),
                api.getConfig()
                        .doOnNext(this::saveConfig)
                        .doOnError(this::handleError))
                .debounce(400L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public ConfigResponse getConfig() {
        return config;
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
    public Observable<RegisterResponse> getUserId(String firebaseToken) {
        return api.registerDevice(firebaseToken)
                .doOnNext(registerResponse -> pref.saveDeviceInfo(registerResponse.device))
                .doOnError(error -> {
                    RegisterResponse.Device device = new RegisterResponse.Device();
                    device.id = "0";
                    device.apiToken = "";
                    pref.saveDeviceInfo(device);
                });
    }

    @Override
    public Observable<ProximityLocationResponse> putProximityLocation(double lat, double lng) {
        ProximityLocationRequest request = new ProximityLocationRequest(lat, lng);
        String userId = pref.getDeviceInfo().id;
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
    public List<Hint> getHintData() {
        //TODO: need confirm use hints from server or local hints (should use local hints for performance when load big image)
//        List<Hint> hints = config.appConfig.getHintsScreen();
        List<Hint> hints = null;
        if (hints == null || hints.isEmpty()) {
            //setting up hard coded hints with desc and image
            hints = new ArrayList<>();
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
        }
        return hints;
    }

    private void handleError(Throwable error) {
        Log.e(TAG, error.getMessage());
    }
}
