package com.project.sdl.tripplanner.TripsPackage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.sdl.tripplanner.R;

import java.util.UUID;

public class CreateTripFormActivity extends AppCompatActivity {

    EditText tripName;
    TextView createText;
    ImageView backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip_form);
        getSupportActionBar().hide();

        tripName = findViewById(R.id.tripName);
        createText = findViewById(R.id.createText);

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        createText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tripName.getText().length() > 0 ) {

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uuid = UUID.randomUUID().toString();

                    Trip trip = new Trip(String.valueOf(tripName.getText()),user.getDisplayName(),0,uuid);

                    mDatabase.child("trips/"+user.getUid()+"/" + uuid + "/")
                            .setValue(trip);

                    mDatabase.child("trips/"+user.getUid()+"/"+uuid+"/sharedWith").push().setValue(user.getUid());

                    Toast.makeText(getApplicationContext(), "Trip created!!!", Toast.LENGTH_SHORT).show();

                    tripName.setText("");

                    finish();

                }else{
                    Toast.makeText(getApplicationContext(), "Trip Name is required!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
