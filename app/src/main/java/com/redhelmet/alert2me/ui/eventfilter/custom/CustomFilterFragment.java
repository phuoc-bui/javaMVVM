package com.redhelmet.alert2me.ui.eventfilter.custom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.CustomNotificationCategoryAdapter;
import com.redhelmet.alert2me.databinding.FragmentCustomFilterBinding;
import com.redhelmet.alert2me.ui.activity.AddStaticZoneNotificationTypes;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.ui.eventfilter.EventFilterActivity;

public class CustomFilterFragment extends BaseFragment<CustomFilterViewModel, FragmentCustomFilterBinding> implements EventFilterActivity.OnSaveClickListener {

    private CustomNotificationCategoryAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_custom_filter;
    }

    @Override
    protected Class<CustomFilterViewModel> getViewModelClass() {
        return CustomFilterViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.allCategories.observe(this, categories -> {

//        simplifyData(dbController.getCustomCatName(0));
            adapter = new CustomNotificationCategoryAdapter(getBaseActivity(), categories);
            binder.customCatList.setAdapter(adapter);
        });
        binder.customCatList.setOnItemClickListener((parent, view1, position, id) -> {

            Intent i = new Intent(getBaseActivity(), AddStaticZoneNotificationTypes.class);
            i.putExtra("catId", position);
            startActivity(i);

        });
    }

    @Override
    public void onSaveClick(boolean editMode) {
        viewModel.saveData(editMode);
    }
}
