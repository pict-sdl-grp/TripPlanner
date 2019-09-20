package com.project.sdl.tripplanner.GuidePackage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.sdl.tripplanner.R;

public class guide_Activity extends FragmentActivity {

    private static final int NUM_PAGES  = 4;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        viewPager = findViewById(R.id.appGuide);
        pagerAdapter = new guide_Activity_Adapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }

    private class guide_Activity_Adapter extends FragmentStatePagerAdapter{

        public guide_Activity_Adapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Fragment getItem(int i) {
            return  null;//new guideFragment();
        }
    }
}


