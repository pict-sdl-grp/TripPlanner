package com.project.sdl.tripplanner.UserPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.project.sdl.tripplanner.AuthPackage.AuthActivity;
import com.project.sdl.tripplanner.R;

public class UserActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    public void onClickLogout(View view){
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();
    }
}