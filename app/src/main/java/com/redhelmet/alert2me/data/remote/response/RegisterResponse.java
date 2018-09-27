package com.redhelmet.alert2me.data.remote.response;

import com.google.gson.annotations.SerializedName;
import com.redhelmet.alert2me.data.model.ApiInfo;

public class RegisterResponse extends Response {
    @SerializedName("device")
    public ApiInfo apiInfo;
}