package com.redhelmet.alert2me.data.local.pref;

import com.redhelmet.alert2me.data.model.response.ConfigResponse;

public interface PreferenceHelper {
    void saveConfig(ConfigResponse configResponse);
    ConfigResponse getConfig();
    boolean isInitialLaunch();
    void setInitialLaunch(boolean isInitial);
    boolean isAccepted();
    void setAccepted(boolean accepted);
}
