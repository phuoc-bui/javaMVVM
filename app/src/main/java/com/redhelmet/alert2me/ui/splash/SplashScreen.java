
package com.redhelmet.alert2me.ui.splash;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.ActivitySplashScreenBinding;
import com.redhelmet.alert2me.ui.base.BaseActivity;

import javax.inject.Inject;

public class SplashScreen extends BaseActivity<SplashViewModel, ActivitySplashScreenBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_screen;
    }

    @Override
    protected void configWindow() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        obtainViewModel(factory, SplashViewModel.class);
    }
}