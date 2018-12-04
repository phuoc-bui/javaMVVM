package com.phuocbui.mvvm.ui.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.FragmentEventListBinding;
import com.phuocbui.basemodule.ui.base.BaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class EventListFragment extends BaseFragment<EventViewModel, FragmentEventListBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_list;
    }

    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, EventViewModel.class);
        viewModel.isRefreshing.observe(this, isRefresh -> binder.swipeContainer.setRefreshing(isRefresh));
    }
}
