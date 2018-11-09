package com.redhelmet.alert2me.ui.watchzone;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentStaticWatchZoneBinding;
import com.redhelmet.alert2me.databinding.ItemStaticWatchZoneBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.ui.base.BaseRecyclerViewAdapter;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class StaticWatchZoneFragment extends BaseFragment<WatchZoneViewModel, FragmentStaticWatchZoneBinding> implements SimpleItemTouchHelperCallback.RecyclerItemTouchHelperListener {
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

        ItemTouchHelper.SimpleCallback callback = new SimpleItemTouchHelperCallback(this) {
            @Override
            public View getForegroundView(RecyclerView.ViewHolder viewHolder) {
                BaseRecyclerViewAdapter.ItemViewHolder holder = (BaseRecyclerViewAdapter.ItemViewHolder) viewHolder;
                ItemStaticWatchZoneBinding binder = (ItemStaticWatchZoneBinding) holder.binder;
                return binder.foregroundView;
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(binder.wzList);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        ItemStaticWZViewModel removeItem = viewModel.staticWZAdapter.itemsSource.get(position);
        viewModel.removeWatchZone(removeItem);
//        viewModel.staticWZAdapter.itemsSource.remove(viewHolder.getAdapterPosition());
//        Snackbar snackbar = Snackbar.make(binder.getRoot(), removeItem.wzName.get() + " removed from list!", Snackbar.LENGTH_LONG);
//        snackbar.setAction("UNDO", view -> viewModel.staticWZAdapter.itemsSource.add(position, removeItem));
//        snackbar.setActionTextColor(Color.YELLOW);
//        snackbar.show();
    }
}
