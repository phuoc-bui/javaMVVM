package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.model.response.ConfigResponse;

import org.json.JSONObject;

import io.reactivex.Observable;

public interface ApiHelper {
    Observable<ConfigResponse> getConfig();
}
