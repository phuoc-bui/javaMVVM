package com.phuocbui.mvvm.ui.watchzone;

import android.os.Bundle;
import android.view.View;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.FragmentStaticWatchZoneBinding;
import com.phuocbui.mvvm.databinding.ItemStaticWatchZoneBinding;
import com.phuocbui.basemodule.ui.base.BaseFragment;
import com.phuocbui.basemodule.ui.base.adapter.BaseRecyclerViewAdapter;

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
