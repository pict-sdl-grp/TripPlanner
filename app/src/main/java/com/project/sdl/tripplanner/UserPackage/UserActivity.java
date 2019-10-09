package com.project.sdl.tripplanner.UserPackage;
//Main User Activity

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.project.sdl.tripplanner.HomePackage.HomeFragment1;
import com.project.sdl.tripplanner.ProfilePackage.ProfileFragment;
import com.project.sdl.tripplanner.R;
import com.project.sdl.tripplanner.TripsPackage.TripsFragment;
import com.project.sdl.tripplanner.WishListPackage.WishListFragment;


public class UserActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
//        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.navigation);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        //loading the default fragment
        if(String.valueOf(getIntent().getStringExtra("flag")) != null && String.valueOf(getIntent().getStringExtra("flag")).equals("trip")){
            loadFragment(new TripsFragment());
            bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
        }else {
            loadFragment(new HomeFragment1());
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment1();
                break;

            case R.id.navigation_dashboard:
                fragment = new TripsFragment();
                break;

            case R.id.navigation_notifications:
                fragment = new WishListFragment();
                break;

            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
        }

        return loadFragment(fragment);
    }
}