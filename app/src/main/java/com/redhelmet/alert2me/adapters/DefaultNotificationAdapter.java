package com.redhelmet.alert2me.adapters;

import android.content.Context;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;


public class DefaultNotificationAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<EventGroup> data;

    public DefaultNotificationAdapter(Context context, List<EventGroup> data) {
        this._context = context;
        this.data = data;
    }

    @Override
    public EventGroup getChild(int groupPosition, int childPosititon) {


        return this.data.get(groupPosition);

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        EventGroup defaultData = getChild(groupPosition, childPosition);
        String childText = defaultData.getDescription();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.default_notification_child, parent, false);
        }

        TextView child_text = (TextView) convertView.findViewById(R.id.default_head_content);
        child_text.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return 1;
    }

    @Override
    public EventGroup getGroup(int groupPosition) {
        return this.data.get(groupPosition);
    }

    @Override
    public int getGroupCount() {

        // Get header size
        return this.data.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, final ViewGroup parent) {

        EventGroup defaultData = getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.default_notification_head, parent, false);
        }

        TextView header_text = (TextView) convertView.findViewById(R.id.default_head_text);
        final ImageButton arrow_direction = (ImageButton) convertView.findViewById(R.id.default_head_trigger);
        final SwitchCompat toggle_switch = (SwitchCompat) convertView.findViewById(R.id.default_head_switch);
        toggle_switch.setTag(defaultData.getId());
        TextView toggle_always = (TextView) convertView.findViewById(R.id.default_head_always);
        toggle_always.setTextSize(12);
        header_text.setText(defaultData.getName());

        if (defaultData.isFilterToggle()) {
            toggle_switch.setVisibility(View.VISIBLE);
            toggle_always.setVisibility(View.GONE);
        } else {
            toggle_always.setVisibility(View.VISIBLE);
            toggle_switch.setVisibility(View.GONE);

        }
        toggle_switch.setChecked(defaultData.isFilterOn());

        toggle_switch.setOnClickListener(v -> {
            data.get(groupPosition).setFilterOn(((SwitchCompat) v).isChecked());
            data.get(groupPosition).setUserEdited(true);
        });

        arrow_direction.setOnClickListener(view -> {
            ExpandableListView expandableListView = (ExpandableListView) parent;
            if (!isExpanded) {
                expandableListView.expandGroup(groupPosition);
                arrow_direction.setImageResource(R.drawable.icon_up);
            } else {
                expandableListView.collapseGroup(groupPosition);
                arrow_direction.setImageResource(R.drawable.icon_down);
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
