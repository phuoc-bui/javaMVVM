package com.phuocbui.mvvm.data.remote.request;

import android.os.Build;

import com.phuocbui.mvvm.BuildConfig;
import com.phuocbui.basemodule.util.DeviceUtils;

public class RegisterDeviceRequest {
    public String platform = "android";
    public String deviceToken;
    public String deviceName = "android";
    public String version;
    public String systemName;
    public String systemVersion;
    public String model;
    public boolean tablet = false;

    public RegisterDeviceRequest(String firebaseToken) {
        version = "v" + BuildConfig.VERSION_NAME;
        systemName = Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName();
        systemVersion = DeviceUtils.getOsVersion();
        model = DeviceUtils.getDeviceName();
        deviceToken = firebaseToken;
    }
}
