package com.redhelmet.alert2me.data.model;

import com.google.gson.annotations.SerializedName;
import com.redhelmet.alert2me.data.model.base.Model;

public class ApiInfo implements Model {
    @SerializedName("id")
    private String userId;
    private String apiToken;

    public long getId() {
        return 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
