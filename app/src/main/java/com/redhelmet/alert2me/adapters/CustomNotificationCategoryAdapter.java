package com.redhelmet.alert2me.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Category;


public class CustomNotificationCategoryAdapter extends BaseAdapter {

    private static LayoutInflater inflater=null;
    private ArrayList<Category> category_data;
    private Category tempValues;

    public TextView catName;
    public Activity _context;

    public CustomNotificationCategoryAdapter(Activity context, ArrayList<Category> category_data) {
        this.category_data = category_data;
        this._context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View rootView=vi;
        if(vi==null)
            rootView = inflater.inflate(R.layout.custom_notification_category, null);
        catName = (TextView) rootView.findViewById(R.id.custom_head_text);

        tempValues = ( Category ) category_data.get( position );

        catName.setText(tempValues.getNameLabel());

        return rootView;
    }


}
