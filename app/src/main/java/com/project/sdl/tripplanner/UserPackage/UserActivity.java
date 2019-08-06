package com.project.sdl.tripplanner.UserPackage;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.project.sdl.tripplanner.AuthPackage.AuthActivity;
import com.project.sdl.tripplanner.ProfilePackage.ProfileActivity;
import com.project.sdl.tripplanner.R;
import android.view.View;

public class UserActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private TabLayout bottomTab;

    public void onClickLogout(View view){
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();
        bottomTab = findViewById(R.id.bottomTablayout);

        bottomTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        break;

                    case 3:
                        Intent profileAcitvity = new Intent(UserActivity.this,ProfileActivity.class);
                        startActivity(profileAcitvity);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
