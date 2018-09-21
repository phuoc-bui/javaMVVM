package com.redhelmet.alert2me.data.local.database;

import com.redhelmet.alert2me.data.model.response.ConfigResponse;

public interface DBHelper {
    void saveConfig(ConfigResponse response);
}
