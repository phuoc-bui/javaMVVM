package com.redhelmet.alert2me.data.local.database;

import com.redhelmet.alert2me.data.remote.response.ConfigResponse;

public interface DBHelper {
    void saveConfig(ConfigResponse response);
}
