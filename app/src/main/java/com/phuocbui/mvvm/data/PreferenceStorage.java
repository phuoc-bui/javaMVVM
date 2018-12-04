package com.phuocbui.mvvm.data;

import com.phuocbui.mvvm.data.model.DeviceInfo;
import com.phuocbui.mvvm.data.model.User;

public interface PreferenceStorage {
    DeviceInfo getDeviceInfo();

    void setLoggedIn(boolean isLoggedIn);

    User getCurrentUser();

    boolean isProximityEnabled();

    void setProximityEnabled(boolean enabled);
}
