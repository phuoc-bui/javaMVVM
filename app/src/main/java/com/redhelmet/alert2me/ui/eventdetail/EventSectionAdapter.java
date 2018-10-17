package com.redhelmet.alert2me.ui.eventdetail;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.ui.base.BaseRecyclerViewAdapter;

public class EventSectionAdapter extends BaseRecyclerViewAdapter<ItemSectionViewModel> {
    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_event_section;
    }
}
