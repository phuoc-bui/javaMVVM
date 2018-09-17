package com.redhelmet.alert2me.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.model.Category;
import com.redhelmet.alert2me.model.CategoryStatus;
import com.redhelmet.alert2me.model.CategoryType;

import static android.graphics.Color.GRAY;


public class CustomNotificationStatusAdapter extends BaseAdapter {

    private  ArrayList<Category> categoryData;
    private List<CategoryType> typeValues;
    private List<CategoryStatus> statusValues;
    private Activity activity;
    private int selectedCategory;
    private int selectedType;


    public CustomNotificationStatusAdapter(Activity act, ArrayList<Category> categoryArray, int selectedCategory, int selectedType) {
        this.activity=act;
        this.categoryData=categoryArray;
        this.selectedCategory=selectedCategory;
        this.selectedType=selectedType;
    }

    @Override
    public int getCount() {
        return categoryData.get(selectedCategory).getTypes().get(selectedType).getStatuses().size();
    }

    @Override
    public Object getItem(int position) {

        statusValues = (List<CategoryStatus>)   categoryData.get(selectedCategory).getTypes().get(selectedType).getStatuses();
        return statusValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CategoryStatus tempStatus = (CategoryStatus) this.getItem(position);

        ViewHolder viewHolder = null;

        if (convertView == null) {
            LayoutInflater inflator = activity.getLayoutInflater();
            convertView = inflator.inflate(R.layout.custom_notification_type, null);
            viewHolder = new ViewHolder();

            viewHolder.typeName = (TextView) convertView.findViewById(R.id.custom_type_text);
            viewHolder.typeSwitch = (SwitchCompat) convertView.findViewById(R.id.custom_type_switch);
            viewHolder.leftarrow = (ImageView) convertView.findViewById(R.id.arrow_key);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.custom_type_text, viewHolder.typeName);
            convertView.setTag(R.id.custom_type_switch, viewHolder.typeSwitch);
            convertView.setTag(R.id.arrow_key, viewHolder.leftarrow);



        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.leftarrow.setVisibility(View.INVISIBLE);
        viewHolder.typeName.setText(tempStatus.getName());
        viewHolder.typeSwitch.setChecked(tempStatus.isNotificationDefaultOn());
        viewHolder.typeSwitch.setTag(position);
        viewHolder.typeSwitch.setEnabled(tempStatus.isNotificationCanFilter());
        Log.e("EA",""+tempStatus.isNotificationCanFilter());
        if(!tempStatus.isNotificationCanFilter()){
            viewHolder.typeSwitch.getThumbDrawable().setColorFilter(true ? GRAY : Color.RED, PorterDuff.Mode.MULTIPLY);
            viewHolder.typeSwitch.getTrackDrawable().setColorFilter(true ? GRAY : Color.RED, PorterDuff.Mode.MULTIPLY);
        }

    //    changeStatusChecks(position,viewHolder.typeSwitch.isChecked());

viewHolder.typeSwitch.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
   //     SwitchCompat switchButton = (SwitchCompat) buttonView;
        int getPosition = (Integer) ((SwitchCompat) v).getTag();
        //categoryData.get(selectedCategory).getStatuses().get(getPosition).setNotificationDefaultOn(isChecked);
        categoryData.get(selectedCategory).getTypes().get(selectedType).getStatuses().get(getPosition).setNotificationDefaultOn(((SwitchCompat) v).isChecked());
        notifyDataSetChanged();
        changeStatusChecks(getPosition,((SwitchCompat) v).isChecked());
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
        protected ImageView leftarrow;

    }

    public void changeStatusChecks(int getPosition, boolean isChecked){
        int check_all=0;
        int check_off=0;

        List<CategoryStatus> status= (List<CategoryStatus>) categoryData.get(selectedCategory).getTypes().get(selectedType).getStatuses();
        for(int i =0 ;i <status.size();i++){

                if(status.get(i).isNotificationDefaultOn()){
                    check_all++;
                }else{
                    check_off++;
                }

        }

        if(check_all>0){
            if( categoryData.get(selectedCategory).getTypes().get(selectedType).isNotificationCanFilter())
                categoryData.get(selectedCategory).getTypes().get(selectedType).setNotificationDefaultOn(true);
        }else if(check_off==status.size()){
            if( categoryData.get(selectedCategory).getTypes().get(selectedType).isNotificationCanFilter())
                categoryData.get(selectedCategory).getTypes().get(selectedType).setNotificationDefaultOn(false);
        }
//        if(check_all==status.size()){
//            if( categoryData.get(selectedCategory).getTypes().get(selectedType).isNotificationCanFilter())
//            categoryData.get(selectedCategory).getTypes().get(selectedType).setNotificationDefaultOn(true);
//        }else if(check_off==status.size()){
//            if( categoryData.get(selectedCategory).getTypes().get(selectedType).isNotificationCanFilter())
//            categoryData.get(selectedCategory).getTypes().get(selectedType).setNotificationDefaultOn(false);
//        }
    }


}
