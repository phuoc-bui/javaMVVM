package com.redhelmet.alert2me.adapters;

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

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;

import java.util.List;

import static android.graphics.Color.GRAY;


public class CustomNotificationStatusAdapter extends BaseAdapter {

    private CategoryType selectedType;


    public CustomNotificationStatusAdapter(CategoryType selectedType) {
        this.selectedType = selectedType;
    }

    @Override
    public int getCount() {
        return selectedType.getStatuses().size();
    }

    @Override
    public Object getItem(int position) {
        return selectedType.getStatuses().get(position);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_notification_type, null);
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
        Log.e("EA", "" + tempStatus.isNotificationCanFilter());
        if (!tempStatus.isNotificationCanFilter()) {
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
                selectedType.getStatuses().get(getPosition).setNotificationDefaultOn(((SwitchCompat) v).isChecked());
                notifyDataSetChanged();
                changeStatusChecks(getPosition, ((SwitchCompat) v).isChecked());
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

    public void changeStatusChecks(int getPosition, boolean isChecked) {
        int check_all = 0;
        int check_off = 0;

        List<CategoryStatus> status = selectedType.getStatuses();
        for (int i = 0; i < status.size(); i++) {

            if (status.get(i).isNotificationDefaultOn()) {
                check_all++;
            } else {
                check_off++;
            }

        }

        if (check_all > 0) {
            if (selectedType.isNotificationCanFilter())
                selectedType.setNotificationDefaultOn(true);
        } else if (check_off == status.size()) {
            if (selectedType.isNotificationCanFilter())
                selectedType.setNotificationDefaultOn(false);
        }
    }


}
