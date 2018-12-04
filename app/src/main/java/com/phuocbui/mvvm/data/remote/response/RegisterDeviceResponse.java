package com.phuocbui.mvvm.data.remote.response;

import com.google.gson.annotations.SerializedName;
import com.phuocbui.mvvm.data.model.DeviceInfo;

public class RegisterDeviceResponse extends Response {
    @SerializedName("device")
    public DeviceInfo apiInfo;
}