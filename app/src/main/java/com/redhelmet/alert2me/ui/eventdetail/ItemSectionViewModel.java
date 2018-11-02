package com.redhelmet.alert2me.ui.eventdetail;

import com.redhelmet.alert2me.data.model.Entry;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

import androidx.databinding.ObservableField;

public class ItemSectionViewModel extends BaseViewModel {
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> value = new ObservableField<>();
    public ObservableField<String> link = new ObservableField<>();
    public ObservableField<String> eventColor = new ObservableField<>();
    public EventSectionAdapter.EntryType type;

    public ItemSectionViewModel(String sectionTitle, String eventColor) {
        title.set(sectionTitle);
        this.eventColor.set(eventColor);
        type = EventSectionAdapter.EntryType.TITLE;
    }

    public ItemSectionViewModel(Entry entry, String eventColor) {
        title.set(entry.getTitle());
        value.set(entry.getValue());
        link.set(entry.getLink());
        this.eventColor.set(eventColor);
        type = EventSectionAdapter.EntryType.fromTypeString(entry.getType());
    }
}
