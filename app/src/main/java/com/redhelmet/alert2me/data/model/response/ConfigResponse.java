package com.redhelmet.alert2me.data.model.response;

import com.google.gson.annotations.SerializedName;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;

import java.util.List;

public class ConfigResponse {

    public boolean success;

    @SerializedName("app")
    public AppConfig appConfig;

    @SerializedName("category")
    public List<Category> categories;

    public String errorMessage;
}
