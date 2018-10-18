package com.redhelmet.alert2me.ui.base;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.redhelmet.alert2me.BR;

import java.util.Collection;

public abstract class BaseRecyclerViewAdapter<IVM> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.ItemViewHolder> implements BindableAdapter<Collection<IVM>> {

    public ObservableArrayList<IVM> itemsSource = new ObservableArrayList<>();

    public BaseRecyclerViewAdapter() {
        itemsSource.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<IVM>>() {
            @Override
            public void onChanged(ObservableList<IVM> sender) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<IVM> sender, int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(ObservableList<IVM> sender, int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(ObservableList<IVM> sender, int fromPosition, int toPosition, int itemCount) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<IVM> sender, int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    @LayoutRes
    protected abstract int getLayoutId(int viewType);

    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewModelVariable() {
        return BR.viewModel;
    }

    /**
     * Override it if you are using custom ViewHolder (must extend from ItemViewHolder)
     *
     * @param binder : binder that holder layout from getLayoutId
     * @return Custom ItemViewHolder object
     */
    public ItemViewHolder getItemViewHolder(ViewDataBinding binder, int viewType) {
        return new ItemViewHolder(binder);
    }

    @Override
    public void setData(Collection<IVM> data) {
        itemsSource.clear();
        itemsSource.addAll(data);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ViewDataBinding binder = DataBindingUtil.inflate(inflater, getLayoutId(viewType), viewGroup, false);
        return getItemViewHolder(binder, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int position) {
        IVM itemViewModel = itemsSource.get(position);
        itemViewHolder.viewModel = itemViewModel;
        itemViewHolder.binder.setVariable(getViewModelVariable(), itemViewModel);
        itemViewHolder.binder.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return itemsSource.size();
    }

    public static class ItemViewHolder<IVM> extends RecyclerView.ViewHolder {

        private IVM viewModel;
        public ViewDataBinding binder;

        public ItemViewHolder(@NonNull ViewDataBinding binder) {
            super(binder.getRoot());
            this.binder = binder;
        }

        public IVM getViewModel() {
            return viewModel;
        }

        public void setViewModel(IVM viewModel) {
            this.viewModel = viewModel;
        }
    }
}
