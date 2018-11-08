package com.redhelmet.alert2me.adapters;


import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.base.BaseRecyclerViewAdapter;
import com.redhelmet.alert2me.ui.event.EventItemViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class EventListRecyclerAdapter extends BaseRecyclerViewAdapter<EventItemViewModel> {

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.event_list_row_template;
    }

    public void sortItemSource(Comparator<Event> comparator) {
        Comparator<EventItemViewModel> vmComparator = (o1, o2) -> comparator.compare(o1.event.get(), o2.event.get());
        Collections.sort(itemsSource, vmComparator);
        notifyDataSetChanged();
    }

    public void setData(List<Event> events) {
        itemsSource.clear();
        for (Event event : events) {
            EventItemViewModel model = new EventItemViewModel(event, true);
            itemsSource.add(model);
        }
    }
}
