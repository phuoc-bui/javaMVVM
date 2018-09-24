package com.redhelmet.alert2me.data;

import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterResponse;

import java.util.List;

import io.reactivex.Observable;

public interface DataManager {
    void saveConfig(ConfigResponse config);
    Observable<ConfigResponse> loadConfig();
    ConfigResponse getConfig();
    List<Hint> getHintData();
    void setInitialLaunch(boolean isInitial);
    boolean getInitialLaunch();
    void setAccepted(boolean accepted);
    boolean getAccepted();
    Observable<RegisterResponse> getUserId(String firebaseToken);
}
