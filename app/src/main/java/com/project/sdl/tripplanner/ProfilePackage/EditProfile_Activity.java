package com.project.sdl.tripplanner.ProfilePackage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.project.sdl.tripplanner.R;

public class EditProfile_Activity extends AppCompatActivity {


    ImageView back_Image;
    Button save;
    EditText name;
    TextView email;
    EditText aboutYou;
    EditText phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_);
        getSupportActionBar().hide();

        back_Image = findViewById(R.id.backButtonEdit);
        save = findViewById(R.id.save);
        email = findViewById(R.id.emailEdit);
        aboutYou = findViewById(R.id.aboutEdit);
        phoneNo = findViewById(R.id.phoneEdit);

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
