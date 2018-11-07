package com.redhelmet.alert2me.ui.addwatchzone;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.AppViewPagerAdapter;
import com.redhelmet.alert2me.databinding.FragmentEditStaticZoneNotificationBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.ui.eventfilter.defaultfilter.DefaultFilterFragment;
import com.redhelmet.alert2me.ui.eventfilter.defaultfilter.DefaultFilterViewModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

public class EditStaticZoneNotificationFragment extends BaseFragment<AddStaticZoneViewModel, FragmentEditStaticZoneNotificationBinding> {

    @Inject
    ViewModelProvider.Factory factory;
    public int position = 0;
    private AppViewPagerAdapter adapter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_static_zone_notification;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, AddStaticZoneViewModel.class);
        setupViewPager();
        initializeControls();
    }

    private void setupViewPager() {
        adapter = new AppViewPagerAdapter(getChildFragmentManager());
        DefaultFilterFragment fragment = new DefaultFilterFragment();
        fragment.setFragmentCallback(() -> {
            DefaultFilterViewModel defaultFilterViewModel = fragment.getViewModel();
            if (viewModel != null) {
                viewModel.setFilterViewModel(defaultFilterViewModel);
                defaultFilterViewModel.setEnabledFilters(viewModel.watchZoneModel.groupIds);
            }
        });
        adapter.addFrag(fragment, getString(R.string.lblDefault));
//        adapter.addFrag(new CustomFilterFragment(), getString(R.string.lblCustom));
        binder.viewpager.setAdapter(adapter);
    }

    public void initializeControls() {
        if (viewModel.isDefaultFilter()) {
            binder.viewpager.setCurrentItem(0, false);
        } else {
            binder.viewpager.setCurrentItem(1, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_btn) {
            viewModel.onSaveClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
