package com.phuocbui.mvvm.ui.watchzone;

import android.os.Bundle;
import android.view.View;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.FragmentWatchzoneListBinding;
import com.phuocbui.basemodule.ui.base.BaseFragment;
import com.phuocbui.basemodule.ui.adapter.AppViewPagerAdapter;

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
}
