package com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.sdl.tripplanner.R;

import java.util.UUID;

public class WritePlaceReview extends AppCompatActivity {

    ImageButton calenderButton;
    TextView monthVisited;
    LinearLayout visitTypeContainer;
    TextView visitTypeText;
    TextView submitReview;
    EditText reviewTitle;
    EditText mainReview;
    RatingBar ratingBar;
    Double rating = 0.0;
    ImageView backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_place_review);
        getSupportActionBar().hide();

        calenderButton = findViewById(R.id.calenderButton);
        monthVisited = findViewById(R.id.monthVisitedText2);
        visitTypeContainer = findViewById(R.id.visitTypeContainer);
        visitTypeText = findViewById(R.id.visitTypeText);
        submitReview = findViewById(R.id.createText);
        reviewTitle = findViewById(R.id.reviewTitle);
        mainReview = findViewById(R.id.mainReview);
        ratingBar = findViewById(R.id.reviewRating);
        backButton = findViewById(R.id.wishlistIcon3);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CalenderDialog.class);
                startActivityForResult(intent,0);
            }
        });

        visitTypeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WritePlaceReview.this);
                builder.setTitle("Type of visit");

                final String[] visitType = {"Solo", "Business", "Couples", "Friends", "Family with teenagers","Family with young children"};
                builder.setItems(visitType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        visitTypeText.setText(visitType[which]);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = Double.valueOf(v);
            }
        });

        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(monthVisited.getText().length() > 0 && visitTypeText.getText().length() > 0 && reviewTitle.getText().length() > 0 && mainReview.getText().length() > 0) {

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    Reviews review = new Reviews(rating, String.valueOf(monthVisited.getText()),
                            String.valueOf(visitTypeText.getText()), String.valueOf(reviewTitle.getText()),
                            String.valueOf(mainReview.getText()),user.getDisplayName(),FirebaseAuth.getInstance().getCurrentUser().getUid());

                    mDatabase.child("reviews/" + getIntent()
                            .getStringExtra("currentPlaceId") + "/" + UUID.randomUUID().toString() + "/")
                            .setValue(review);

                    Toast.makeText(WritePlaceReview.this, "Review Saved Successfully", Toast.LENGTH_SHORT).show();

                    clearAllData();

                }else{
                    Toast.makeText(WritePlaceReview.this, "All fields are required!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public void clearAllData(){

        visitTypeText.setText("Type of visit");
        reviewTitle.setText("");
        mainReview.setText("");
        ratingBar.setRating(0);
        monthVisited.setText(" -- / -- / --");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    monthVisited.setText(data.getStringExtra("date"));
                }
                break;
            }
        }
    }
}


