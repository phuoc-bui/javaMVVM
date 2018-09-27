package com.redhelmet.alert2me.ui.home.event;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.EventListRecyclerAdapter;
import com.redhelmet.alert2me.databinding.FragmentEventListBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

public class EventListFragment extends BaseFragment<EventListViewModel, FragmentEventListBinding> implements SwipeRefreshLayout.OnRefreshListener {
    private static final String VIEW_MODEL_KEY = "viewModel";

    RecyclerView listEventIcon;
    EventListRecyclerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_list;
    }

    @Override
    protected Class<EventListViewModel> getViewModelClass() {
        return EventListViewModel.class;
    }

    @Override
    protected EventListViewModel obtainViewModel() {
        Object data = getArguments().getSerializable(VIEW_MODEL_KEY);
        if (data instanceof EventListViewModel) {
            return (EventListViewModel) data;
        } else {
            throw new Error("viewModel must is not null");
        }
    }

    public static EventListFragment newInstance(EventListViewModel viewModel) {
        EventListFragment fragment = new EventListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIEW_MODEL_KEY, viewModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onRefresh() {

    }
}
