package com.phuocbui.basemodule.ui.base.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phuocbui.basemodule.BR;
import com.phuocbui.basemodule.ui.base.Destroyable;

import java.util.Collection;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableList;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerViewAdapter<IVM> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.ItemViewHolder> {

    public ObservableArrayList<IVM> itemsSource = new ObservableArrayList<>();
    public ObservableBoolean hasData = new ObservableBoolean(false);
    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    public BaseRecyclerViewAdapter() {
        itemsSource.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<IVM>>() {
            @Override
            public void onChanged(ObservableList<IVM> sender) {
                notifyDataSetChanged();
                hasData.set(sender.size() > 0);
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
                hasData.set(sender.size() > 0);
            }
        });
    }

    @LayoutRes
    protected abstract int getLayoutId(int viewType);

    public int getItemViewType(int position) {
        return 0;
    }

    @IdRes
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

    public void setData(Collection<IVM> data) {
        itemsSource.clear();
        itemsSource.addAll(data);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
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

        if (itemViewHolder.viewModel instanceof Destroyable)
            ((Destroyable) itemViewHolder.viewModel).onDestroy();
        itemViewHolder.viewModel = itemViewModel;
        itemViewHolder.binder.setVariable(getViewModelVariable(), itemViewModel);
        itemViewHolder.binder.executePendingBindings();
        itemViewHolder.setItemClickListener(itemClickListener);
        itemViewHolder.setItemLongClickListener(itemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return itemsSource.size();
    }

    public static class ItemViewHolder<IVM> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private IVM viewModel;
        public ViewDataBinding binder;
        private ItemClickListener itemClickListener;
        private ItemLongClickListener itemLongClickListener;

        ItemViewHolder(@NonNull ViewDataBinding binder) {
            super(binder.getRoot());
            this.binder = binder;
            binder.getRoot().setOnClickListener(this);
            binder.getRoot().setOnLongClickListener(this);
        }

        public IVM getViewModel() {
            return viewModel;
        }

        public void setViewModel(IVM viewModel) {
            this.viewModel = viewModel;
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void setItemLongClickListener(ItemLongClickListener itemLongClickListener) {
            this.itemLongClickListener = itemLongClickListener;
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) itemClickListener.onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickListener != null) {
                itemLongClickListener.onLongClick(v, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public interface ItemLongClickListener {
        void onLongClick(View view, int position);
    }
}
