package com.phuocbui.mvvm.data.remote;

import com.google.gson.annotations.SerializedName;

public class NetworkError {
    public boolean success;
    public String name;
    public String created;
    @SerializedName("errorMessage")
    public String errorMessage;
}
