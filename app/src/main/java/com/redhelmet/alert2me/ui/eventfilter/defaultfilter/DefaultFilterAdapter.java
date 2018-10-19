package com.redhelmet.alert2me.ui.eventfilter.defaultfilter;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.ui.base.BaseRecyclerViewAdapter;

public class DefaultFilterAdapter extends BaseRecyclerViewAdapter<EventGroupItemViewModel> {
    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_default_filter;
    }
}
