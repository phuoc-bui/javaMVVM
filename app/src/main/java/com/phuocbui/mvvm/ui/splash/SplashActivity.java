
package com.phuocbui.mvvm.ui.splash;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.ActivitySplashScreenBinding;
import com.phuocbui.basemodule.ui.base.BaseActivity;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity<SplashViewModel, ActivitySplashScreenBinding> {

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