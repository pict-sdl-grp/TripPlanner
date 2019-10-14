package com.project.sdl.tripplanner.UserPackage;
//Main User Activity

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.sdl.tripplanner.HomePackage.HomeFragment1;
import com.project.sdl.tripplanner.HomePackage.SetCurrentLocation;
import com.project.sdl.tripplanner.NotificationPackage.NotificationFragment;
import com.project.sdl.tripplanner.ProfilePackage.ProfileFragment;
import com.project.sdl.tripplanner.R;
import com.project.sdl.tripplanner.TripsPackage.TripsFragment;
import com.project.sdl.tripplanner.WishListPackage.WishListFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class UserActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    int count = 0;

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

    public static void showBadge(Context context, BottomNavigationView
            bottomNavigationView, @IdRes int itemId, String value) {
                removeBadge(bottomNavigationView, itemId);
                BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
                View badge = LayoutInflater.from(context).inflate(R.layout.notification_badge, bottomNavigationView, false);

                TextView text = badge.findViewById(R.id.badge_text_view);
                text.setText(value);
                itemView.addView(badge);
    }

    public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        for (int i=2;i<itemView.getChildCount();i++){
            itemView.removeViewAt(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
//        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.navigation);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("notifications/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count  = 0;
                Map<String, Object> notificationHash = (HashMap<String,Object>) dataSnapshot.getValue();

                if(notificationHash != null) {
                    JSONObject notificationJson = new JSONObject(notificationHash);

                    try {
                        for (Iterator<String> it = notificationJson.keys(); it.hasNext(); ) {
                            String notificationKey = it.next();

                            if(!notificationJson.getJSONObject(notificationKey).getBoolean("seen")){
                                count++;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }else{

                }

                if(count > 0) {
                    showBadge(getApplicationContext(), bottomNavigationView, R.id.navigation_notifications, "+"+count);
                }else{
                    removeBadge(bottomNavigationView,R.id.navigation_notifications);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





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
            if(String.valueOf(getIntent().getStringExtra("signup")) != "null" && getIntent().getStringExtra("signup").equals("true")){
                Intent intent = new Intent(getApplicationContext(), SetCurrentLocation.class);
                startActivity(intent);
            }
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

            case R.id.navigation_wishlist:
                fragment = new WishListFragment();
                break;

            case R.id.navigation_notifications:
                removeBadge(bottomNavigationView,R.id.navigation_notifications);
                fragment = new NotificationFragment();
                break;

            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
        }

        return loadFragment(fragment);
    }
}