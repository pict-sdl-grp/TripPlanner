package com.project.sdl.tripplanner.UserPackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.project.sdl.tripplanner.AuthPackage.AuthActivity;
import com.project.sdl.tripplanner.ProfilePackage.ProfileActivity;
import com.project.sdl.tripplanner.R;
import android.view.View;
import android.widget.Button;

public class UserActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private Button profile;

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
        profile = findViewById(R.id.profile_button);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileActivity();
            }
        });
    }

    public  void openProfileActivity(){
        Intent profile_Intent = new Intent(UserActivity.this, ProfileActivity.class);
        startActivity(profile_Intent);
    }
}