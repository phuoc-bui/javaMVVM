package com.redhelmet.alert2me.ui.help;

import android.os.Bundle;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentEditProfileFieldBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class EditProfileFieldFragment extends BaseFragment<HelpViewModel, FragmentEditProfileFieldBinding> {

    public static final String EXTRA_HINT = "extra-hint";

    @Inject
    ViewModelProvider.Factory factory;

    private int hintId;

    public static EditProfileFieldFragment newInstance(int hintId) {
        EditProfileFieldFragment fragment = new EditProfileFieldFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_HINT, hintId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public boolean onBackPressed() {
        viewModel.userModel.rollbackToOrigin();
        return super.onBackPressed();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_profile_field;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, HelpViewModel.class);
        Bundle bundle = getArguments();
        if (bundle != null) {
            hintId = bundle.getInt(EXTRA_HINT);
            binder.setHintId(hintId);
            binder.setObservable(viewModel.userModel.getObservable(hintId));
        }
    }
}
