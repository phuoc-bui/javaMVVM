package com.redhelmet.alert2me.adapters;


import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.ui.base.BaseRecyclerViewAdapter;
import com.redhelmet.alert2me.ui.home.event.EventItemViewModel;


public class EventListRecyclerAdapter extends BaseRecyclerViewAdapter<EventItemViewModel> {

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.event_list_row_template;
    }
}
