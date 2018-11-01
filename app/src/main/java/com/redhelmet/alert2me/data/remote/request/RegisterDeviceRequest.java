package com.redhelmet.alert2me.data.remote.request;

import android.os.Build;

import com.google.firebase.iid.FirebaseInstanceId;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.util.DeviceUtil;

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
        systemVersion = DeviceUtil.getOsVersion();
        model = DeviceUtil.getDeviceName();
        deviceToken = firebaseToken;
    }
}
