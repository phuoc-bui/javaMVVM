package com.redhelmet.alert2me.ui.home.event;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.EmptyListRecyclerAdapter;
import com.redhelmet.alert2me.adapters.EventListRecyclerAdapter;
import com.redhelmet.alert2me.databinding.FragmentEventListBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import static com.redhelmet.alert2me.core.CoreFunctions._context;

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

    private void SetEventListDataSource() {

        SortList();

        if (listEventIcon != null) {
            if (_events.size() > 0) {
                mAdapter = new EventListRecyclerAdapter(getActivity(), _events, false);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                listEventIcon.setLayoutManager(mLayoutManager);
                listEventIcon.setItemAnimator(new DefaultItemAnimator());
                listEventIcon.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {


                String emptyText = _context.getString(R.string.no_data_to_display);

                EmptyListRecyclerAdapter emptyListRecyclerAdapter = new EmptyListRecyclerAdapter(emptyText);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                listEventIcon.setLayoutManager(mLayoutManager);
                listEventIcon.setItemAnimator(new DefaultItemAnimator());
                listEventIcon.setAdapter(emptyListRecyclerAdapter);
                emptyListRecyclerAdapter.notifyDataSetChanged();
            }
            mProgress.setVisibility(View.INVISIBLE);
        }
    }
}
