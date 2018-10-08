package com.redhelmet.alert2me.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Category;

import java.util.List;


public class CustomNotificationCategoryAdapter extends BaseAdapter {

    private List<Category> category_data;

    public CustomNotificationCategoryAdapter(List<Category> category_data) {
        this.category_data = category_data;
    }

    @Override
    public int getCount() {
        return category_data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View vi, ViewGroup parent) {
        View rootView = vi;
        if (vi == null)
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_notification_category, null);
        TextView catName = rootView.findViewById(R.id.custom_head_text);

        Category tempValues = category_data.get(position);

        catName.setText(tempValues.getNameLabel());

        return rootView;
    }


}
