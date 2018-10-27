package com.redhelmet.alert2me.ui.eventfilter.custom;

import android.app.Activity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.CustomNotificationCategoryAdapter;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.databinding.FragmentCustomFilterBinding;
import com.redhelmet.alert2me.ui.activity.AddStaticZoneNotificationTypes;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.ui.eventfilter.EventFilterActivity;

import java.util.List;

import javax.inject.Inject;

public class CustomFilterFragment extends BaseFragment<CustomFilterViewModel, FragmentCustomFilterBinding> implements EventFilterActivity.OnSaveClickListener {

    @Inject
    ViewModelProvider.Factory factory;

    private static final int REQUEST_CATEGORY = 9;
    private CustomNotificationCategoryAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_custom_filter;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, CustomFilterViewModel.class);
        viewModel.allCategories.observe(this, categories -> {

//        simplifyData(dbController.getCustomCatName(0));
            adapter = new CustomNotificationCategoryAdapter(categories);
            binder.customCatList.setAdapter(adapter);
        });
        binder.customCatList.setOnItemClickListener((parent, view1, position, id) -> {
            List<Category> categories = viewModel.allCategories.getValue();
            if (categories != null && position < categories.size()) {
                Intent i = AddStaticZoneNotificationTypes.newInstance(getBaseActivity(), viewModel.allCategories.getValue().get(position), position);
                startActivityForResult(i, REQUEST_CATEGORY);
            }
        });
    }

    @Override
    public void onSaveClick() {
        viewModel.saveData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CATEGORY && resultCode == Activity.RESULT_OK) {
            Category selectedCategory = (Category) data.getSerializableExtra(AddStaticZoneNotificationTypes.EXTRA_CATEGORY);
            int categoryIndex = data.getIntExtra(AddStaticZoneNotificationTypes.EXTRA_CATEGORY_INDEX, 0);
            viewModel.updateCategory(selectedCategory, categoryIndex);
        }
    }
}
