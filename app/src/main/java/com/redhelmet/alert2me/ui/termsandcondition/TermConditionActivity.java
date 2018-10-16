package com.redhelmet.alert2me.ui.termsandcondition;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.ActivityMainBinding;
import com.redhelmet.alert2me.ui.base.BaseActivity;

import javax.inject.Inject;

/**
 * Created by inbox on 13/11/17.
 */

public class TermConditionActivity extends BaseActivity<TermsConditionViewModel, ActivityMainBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void configWindow() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainViewModel(factory, TermsConditionViewModel.class);
    }
}
