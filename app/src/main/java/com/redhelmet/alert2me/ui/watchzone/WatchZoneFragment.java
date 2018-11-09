package com.redhelmet.alert2me.ui.watchzone;

import android.os.Bundle;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.AppViewPagerAdapter;
import com.redhelmet.alert2me.databinding.FragmentWatchzoneListBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

public class WatchZoneFragment extends BaseFragment<WatchZoneViewModel, FragmentWatchzoneListBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_watchzone_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, WatchZoneViewModel.class);

        setupViewPager();
        getBaseActivity().hideToolbar(true);

        binder.viewpager.setCurrentItem(viewModel.proximityEnable.get() ? 1 : 0);
    }

    private void setupViewPager() {
        AppViewPagerAdapter adapter = new AppViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new StaticWatchZoneFragment(), getString(R.string.lbl_staticWZHeading));
        adapter.addFrag(new MobileWatchZoneFragment(), getString(R.string.lbl_mobileWZHeading));
        binder.viewpager.setAdapter(adapter);
        binder.viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) binder.floatingActionButton.show();
                else binder.floatingActionButton.hide();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onRefresh();
    }
}
