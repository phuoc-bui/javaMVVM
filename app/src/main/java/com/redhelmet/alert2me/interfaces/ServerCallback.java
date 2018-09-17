package com.redhelmet.alert2me.interfaces;

import org.json.JSONObject;

public interface ServerCallback{
    void onSuccess(boolean result);

    void onSuccess(JSONObject response);
}