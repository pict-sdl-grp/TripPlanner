package com.project.sdl.tripplanner.TripsPackage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.project.sdl.tripplanner.R;

public class CreateTripFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip_form);
        getSupportActionBar().hide();
    }
}
