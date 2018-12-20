package com.phuocbui.basemodule.data.remote.response;

import com.google.gson.annotations.SerializedName;

public class Response<T> {
    public T data;
    @SerializedName("error_message")
    public String errorMessage;
    public int status;
}
