package com.project.sdl.tripplanner.ProfilePackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.project.sdl.tripplanner.R;

public class ProfileActivity extends AppCompatActivity {

    Button editProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //removes Notifiaction bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);

        editProfile = findViewById(R.id.editProf);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditProfile();
            }
        });
    }

    public void openEditProfile(){
        Intent editProfile_intent = new Intent(getApplicationContext(),EditProfile_Activity.class);
        startActivity(editProfile_intent);
    }
}
