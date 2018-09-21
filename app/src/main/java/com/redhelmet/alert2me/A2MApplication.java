package com.redhelmet.alert2me;

import android.app.Application;

import com.redhelmet.alert2me.data.DataManager;

public class A2MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
       AppModule.init(this);
    }
}
