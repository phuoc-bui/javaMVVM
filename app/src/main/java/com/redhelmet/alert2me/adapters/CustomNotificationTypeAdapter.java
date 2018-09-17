package com.redhelmet.alert2me.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.model.Category;
import com.redhelmet.alert2me.model.CategoryStatus;
import com.redhelmet.alert2me.model.CategoryType;


public class CustomNotificationTypeAdapter extends BaseAdapter {

    private final ArrayList<Category> typeDetail;
    private List<CategoryType> tempValues;
    private Activity activity;
    private int selectedCategory;

    public CustomNotificationTypeAdapter(Activity activity, ArrayList<Category> categoryData, int selectedCategory) {
        this.activity = activity;
        this.typeDetail = categoryData;
        this.selectedCategory = selectedCategory;
    }


    @Override
    public int getCount() {
        return typeDetail.get(selectedCategory).getTypes().size();
    }

    @Override
    public Object getItem(int position) {
        tempValues = (List<CategoryType>) typeDetail.get(selectedCategory).getTypes();
       return tempValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        CategoryType tempTypes = (CategoryType) this.getItem(position);

        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = activity.getLayoutInflater();
            convertView = inflator.inflate(R.layout.custom_notification_type, null);
            viewHolder = new ViewHolder();

            viewHolder.typeName = (TextView) convertView.findViewById(R.id.custom_type_text);
            viewHolder.typeSwitch = (SwitchCompat) convertView.findViewById(R.id.custom_type_switch);
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.custom_type_text, viewHolder.typeName);
            convertView.setTag(R.id.custom_type_switch, viewHolder.typeSwitch);



        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.typeName.setText(tempTypes.getNameLabel());
        viewHolder.typeSwitch.setChecked(tempTypes.isNotificationDefaultOn());
        viewHolder.typeSwitch.setTag(position);
        viewHolder.typeSwitch.setEnabled(tempTypes.isNotificationCanFilter());
        //changeStatusChecks(position, viewHolder.typeSwitch.isChecked());
        if(!tempTypes.isNotificationCanFilter()){
            viewHolder.typeSwitch.getThumbDrawable().setColorFilter(true ? Color.GRAY : Color.RED, PorterDuff.Mode.MULTIPLY);
            viewHolder.typeSwitch.getTrackDrawable().setColorFilter(true ? Color.GRAY : Color.RED, PorterDuff.Mode.MULTIPLY);
        }

        viewHolder.typeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int getPosition = (Integer) ((SwitchCompat) v).getTag();
                typeDetail.get(selectedCategory).getTypes().get(getPosition).setNotificationDefaultOn(((SwitchCompat) v).isChecked());


                changeStatusChecks(getPosition,((SwitchCompat) v).isChecked());

                notifyDataSetChanged();
            }
        });
        viewHolder.typeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        return convertView;
    }

    static class ViewHolder {
        protected TextView typeName;
        protected SwitchCompat typeSwitch;
    }

    public void changeStatusChecks(int getPosition, boolean isChecked){

        List<CategoryStatus> status= (List<CategoryStatus>) typeDetail.get(selectedCategory).getTypes().get(getPosition).getStatuses();
        for(int i =0 ;i <status.size();i++){
            if(status.get(i).isNotificationCanFilter()) {
                status.get(i).setNotificationDefaultOn(isChecked);
            }

        }
    }
}
