
package com.redhelmet.alert2me.ui.splash;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.ActivitySplashScreenBinding;
import com.redhelmet.alert2me.domain.ExceptionHandler;
import com.redhelmet.alert2me.ui.base.BaseActivity;

public class SplashScreen extends BaseActivity<SplashViewModel, ActivitySplashScreenBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_screen;
    }

    @Override
    protected Class<SplashViewModel> obtainViewModel() {
        return SplashViewModel.class;
    }

    @Override
    protected void configWindow() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }
}