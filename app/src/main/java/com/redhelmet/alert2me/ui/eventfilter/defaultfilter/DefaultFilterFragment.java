package com.redhelmet.alert2me.ui.eventfilter.defaultfilter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.gson.Gson;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.DefaultNotificationAdapter;
import com.redhelmet.alert2me.data.model.CategoryFilter;
import com.redhelmet.alert2me.data.model.CategoryTypeFilter;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.databinding.FragmentDefaultFilterBinding;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.ui.eventfilter.EventFilterActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class DefaultFilterFragment extends BaseFragment<DefaultFilterViewModel, FragmentDefaultFilterBinding> implements EventFilterActivity.OnSaveClickListener {

    private DefaultNotificationAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_default_filter;
    }

    @Override
    protected Class<DefaultFilterViewModel> getViewModelClass() {
        return DefaultFilterViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binder.defaultList.setGroupIndicator(null);
        viewModel.allEventGroup.observe(this, eventGroups -> {
            adapter = new DefaultNotificationAdapter(getBaseActivity(), eventGroups);
            binder.defaultList.setAdapter(adapter);
        });

        binder.defaultList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    binder.defaultList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
    }

    @Override
    public void onSaveClick(boolean editMode) {
        viewModel.saveData(editMode);
    }
}
