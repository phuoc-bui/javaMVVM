package com.redhelmet.alert2me.ui.watchzone;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.RecyclerTouchListener;
import com.redhelmet.alert2me.databinding.FragmentStaticWatchZoneBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import javax.inject.Inject;

public class StaticWatchZoneFragment extends BaseFragment<WatchZoneViewModel, FragmentStaticWatchZoneBinding> {
    @Inject
    ViewModelProvider.Factory factory;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_static_watch_zone;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, WatchZoneViewModel.class);
        viewModel.isRefreshing.observe(this, b -> binder.swipeRefreshLayout.setRefreshing(b));

        binder.wzList.addOnItemTouchListener(new RecyclerTouchListener(getContext(), binder.wzList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                viewModel.onStaticWZClick(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
}
