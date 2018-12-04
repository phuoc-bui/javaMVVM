package com.phuocbui.mvvm.ui.help;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.FragmentHelpBinding;
import com.phuocbui.basemodule.ui.base.BaseFragment;

import javax.inject.Inject;


public class HelpFragment extends BaseFragment<HelpViewModel, FragmentHelpBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_help;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, HelpViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        // prevent show toolbar when back from profile fragment
        getBaseActivity().hideToolbar(true);
    }
}
