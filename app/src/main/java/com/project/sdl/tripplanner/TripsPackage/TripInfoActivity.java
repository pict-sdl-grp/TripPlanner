package com.project.sdl.tripplanner.TripsPackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.project.sdl.tripplanner.R;
import com.project.sdl.tripplanner.UserPackage.UserActivity;

public class TripInfoActivity extends AppCompatActivity {

    Button browseButton;
    Button addDates;
    Button organizeTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);
        getSupportActionBar().hide();
        browseButton = findViewById(R.id.browseButton);
        addDates = findViewById(R.id.addDates);
        organizeTrip = findViewById(R.id.organizeTrip);

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);
            }
        });

        addDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddDatesActivity.class);
                startActivityForResult(intent,0);
            }
        });

        organizeTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),OrganizeTripActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    addDates.setText(data.getStringArrayListExtra("dateRange").get(0).substring(4,11)+ " - "+
                            data.getStringArrayListExtra("dateRange").get(data.getStringArrayListExtra("dateRange").size()-1).substring(4,11));
                }
                break;
            }
        }
    }
}
