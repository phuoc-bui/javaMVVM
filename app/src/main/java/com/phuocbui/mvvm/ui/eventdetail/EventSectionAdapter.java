package com.phuocbui.mvvm.ui.eventdetail;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.ui.base.adapter.BaseRecyclerViewAdapter;

public class EventSectionAdapter extends BaseRecyclerViewAdapter<ItemSectionViewModel> {

    public enum EntryType {
        TITLE(R.layout.item_event_section_title),
        BLOCK(R.layout.item_event_section_block),
        INLINE(R.layout.item_event_section_inline);

        int layoutId;
        EntryType(int layoutId) {
            this.layoutId = layoutId;
        }

        static EntryType fromTypeString(String type) {
            if (type.equals("block")) return BLOCK;
            if (type.equals("inline")) return INLINE;
            return TITLE;
        }
    }

    @Override
    protected int getLayoutId(int viewType) {
        return viewType;
    }

    @Override
    public int getItemViewType(int position) {
        // use layoutId as type
        ItemSectionViewModel item = itemsSource.get(position);
        return item.type.layoutId;
    }
}
