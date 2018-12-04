package com.phuocbui.mvvm.ui.help;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.FragmentEditPasswordBinding;
import com.phuocbui.basemodule.ui.base.BaseFragment;

import javax.inject.Inject;

public class EditPasswordFragment extends BaseFragment<HelpViewModel, FragmentEditPasswordBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_password;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, HelpViewModel.class);
    }
}
