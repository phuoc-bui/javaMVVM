package com.redhelmet.alert2me.ui.hint;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;

import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.ui.activity.MainActivity;
import com.redhelmet.alert2me.ui.fragments.HintFragment;

import java.util.ArrayList;
import java.util.HashMap;

import com.redhelmet.alert2me.R;
import me.relex.circleindicator.CircleIndicator;

public class HintsActivity extends BaseActivity {


    ArrayList<HashMap<String,String>> hints;
        private boolean isLastPageSwiped;
    private int counterPageScroll;
    private boolean help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor();
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        hints=config.getHintsScreen();

        //setting up hard coded hints with desc and image

        HashMap<String,String> data=new HashMap<>();
        hints=new ArrayList<>();
        data.put("title","");
        data.put("desc","EmergencyAUS keeps you<br>updated with current<br>emergency information and<br>alerts in Australia.");
        data.put("img","hint4");
        hints.add(data);
        data=new HashMap<>();
        data.put("title","Observation");
        data.put("desc","Share what you know<br>share what you see, hear and feel.");
        data.put("img","hint1");
        hints.add(data);
        data=new HashMap<>();
        data.put("title","Watch Zones");
        data.put("desc","Monitor the risk all day<br>all night, all year");
        data.put("img","hint2");
        hints.add(data);
        data=new HashMap<>();
        data.put("title","Warning & Incidents");
        data.put("desc","Be aware of your environment<br>your risk, your safety");
        data.put("img","hint3");
        hints.add(data);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {     //edit mode

            help = extras.getBoolean("help");
        }



        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(),hints));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
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
                     launchHomeScreen();
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


        ArrayList<HashMap<String,String>> introHints;

        private PagerAdapter(FragmentManager fragmentManager, ArrayList<HashMap<String, String>> introHints) {
            super(fragmentManager);
            this.introHints=introHints;
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
        private void launchHomeScreen() {
        //preparing for the home launch

            if(help) {
                finish();
            }else {
                PreferenceUtils.saveToPrefs(this, getString(R.string.pref_initialLaunch), true);
                startActivity(new Intent(HintsActivity.this, MainActivity.class));
                finish();
            }
    }
}
