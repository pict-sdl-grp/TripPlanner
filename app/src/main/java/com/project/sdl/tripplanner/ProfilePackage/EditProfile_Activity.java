package com.project.sdl.tripplanner.ProfilePackage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.project.sdl.tripplanner.R;

public class EditProfile_Activity extends AppCompatActivity {


    ImageView back_Image;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_);
        back_Image = findViewById(R.id.backButtonEdit);
        back_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToProfile();
            }
        });
    }

    public void backToProfile(){
        finish();
    }
}
