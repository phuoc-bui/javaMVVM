package com.redhelmet.alert2me.ui.event;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.RecyclerTouchListener;
import com.redhelmet.alert2me.databinding.FragmentEventListBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import javax.inject.Inject;

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
        binder.rvEvents.addOnItemTouchListener(new RecyclerTouchListener(getContext(), binder.rvEvents, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                viewModel.onEventClick(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        viewModel.isRefreshing.observe(this, isRefresh -> binder.swipeContainer.setRefreshing(isRefresh));
    }
}
