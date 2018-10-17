package com.redhelmet.alert2me.ui.eventdetail;

import android.databinding.ObservableField;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Entry;
import com.redhelmet.alert2me.data.model.Section;
import com.redhelmet.alert2me.ui.base.BaseRecyclerViewAdapter;

public class ItemSectionViewModel {
    public ObservableField<String> sectionTitle = new ObservableField<>();
    public SectionContentAdapter adapter = new SectionContentAdapter();
    public ObservableField<String> eventColor = new ObservableField<>();
    public ObservableField<String> titleColor = new ObservableField<>();

    public ItemSectionViewModel(Section section, String eventColor, String eventTextColor) {
        sectionTitle.set(section.getName());
        titleColor.set(eventTextColor);
        this.eventColor.set(eventColor);
        for (Entry entry : section.getEntries()) {
            adapter.itemsSource.add(new ItemContentViewModel(entry));
        }
    }

    public static class SectionContentAdapter extends BaseRecyclerViewAdapter<ItemContentViewModel> {

        @Override
        protected int getLayoutId(int viewType) {
            return R.layout.item_section_content;
        }
    }

    public static class ItemContentViewModel {
        public ObservableField<String> content = new ObservableField<>();

        public ItemContentViewModel(Entry entry) {
            content.set(entry.getValue());
        }
    }
}
