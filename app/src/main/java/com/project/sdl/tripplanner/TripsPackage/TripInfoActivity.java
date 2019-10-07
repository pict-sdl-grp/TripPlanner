package com.project.sdl.tripplanner.TripsPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.here.android.mpa.common.GeoCoordinate;
import com.project.sdl.tripplanner.R;
import com.project.sdl.tripplanner.UserPackage.UserActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TripInfoActivity extends AppCompatActivity {

    Button browseButton;
    ImageButton deleteTripIcon;
    ImageButton shareTripIcon;
    Button addDates;
    Button organizeTrip;
    TextView tripName;
    TextView tripCreatedBy;
    TextView tripNoOfItems;
    LinearLayout browseContainer;
    LinearLayout placesListContainer;
    JSONObject tripJson;
    TextView header = null;
    ArrayList<GeoCoordinate> positionArray;
    LinearLayout mapHolder;
    ImageView tripBg;
    ArrayList<byte[]> tripPlacesImagesArray;
    SharedPreferences sharedPreferences;
    Map<String,View> listItems;

    int dayCount;
    int placeCount;

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
        positionArray = new ArrayList<>();
        mapHolder = findViewById(R.id.mapHolder);
        tripBg = findViewById(R.id.tripBg);
        tripPlacesImagesArray = new ArrayList<>();
        sharedPreferences = getApplicationContext().getSharedPreferences("com.project.sdl.tripplanner", Context.MODE_PRIVATE);
        listItems = new HashMap<>();
        deleteTripIcon = findViewById(R.id.deleteTripIcon);
        shareTripIcon = findViewById(R.id.shareTripIcon);



        if(String.valueOf(getIntent().getStringExtra("selectedTripUserId")) != "null"){
            Log.i("checkId",String.valueOf(getIntent().getStringExtra("selectedTripUserId")));

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference ref = database.getReference("trips/"+getIntent().getStringExtra("selectedTripUserId")+"/"+getIntent().getStringExtra("selectedTripId"));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null) {
                        Map<String, Object> tripHash = (HashMap<String, Object>) dataSnapshot.getValue();
                        tripJson = new JSONObject(tripHash);

                        initialize();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference ref = database.getReference("trips/" + user.getUid() + "/" + getIntent().getStringExtra("selectedTripId"));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> tripHash = (HashMap<String, Object>) dataSnapshot.getValue();
                        tripJson = new JSONObject(tripHash);

                        initialize();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if(String.valueOf(getIntent().getStringExtra("selectedTripUserId")) != "null"){

                shareTripIcon.setVisibility(View.INVISIBLE);
                deleteTripIcon.setVisibility(View.INVISIBLE);

        }

        shareTripIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(),SelectToShareUserActivity.class);
                    intent.putExtra("selectedTripId",getIntent().getStringExtra("selectedTripId"));
                    startActivity(intent);

            }
        });

        deleteTripIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                mDatabase.child("trips/"+user.getUid()+"/" + getIntent().getStringExtra("selectedTripId"))
                        .setValue(null);
                deleteFromSharedUsers("delete");
                Toast.makeText(TripInfoActivity.this, "trip deleted!!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

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
                intent.putExtra("selectedTripUserId",getIntent().getStringExtra("selectedTripUserId"));
                try {
                    intent.putExtra("id",tripJson.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    public void initialize(){

        positionArray.clear();
        listItems.clear();
        placesListContainer.removeAllViews();


        try {

            try {
                dayCount = tripJson.getJSONArray("tripDate").length();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                placeCount = tripJson.getJSONArray("placesToTrip").length();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(placeCount == 0 || dayCount == 0){
                organizeTrip.setVisibility(View.INVISIBLE);
            }

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

                    GeoCoordinate currentPlaceCoords = new GeoCoordinate(
                            jsonObject.getJSONObject("position").getDouble("latitude"),jsonObject.getJSONObject("position").getDouble("longitude"));
                    positionArray.add(currentPlaceCoords);

                    createPlaceListItem(jsonObject,tripJson,i);


                }

                try{
                    if(tripJson.getString("positions").length() > 0){
                        createLayout();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }




                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                try {
                    JSONObject homeBgJson = new JSONObject(String.valueOf(tripJson.getJSONArray("placesToTrip").get(0)));
                    storageRef.child("places/" + homeBgJson.getString("id") + "/" + homeBgJson.getJSONArray("imageRefs").get(0)).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Use the bytes to display the image
                            tripBg.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

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


                mapHolder.setVisibility(View.VISIBLE);
                PlacesToTripMap placesToTripMap = new PlacesToTripMap(this,positionArray);

            }else{
                browseContainer.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            browseContainer.setVisibility(View.VISIBLE);
        }




    }

    public void deleteFromSharedUsers(String params){
        final String[] p1 = {params};
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = database.getReference("sharedTrips/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null && String.valueOf(p1[0]) == "delete") {
                    Map<String, Object> hash1 = (HashMap<String, Object>) dataSnapshot.getValue();
                    JSONArray hash1JsonArray = new JSONArray(hash1.values());


                    ArrayList<String> keyset1 = new ArrayList<>(hash1.keySet());

                    for(String key1:keyset1){
                        Log.i("check1",key1);

                        for (int i = 0; i < hash1JsonArray.length(); i++) {
                            try {
                                JSONObject jsonPart = hash1JsonArray.getJSONObject(i);
                                for (Iterator<String> it = jsonPart.keys(); it.hasNext(); ) {
                                    String parentKey = it.next();
                                    final String currentUserId = parentKey.split("separator")[0];
                                    final String sharedByUserId = parentKey.split("separator")[1];

                                    Log.i("check3",parentKey);
                                    Log.i("check4",jsonPart.getString(parentKey));
                                    JSONObject jsonPart2 = new JSONObject(jsonPart.getString(parentKey));

                                    for (Iterator<String> it1 = jsonPart2.keys(); it1.hasNext(); ) {
                                        String tripKey = it1.next();

                                        if(getIntent().getStringExtra("selectedTripId").equals(jsonPart2.getString(tripKey))) {
                                            Log.i("check5", jsonPart2.getString(tripKey));
                                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                            mDatabase.child("sharedTrips/"+currentUserId+"/"+parentKey+"/"+tripKey).setValue(null);
                                            p1[0] = null;
                                            finish();
                                        }
                                    }




                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }



                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void createLayout(){
        placesListContainer.removeAllViews();

        try{
            if(tripJson.getString("placesToHeaders").length() > 0) {
                JSONObject json = new JSONObject(String.valueOf(tripJson.getString("placesToHeaders")));

                for (int i = 0; i < tripJson.getJSONArray("tripDate").length(); i++) {
                    TextView header = new TextView(this);
                    LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    header.setLayoutParams(params7);
                    ViewGroup.MarginLayoutParams params2 = new ViewGroup.MarginLayoutParams(header.getLayoutParams());
                    params2.setMargins(0, 20, 0, 0);
                    RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(params2);
                    header.setLayoutParams(layoutParams1);
                    header.setTypeface(header.getTypeface(), Typeface.BOLD);
                    header.setPadding(40, 40, 40, 40);
                    header.setBackgroundColor(Color.parseColor("#68C4A5"));
                    header.setTextColor(Color.WHITE);
                    header.setText(tripJson.getJSONArray("tripDate").get(i).toString().substring(0, 11));
                    header.setTag("header-" + (i + 1));
                    listItems.put("header-" + (i + 1), header);
                }

                for (int i = 0; i < tripJson.getJSONArray("placesToTrip").length(); i++) {
                    JSONObject jsonObject = new JSONObject(String.valueOf(tripJson.getJSONArray("placesToTrip").get(i)));
//                    if(json.getString("place-"+(i+1)).contains("header")){
                    LinearLayout listItemBlock = new LinearLayout(this);
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    listItemBlock.setLayoutParams(params1);
                    ViewGroup.MarginLayoutParams params2 = new ViewGroup.MarginLayoutParams(listItemBlock.getLayoutParams());
                    params2.setMargins(0, 0, 0, 10);
                    RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(params2);
                    listItemBlock.setLayoutParams(layoutParams1);
                    listItemBlock.setOrientation(LinearLayout.HORIZONTAL);
                    listItemBlock.setPadding(40, 40, 40, 40);
                    listItemBlock.setBackgroundColor(Color.parseColor("#E0F2F1"));
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
                                tripPlacesImagesArray.add(bytes);
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


                    TextView textView = new TextView(this);
                    LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(params4);
                    ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(textView.getLayoutParams());
                    params5.setMargins(40, 0, 0, 0);
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

                    listItems.put("place-" + (i + 1), listItemBlock);
//                    }
                }

                JSONObject json1 = new JSONObject(tripJson.getString("positions"));
                JSONObject json2 = new JSONObject(tripJson.getString("placesToHeaders"));
                ArrayList<String> placesToHeaders = new ArrayList<>();
                for(int k = 0;k<placeCount;k++){
                    placesToHeaders.add(json2.getString("place-"+(k+1)));
                }
                Log.i("createLayout: ",placesToHeaders.toString());
                for (int j = 0; j < tripJson.getJSONArray("tripDate").length() + tripJson.getJSONArray("placesToTrip").length() + 1; j++) {
                    Log.i("check", json1.getString(String.valueOf(j)));
                    Log.i("check", String.valueOf(listItems.get(json1.getString(String.valueOf(j)))));
                    if (json1.getString(String.valueOf(j)).contains("obstacle")) {

                    } else {

                        if(json1.getString(String.valueOf(j)).contains("place")) {
                            if (json2.getString(json1.getString(String.valueOf(j))).equals("none")) {
                                TextView separator = new TextView(this);
                                LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                separator.setLayoutParams(params4);
                                ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(separator.getLayoutParams());
                                params5.setMargins(0, 10, 0, 0);
                                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
                                separator.setLayoutParams(layoutParams2);
                                separator.setPadding(1, 10, 1, 10);
                                separator.setTag("separator");
                                separator.setTextColor(Color.LTGRAY);
                                separator.setTypeface(separator.getTypeface(), Typeface.BOLD);
                                separator.setText("-----------------------------------------------------------------");
                                separator.setGravity(Gravity.CENTER);

                                placesListContainer.addView(separator);
                            }
                        }

                        if(json1.getString(String.valueOf(j)).contains("header")){
                            if(placesToHeaders.contains(json1.getString(String.valueOf(j)))){

                                TextView separator = new TextView(this);
                                LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                separator.setLayoutParams(params4);
                                ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(separator.getLayoutParams());
                                params5.setMargins(0, 10, 0, 0);
                                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
                                separator.setLayoutParams(layoutParams2);
                                separator.setPadding(1, 10, 1, 10);
                                separator.setTag("separator");
                                separator.setTextColor(Color.LTGRAY);
                                separator.setTypeface(separator.getTypeface(), Typeface.BOLD);
                                separator.setText("-----------------------------------------------------------------");
                                separator.setGravity(Gravity.CENTER);

                                placesListContainer.addView(separator);
                                placesListContainer.addView(listItems.get(json1.getString(String.valueOf(j))));
                            }
                        }else{
                            placesListContainer.addView(listItems.get(json1.getString(String.valueOf(j))));
                        }

                    }
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public View createPlaceListItem(JSONObject jsonObject, JSONObject tripJson, int i){


        LinearLayout listItemBlock = new LinearLayout(this);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listItemBlock.setLayoutParams(params1);
        ViewGroup.MarginLayoutParams params2 = new ViewGroup.MarginLayoutParams(listItemBlock.getLayoutParams());
        params2.setMargins(0, 0,0, 10);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(params2);
        listItemBlock.setLayoutParams(layoutParams1);
        listItemBlock.setOrientation(LinearLayout.HORIZONTAL);
        listItemBlock.setPadding(40,40,40,40);
        listItemBlock.setBackgroundColor(Color.parseColor("#E0F2F1"));
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
                    tripPlacesImagesArray.add(bytes);
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


        header =new TextView(this);
        LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(params6);
        header.setTypeface(header.getTypeface(), Typeface.BOLD);
        header.setPadding(40,40,40,40);
        header.setBackgroundColor(Color.parseColor("#68C4A5"));
        header.setTextColor(Color.WHITE);

        try {

        if(tripJson.getJSONArray("tripDate").length() > 0 && i==0){
            header.setText(tripJson.getJSONArray("tripDate").get(0).toString().substring(0,11)+ " - "+
                    tripJson.getJSONArray("tripDate").get(tripJson.getJSONArray("tripDate").length()-1).toString().substring(0,11));

            placesListContainer.addView(header);
        }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        listItemBlock.setTag("place"+(i+1));
        listItemBlock.addView(imageView);
        listItemBlock.addView(textView);


        placesListContainer.addView(listItemBlock);

        return listItemBlock;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    addDates.setText(data.getStringArrayListExtra("dateRange").get(0).substring(4,11)+ " - "+
                            data.getStringArrayListExtra("dateRange").get(data.getStringArrayListExtra("dateRange").size()-1).substring(4,11));


                    if(header != null) {

                            header.setText(data.getStringArrayListExtra("dateRange").get(0).substring(0,11)+ " - "+
                                    data.getStringArrayListExtra("dateRange").get(data.getStringArrayListExtra("dateRange").size()-1).substring(0,11));

                            ArrayList<View> viewArrayList = new ArrayList<>();
                            for(int i=1;i<placesListContainer.getChildCount();i++){
                                viewArrayList.add(placesListContainer.getChildAt(i));
                            }

                            placesListContainer.removeAllViews();
                            placesListContainer.addView(header,0);
                            for(int i=0;i<viewArrayList.size();i++){
                                placesListContainer.addView(viewArrayList.get(i));
                            }
                            organizeTrip.setVisibility(View.VISIBLE);
                    }

                    try{
                        if(tripJson.getString("positions").length() > 0){
                            createLayout();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(String.valueOf(getIntent().getStringExtra("selectedTripUserId")) != "null") {
                            mDatabase.child("trips/"+getIntent().getStringExtra("selectedTripUserId")+"/" + tripJson.getString("id") + "/tripDate")
                                    .setValue(data.getStringArrayListExtra("dateRange"));

                            mDatabase.child("trips/"+getIntent().getStringExtra("selectedTripUserId")+"/" + tripJson.getString("id") + "/positions")
                                    .setValue(null);
                        }else{
                            mDatabase.child("trips/"+user.getUid()+"/" + tripJson.getString("id") + "/tripDate")
                                    .setValue(data.getStringArrayListExtra("dateRange"));

                            mDatabase.child("trips/"+user.getUid()+"/" + tripJson.getString("id") + "/positions")
                                    .setValue(null);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
                break;
            }
        }
    }
}
