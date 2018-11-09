package com.redhelmet.alert2me.ui.watchzone;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener listener;

    public SimpleItemTouchHelperCallback(RecyclerItemTouchHelperListener listener) {
        super(0, ItemTouchHelper.END);
        this.listener = listener;
    }

    public View getForegroundView(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.itemView;
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        getDefaultUIUtil().onDrawOver(c, recyclerView, getForegroundView(viewHolder), dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        getDefaultUIUtil().clearView(getForegroundView(viewHolder));
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        getDefaultUIUtil().onDraw(c, recyclerView, getForegroundView(viewHolder), dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null) {
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
