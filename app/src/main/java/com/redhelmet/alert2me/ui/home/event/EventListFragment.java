package com.redhelmet.alert2me.ui.home.event;

import android.os.Bundle;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentEventListBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

public class EventListFragment extends BaseFragment<EventViewModel, FragmentEventListBinding> {
    private static final String VIEW_MODEL_KEY = "viewModel";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_list;
    }

    @Override
    protected Class<EventViewModel> getViewModelClass() {
        return EventViewModel.class;
    }

    @Override
    protected EventViewModel obtainViewModel() {
        if (getArguments() != null) {
            Object data = getArguments().getSerializable(VIEW_MODEL_KEY);
            if (data instanceof EventViewModel) {
                return (EventViewModel) data;
            } else {
                throw new Error("viewModel must is not null");
            }
        } else {
            throw new Error("viewModel must is not null");
        }
    }

    public static EventListFragment newInstance(EventViewModel viewModel) {
        EventListFragment fragment = new EventListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIEW_MODEL_KEY, viewModel);
        fragment.setArguments(bundle);
        return fragment;
    }
}
