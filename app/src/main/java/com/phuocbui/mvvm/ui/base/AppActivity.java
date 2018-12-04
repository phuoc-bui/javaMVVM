package com.phuocbui.mvvm.ui.base;

import androidx.databinding.ViewDataBinding;

public abstract class AppActivity<VM extends AppViewModel, VDB extends ViewDataBinding> extends com.phuocbui.basemodule.ui.base.BaseActivity<VM, VDB> {
    @Override
    protected int getBindingVariable() {
        return BR.viewModel;
    }
}
