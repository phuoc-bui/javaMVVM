package com.redhelmet.alert2me.ui.help;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentHelpBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

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
