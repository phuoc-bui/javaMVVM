package com.phuocbui.mvvm.ui.watchzone;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.data.model.EditWatchZones;
import com.phuocbui.basemodule.ui.base.adapter.BaseRecyclerViewAdapter;

public class StaticWZAdapter extends BaseRecyclerViewAdapter<ItemStaticWZViewModel> {
    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_static_watch_zone;
    }

    interface OnItemClickListener {
        void onItemClick(EditWatchZones data);
    }
}
