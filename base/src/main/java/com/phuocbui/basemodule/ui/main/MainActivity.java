package com.phuocbui.basemodule.ui.main;

import android.os.Bundle;

import com.phuocbui.basemodule.R;
import com.phuocbui.basemodule.databinding.ActivityMainBinding;
import com.phuocbui.basemodule.ui.base.BaseActivity;

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
