package com.project.sdl.tripplanner.TripsPackage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.sdl.tripplanner.R;
import com.project.sdl.tripplanner.UserPackage.UserActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class TripInfoActivity extends AppCompatActivity {

    Button browseButton;
    Button addDates;
    Button organizeTrip;
    TextView tripName;
    TextView tripCreatedBy;
    TextView tripNoOfItems;
    LinearLayout browseContainer;
    LinearLayout placesListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);
        getSupportActionBar().hide();
        browseButton = findViewById(R.id.browseButton);
        addDates = findViewById(R.id.addDates);
        organizeTrip = findViewById(R.id.organizeTrip);
        tripName = findViewById(R.id.tripName);
        tripCreatedBy = findViewById(R.id.createdBy);
        tripNoOfItems = findViewById(R.id.noOfItems);
        browseContainer = findViewById(R.id.browseContainer);
        placesListContainer = findViewById(R.id.placesListContainer);


        initialize();

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

    public void initialize(){
        try {
            JSONObject tripJson = new JSONObject(getIntent().getStringExtra("selectedTrip"));

            tripName.setText(tripJson.getString("tripName"));
            tripCreatedBy.setText("By "+tripJson.getString("createdBy"));
            tripNoOfItems.setText("Items Available - "+tripJson.getString("noOfItems"));

            try {
                addDates.setText(tripJson.getJSONArray("tripDate").get(0).toString().substring(4,11)+ " - "+
                        tripJson.getJSONArray("tripDate").get(tripJson.getJSONArray("tripDate").length()-1).toString().substring(4,11));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(tripJson.getJSONArray("placesToTrip").length() > 0){
                browseContainer.setVisibility(View.INVISIBLE);

                for(int i=0;i<tripJson.getJSONArray("placesToTrip").length();i++){

                    JSONObject jsonObject = new JSONObject(String.valueOf(tripJson.getJSONArray("placesToTrip").get(i)));

                    createPlaceListItem(jsonObject);

                }

            }else{
                browseContainer.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            browseContainer.setVisibility(View.VISIBLE);
        }
    }

    public void createPlaceListItem(JSONObject jsonObject){

        LinearLayout listItemBlock = new LinearLayout(this);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listItemBlock.setLayoutParams(params1);
        ViewGroup.MarginLayoutParams params2 = new ViewGroup.MarginLayoutParams(listItemBlock.getLayoutParams());
        params2.setMargins(0, 0,0, 40);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(params2);
        listItemBlock.setLayoutParams(layoutParams1);
        listItemBlock.setOrientation(LinearLayout.HORIZONTAL);
        listItemBlock.setPadding(40,40,40,40);
        listItemBlock.setBackgroundColor(Color.parseColor("#E8EAF6"));
        listItemBlock.setElevation(3);
        listItemBlock.setGravity(Gravity.CENTER_VERTICAL);

        final ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(120, 120);
        imageView.setLayoutParams(params3);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.nat4);


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        try {
            storageRef.child("places/" + jsonObject.getString("id") + "/" + jsonObject.getJSONArray("imageRefs").get(0)).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

                @Override
                public void onSuccess(byte[] bytes) {
                    // Use the bytes to display the image
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bm);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


        TextView textView =new TextView(this);
        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params4);
        ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(textView.getLayoutParams());
        params5.setMargins(40, 0,0, 0);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
        textView.setLayoutParams(layoutParams2);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        try {
            textView.setText(jsonObject.getString("name").toUpperCase());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listItemBlock.addView(imageView);
        listItemBlock.addView(textView);

        placesListContainer.addView(listItemBlock);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    addDates.setText(data.getStringArrayListExtra("dateRange").get(0).substring(4,11)+ " - "+
                            data.getStringArrayListExtra("dateRange").get(data.getStringArrayListExtra("dateRange").size()-1).substring(4,11));

                    try {
                        JSONObject tripJson = new JSONObject(getIntent().getStringExtra("selectedTrip"));
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        mDatabase.child("trips/"+user.getUid()+"/" + tripJson.getString("id") + "/tripDate")
                                .setValue(data.getStringArrayListExtra("dateRange"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
                break;
            }
        }
    }
}
