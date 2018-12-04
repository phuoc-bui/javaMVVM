package com.phuocbui.mvvm.data.remote.response;

import com.google.gson.annotations.SerializedName;
import com.phuocbui.mvvm.data.model.AppConfig;
import com.phuocbui.mvvm.data.model.Category;
import com.phuocbui.mvvm.data.model.EventGroup;

import java.util.List;

public class ConfigResponse extends Response {

    @SerializedName("app")
    public AppConfig appConfig;

    @SerializedName("category")
    public List<Category> categories;

    @SerializedName("eventGroup")
    public List<EventGroup> eventGroups;
}
