package com.redhelmet.alert2me.ui.hint;


import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.databinding.ActivityIntroBinding;
import com.redhelmet.alert2me.ui.base.BaseActivity;

import java.util.List;

import javax.inject.Inject;

public class HintsActivity extends BaseActivity<HintViewModel, ActivityIntroBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    private boolean isLastPageSwiped;
    private int counterPageScroll;
    private boolean help;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_intro;
    }

    @Override
    protected void configWindow() {
        changeStatusBarColor();
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        obtainViewModel(factory, HintViewModel.class);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {     //edit mode

            help = extras.getBoolean("help");
        }

        binder.viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), viewModel.getHints()));
        binder.indicator.setViewPager(binder.viewPager);
        binder.indicator.setViewPager(binder.viewPager);

        binder.viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int arg2) {

            //logic for last view page to move to home screen

            if (position == 3 && positionOffset == 0 && !isLastPageSwiped) {
                if (counterPageScroll != 0) {
                    isLastPageSwiped = true;
                    viewModel.onLastPageScrolled(help);
                }
                counterPageScroll++;
            } else {
                counterPageScroll = 0;
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private static class PagerAdapter extends FragmentPagerAdapter {


        List<Hint> introHints;

        private PagerAdapter(FragmentManager fragmentManager, List<Hint> introHints) {
            super(fragmentManager);
            this.introHints = introHints;
        }

        @Override
        public int getCount() {
            return introHints.size();
        }

        @Override
        public Fragment getItem(int position) {
            return HintFragment.newInstance(introHints.get(position));
        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
