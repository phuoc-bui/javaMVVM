package com.redhelmet.alert2me.ui.watchzone;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.ui.base.BaseRecyclerViewAdapter;

public class StaticWZAdapter extends BaseRecyclerViewAdapter<ItemStaticWZViewModel> {
    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_static_watch_zone;
    }
}
