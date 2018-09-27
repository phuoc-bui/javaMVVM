package com.redhelmet.alert2me.data.remote.response;

import com.google.gson.annotations.SerializedName;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

public class ConfigResponse extends Response {

    @SerializedName("app")
    public AppConfig appConfig;

    @SerializedName("category")
    public List<Category> categories;

    @SerializedName("eventGroup")
    public List<EventGroup> eventGroups;
}
