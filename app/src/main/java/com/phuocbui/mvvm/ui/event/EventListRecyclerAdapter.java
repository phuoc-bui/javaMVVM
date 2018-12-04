package com.phuocbui.mvvm.ui.event;


import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.data.model.Event;
import com.phuocbui.basemodule.ui.base.adapter.BaseRecyclerViewAdapter;

import java.util.List;


public class EventListRecyclerAdapter extends BaseRecyclerViewAdapter<EventItemViewModel> {

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.event_list_row_template;
    }

    public void setData(List<Event> events) {
        itemsSource.clear();
        for (Event event : events) {
            EventItemViewModel model = new EventItemViewModel(event, true);
            itemsSource.add(model);
        }
    }
}
