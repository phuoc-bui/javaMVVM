package com.redhelmet.alert2me.data.local.pref;

import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterResponse;

public interface PreferenceHelper {
    void saveConfig(ConfigResponse configResponse);
    ConfigResponse getConfig();
    boolean isInitialLaunch();
    void setInitialLaunch(boolean isInitial);
    boolean isAccepted();
    void setAccepted(boolean accepted);
    void saveDeviceInfo(RegisterResponse.Device device);
    RegisterResponse.Device getDeviceInfo();
}
