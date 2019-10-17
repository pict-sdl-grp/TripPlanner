package com.project.sdl.tripplanner.TripsPackage;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.jmedeisis.draglinearlayout.DragLinearLayout;
import com.project.sdl.tripplanner.NotificationPackage.Notification;
import com.project.sdl.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OrganizeTripActivity extends AppCompatActivity {

    JSONObject places;
    JSONObject positions;
    TextView saveText;
    int dayCount;
    int placeCount;
    JSONObject tripJson;
    DragLinearLayout dragDropAndroidLinearLayout;
    int obstaclePos;
    ArrayList<View> arrayList;
    ImageView backButton;

    public void createTripListItem(String type,String i){

        if(type.contains("header")) {
            TextView header = new TextView(this);
            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            header.setLayoutParams(params4);
            ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(header.getLayoutParams());
            params5.setMargins(0, 10, 0, 0);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
            header.setLayoutParams(layoutParams2);
            header.setPadding(40,40,40,40);
            header.setBackgroundColor(Color.parseColor("#68C4A5"));
            header.setTextColor(Color.WHITE);
            header.setElevation(0);
            header.setTag(type);
            header.setTypeface(header.getTypeface(), Typeface.BOLD);
            try {
                String currentDayJson = String.valueOf(tripJson.getJSONArray("tripDate").get(Integer.valueOf(type.split("-")[1])-1));
                header.setText("Day-"+type.split("-")[1]+" "+currentDayJson.substring(0,11));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            header.setGravity(Gravity.LEFT);

            dragDropAndroidLinearLayout.addView(header);
        }else if(type.contains("place")){
            TextView place = new TextView(this);
            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            place.setLayoutParams(params4);
            ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(place.getLayoutParams());
            params5.setMargins(0, 10, 0, 0);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
            place.setLayoutParams(layoutParams2);
            place.setBackgroundColor(Color.parseColor("#E8EAF6"));
            place.setElevation(0);
            place.setPadding(60, 60, 60, 60);
            place.setTag(type);
            place.setTypeface(place.getTypeface(), Typeface.BOLD);
            try {
                JSONObject currentPlaceJson = new JSONObject(String.valueOf(tripJson.getJSONArray("placesToTrip").get(Integer.valueOf(type.split("-")[1])-1)));
                place.setText(currentPlaceJson.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            place.setGravity(Gravity.LEFT);

            arrayList.add(place);

            dragDropAndroidLinearLayout.addView(place);
        }else{
            TextView place = new TextView(this);
            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            place.setLayoutParams(params4);
            ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(place.getLayoutParams());
            params5.setMargins(0, 10, 0, 0);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
            place.setLayoutParams(layoutParams2);
            place.setPadding(60, 60, 60, 60);
            place.setTag("obstacle");
            place.setTypeface(place.getTypeface(), Typeface.BOLD);
            place.setText("--------------------------------------------------------------");
            place.setGravity(Gravity.CENTER);

            dragDropAndroidLinearLayout.addView(place);
            obstaclePos = Integer.valueOf(i);
        }




    }


    public void createTripListItem(String type,int i){

        if(type.contains("header")) {
            TextView header = new TextView(this);
            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            header.setLayoutParams(params4);
            ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(header.getLayoutParams());
            params5.setMargins(0, 10, 0, 0);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
            header.setLayoutParams(layoutParams2);
            header.setPadding(40,40,40,40);
            header.setBackgroundColor(Color.parseColor("#68C4A5"));
            header.setTextColor(Color.WHITE);
            header.setTag("header-" + (i+1));
            header.setTypeface(header.getTypeface(), Typeface.BOLD);
            try {
                String currentDayJson = String.valueOf(tripJson.getJSONArray("tripDate").get(i));
                header.setText("Day-"+(i+1)+" "+currentDayJson.substring(0,11));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            header.setGravity(Gravity.LEFT);

            dragDropAndroidLinearLayout.addView(header);
        }else if(type.contains("place")){

            TextView place = new TextView(this);
            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            place.setLayoutParams(params4);
            ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(place.getLayoutParams());
            params5.setMargins(0, 10, 0, 0);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
            place.setLayoutParams(layoutParams2);
            place.setBackgroundColor(Color.parseColor("#E8EAF6"));
            place.setElevation(3);
            place.setPadding(60, 60, 60, 60);
            place.setTag("place-" + (i+1));
            place.setTypeface(place.getTypeface(), Typeface.BOLD);
            place.setText("Place-" + (i + 1));
            try {
                JSONObject currentPlaceJson = new JSONObject(String.valueOf(tripJson.getJSONArray("placesToTrip").get(i)));
                place.setText(currentPlaceJson.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            place.setGravity(Gravity.LEFT);

            arrayList.add(place);


            dragDropAndroidLinearLayout.addView(place);
        }else{
            TextView place = new TextView(this);
            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            place.setLayoutParams(params4);
            ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(place.getLayoutParams());
            params5.setMargins(0, 10, 0, 0);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
            place.setLayoutParams(layoutParams2);
            place.setPadding(60, 60, 60, 60);
            place.setTag("obstacle");
            place.setTypeface(place.getTypeface(), Typeface.BOLD);
            place.setText("--------------------------------------------------------------");
            place.setGravity(Gravity.CENTER);

            dragDropAndroidLinearLayout.addView(place);
        }




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organize_trip);
        getSupportActionBar().hide();

        arrayList = new ArrayList<>();
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(String.valueOf(getIntent().getStringExtra("selectedTripUserId")) != "null"){
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref = database.getReference("trips/"+getIntent().getStringExtra("selectedTripUserId"));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> tripHash = (HashMap<String,Object>) dataSnapshot.getValue();
                    tripJson = new JSONObject(tripHash);
                    try {
                        tripJson = tripJson.getJSONObject(getIntent().getStringExtra("id"));

                        dayCount = tripJson.getJSONArray("tripDate").length();
                        placeCount = tripJson.getJSONArray("placesToTrip").length();


                        saveText = findViewById(R.id.saveText);

                        Snackbar.make(findViewById(android.R.id.content), "Drag Your places to organize by Dates", Snackbar.LENGTH_LONG)
                                .setAction("CLOSE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                .show();

                        dragDropAndroidLinearLayout = (DragLinearLayout) findViewById(R.id.drag_drop_layout);

                        places = new JSONObject();
                        positions = new JSONObject();



                        try {
                            JSONObject positionJson = new JSONObject(tripJson.getString("positions"));

                            for (int i=0;i<dayCount+placeCount+1;i++){
                                createTripListItem(positionJson.getString(String.valueOf(i)),String.valueOf(i));
                                positions = positionJson;
                            }
                            places = new JSONObject(tripJson.getString("placesToHeaders"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            for (int i=0;i<dayCount;i++){
                                createTripListItem("header",i);
                                positions.put(String.valueOf(i),"header-"+(i+1));
                            }
                            createTripListItem("obstacle",dayCount);
                            positions.put(String.valueOf(dayCount),"obstacle");
                            for (int i=0;i<placeCount;i++){
                                createTripListItem("place",i);
                                places.put("place-"+(i+1),"none");
                                positions.put(String.valueOf(i+dayCount+1),"place-"+(i+1));
                            }
                        }


                        Log.i("dayCount", String.valueOf(dayCount));
//                    for(int i=dayCount+1;i<dragDropAndroidLinearLayout.getChildCount();i++){
//                        arrayList.add(dragDropAndroidLinearLayout.getChildAt(i));
//                    }

                        ScrollView scrollView = findViewById(R.id.drag_drop_layout_scroll);
                        dragDropAndroidLinearLayout.setContainerScrollView(scrollView);

                        if(dayCount+1 >= placeCount) {
                            int j = 0;
                            for (int i = 1; i < dragDropAndroidLinearLayout.getChildCount(); i++) {

                                View child = dragDropAndroidLinearLayout.getChildAt(i);

                                if(i != dayCount) {
                                    dragDropAndroidLinearLayout.setViewDraggable(child, arrayList.get(j));
                                }

                                j++;
                                if (j == arrayList.size()) {
                                    j = 0;
                                }

                            }

                            dragDropAndroidLinearLayout.setViewDraggable(dragDropAndroidLinearLayout.getChildAt(dayCount),arrayList.get(0));
                            for(int k =0;k<arrayList.size();k++){
                                dragDropAndroidLinearLayout.setViewDraggable(arrayList.get(k),arrayList.get(k));
                            }
                        }else{
                            int j = 1;
                            for (int i = 1; i < dragDropAndroidLinearLayout.getChildCount(); i++) {

                                View child = dragDropAndroidLinearLayout.getChildAt(i);

                                if(i != dayCount) {
                                    dragDropAndroidLinearLayout.setViewDraggable(child, arrayList.get(j));
                                }

                                j++;
                                if (j == arrayList.size()) {
                                    j = 1;
                                }

                            }
                            dragDropAndroidLinearLayout.setViewDraggable(dragDropAndroidLinearLayout.getChildAt(dayCount),arrayList.get(0));
                            for(int k =0;k<arrayList.size();k++){
                                dragDropAndroidLinearLayout.setViewDraggable(arrayList.get(k),arrayList.get(k));
                            }
                        }


                        saveText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                int flag = 0;
                                for(int i=0;i<dayCount+1;i++){
                                    if(String.valueOf(dragDropAndroidLinearLayout.getChildAt(i).getTag()).contains("place")){
                                        flag = 1;
                                        break;
                                    }
                                }
                                if(flag == 0){
                                    mDatabase.child("trips/" + getIntent().getStringExtra("selectedTripUserId") + "/" + getIntent().getStringExtra("id") + "/positions").setValue(null);
                                    mDatabase.child("trips/" + getIntent().getStringExtra("selectedTripUserId") + "/" + getIntent().getStringExtra("id") + "/placesToHeaders").setValue(null);
                                }else {
                                    mDatabase.child("trips/" + getIntent().getStringExtra("selectedTripUserId") + "/" + getIntent().getStringExtra("id") + "/positions").setValue(positions.toString());
                                    mDatabase.child("trips/" + getIntent().getStringExtra("selectedTripUserId") + "/" + getIntent().getStringExtra("id") + "/placesToHeaders").setValue(places.toString());
                                }


                                JSONObject sharedWithJson;
                                try {
                                    DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference();
                                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                    sharedWithJson = tripJson.getJSONObject("sharedWith");
                                    for (Iterator<String> it1 = sharedWithJson.keys(); it1.hasNext(); ) {
                                        String key = it1.next();
                                        Log.i("onClick: ",key);
                                        if(sharedWithJson.getString(key).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                                        }else {
                                            Log.i("sharedKeys", sharedWithJson.getString(key));

                                            //Handle Notification
                                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                            Date date = new Date();
                                            String id  = formatter.format(date).substring(11);

                                            Notification notification = new Notification(id,"share",
                                                    (user1.getDisplayName() +" <b>organized</b> a trip <b>" +getIntent().getStringExtra("tripName")+"</b> in different way."),
                                                    formatter.format(date),user.getDisplayName(),getIntent().getStringExtra("currentUserPhotoUrl"), ServerValue.TIMESTAMP,false);

                                            mDatabase1.child("notifications/").child(sharedWithJson.getString(key)).child(id).setValue(notification);


                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }





                                finish();

                            }
                        });


                        dragDropAndroidLinearLayout.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
                            @Override
                            public void onSwap(View firstView, int firstPosition, View secondView, int secondPosition) {
                                Log.i("onSwap: ", String.valueOf(firstView.getTag()));
                                Log.i("onSwap: ", String.valueOf(firstPosition));
                                Log.i("onSwap: ", String.valueOf(secondView.getTag()));
                                Log.i("onSwap: ", String.valueOf(secondPosition));

                                TextView textView1 = (TextView) firstView;
                                TextView textView2 = (TextView) secondView;

                                firstView.setElevation(20);

                                try {
                                    positions.put(String.valueOf(secondPosition),String.valueOf(firstView.getTag()));
                                    positions.put(String.valueOf(firstPosition),String.valueOf(secondView.getTag()));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(String.valueOf(secondView.getTag()).contains("header") || String.valueOf(secondView.getTag()).contains("obstacle")) {



                                    if (secondPosition < firstPosition) {

                                        if(String.valueOf(secondView.getTag()).contains("obstacle")){
                                            try {
                                                places.put(String.valueOf(firstView.getTag()),"header-"+(dayCount));
                                                firstView.setElevation(0);
                                                Snackbar.make(findViewById(android.R.id.content),textView1.getText() + " on Day-"+(dayCount), Snackbar.LENGTH_LONG)
                                                        .setAction("CLOSE", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                            }
                                                        })
                                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                                        .show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else {

                                            try {
                                                places.put(String.valueOf(firstView.getTag()),
                                                        String.valueOf(secondView.getTag()).split("-")[0] +"-"+ (Integer.valueOf(String.valueOf(secondView.getTag()).split("-")[1]) - 1));
                                                firstView.setElevation(0);
                                                Snackbar.make(findViewById(android.R.id.content), textView1.getText() + " on " + String.valueOf(textView2.getText()).split(" ")[0].split("-")[0] + (Integer.valueOf(String.valueOf(textView2.getText()).split(" ")[0].split("-")[1]) - 1), Snackbar.LENGTH_LONG)
                                                        .setAction("CLOSE", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                            }
                                                        })
                                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                                        .show();


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else if (secondPosition > firstPosition) {

                                        if(String.valueOf(secondView.getTag()).contains("obstacle")){
                                            try {
                                                places.put(String.valueOf(firstView.getTag()),"none");
                                                firstView.setElevation(0);
                                                Snackbar.make(findViewById(android.R.id.content),textView1.getText() + " on waiting list", Snackbar.LENGTH_LONG)
                                                        .setAction("CLOSE", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                            }
                                                        })
                                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                                        .show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else {

                                            try {
                                                places.put(String.valueOf(firstView.getTag()),
                                                        String.valueOf(secondView.getTag()));
                                                firstView.setElevation(0);
                                                Snackbar.make(findViewById(android.R.id.content), textView1.getText() + " on " + textView2.getText().subSequence(0,5), Snackbar.LENGTH_LONG)
                                                        .setAction("CLOSE", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                            }
                                                        })
                                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                                        .show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }else{

                                }

                                Log.i("onSwap: ",places.toString());
                                Log.i("onSwapos: ",positions.toString());

                            }
                        });




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref = database.getReference("trips/"+user.getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> tripHash = (HashMap<String,Object>) dataSnapshot.getValue();
                    tripJson = new JSONObject(tripHash);
                    try {
                        tripJson = tripJson.getJSONObject(getIntent().getStringExtra("id"));

                        dayCount = tripJson.getJSONArray("tripDate").length();
                        placeCount = tripJson.getJSONArray("placesToTrip").length();


                        saveText = findViewById(R.id.saveText);

                        Snackbar.make(findViewById(android.R.id.content), "Drag Your places to organize by Dates", Snackbar.LENGTH_LONG)
                                .setAction("CLOSE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                .show();

                        dragDropAndroidLinearLayout = (DragLinearLayout) findViewById(R.id.drag_drop_layout);

                        places = new JSONObject();
                        positions = new JSONObject();



                        try {
                            JSONObject positionJson = new JSONObject(tripJson.getString("positions"));

                            for (int i=0;i<dayCount+placeCount+1;i++){
                                createTripListItem(positionJson.getString(String.valueOf(i)),String.valueOf(i));
                                positions = positionJson;
                            }
                            places = new JSONObject(tripJson.getString("placesToHeaders"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            for (int i=0;i<dayCount;i++){
                                createTripListItem("header",i);
                                positions.put(String.valueOf(i),"header-"+(i+1));
                            }
                            createTripListItem("obstacle",dayCount);
                            positions.put(String.valueOf(dayCount),"obstacle");
                            for (int i=0;i<placeCount;i++){
                                createTripListItem("place",i);
                                places.put("place-"+(i+1),"none");
                                positions.put(String.valueOf(i+dayCount+1),"place-"+(i+1));
                            }
                        }


                        Log.i("dayCount", String.valueOf(dayCount));
//                    for(int i=dayCount+1;i<dragDropAndroidLinearLayout.getChildCount();i++){
//                        arrayList.add(dragDropAndroidLinearLayout.getChildAt(i));
//                    }

                        ScrollView scrollView = findViewById(R.id.drag_drop_layout_scroll);
                        dragDropAndroidLinearLayout.setContainerScrollView(scrollView);

                        if(dayCount+1 >= placeCount) {
                            int j = 0;
                            for (int i = 1; i < dragDropAndroidLinearLayout.getChildCount(); i++) {

                                View child = dragDropAndroidLinearLayout.getChildAt(i);

                                if(i != dayCount) {
                                    dragDropAndroidLinearLayout.setViewDraggable(child, arrayList.get(j));
                                }

                                j++;
                                if (j == arrayList.size()) {
                                    j = 0;
                                }

                            }

                            dragDropAndroidLinearLayout.setViewDraggable(dragDropAndroidLinearLayout.getChildAt(dayCount),arrayList.get(0));
                            for(int k =0;k<arrayList.size();k++){
                                dragDropAndroidLinearLayout.setViewDraggable(arrayList.get(k),arrayList.get(k));
                            }
                        }else{
                            int j = 1;
                            for (int i = 1; i < dragDropAndroidLinearLayout.getChildCount(); i++) {

                                View child = dragDropAndroidLinearLayout.getChildAt(i);

                                if(i != dayCount) {
                                    dragDropAndroidLinearLayout.setViewDraggable(child, arrayList.get(j));
                                }

                                j++;
                                if (j == arrayList.size()) {
                                    j = 1;
                                }

                            }
                            dragDropAndroidLinearLayout.setViewDraggable(dragDropAndroidLinearLayout.getChildAt(dayCount),arrayList.get(0));
                            for(int k =0;k<arrayList.size();k++){
                                dragDropAndroidLinearLayout.setViewDraggable(arrayList.get(k),arrayList.get(k));
                            }
                        }


                        saveText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                int flag = 0;
                                for(int i=0;i<dayCount+1;i++){
                                    if(String.valueOf(dragDropAndroidLinearLayout.getChildAt(i).getTag()).contains("place")){
                                        flag = 1;
                                        break;
                                    }
                                }
                                if(flag == 0){
                                    mDatabase.child("trips/" + user.getUid() + "/" + getIntent().getStringExtra("id") + "/positions").setValue(null);
                                    mDatabase.child("trips/" + user.getUid() + "/" + getIntent().getStringExtra("id") + "/placesToHeaders").setValue(null);
                                }else {
                                    mDatabase.child("trips/" + user.getUid() + "/" + getIntent().getStringExtra("id") + "/positions").setValue(positions.toString());
                                    mDatabase.child("trips/" + user.getUid() + "/" + getIntent().getStringExtra("id") + "/placesToHeaders").setValue(places.toString());
                                }


                                JSONObject sharedWithJson;
                                try {
                                    DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference();
                                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                    sharedWithJson = tripJson.getJSONObject("sharedWith");
                                    for (Iterator<String> it1 = sharedWithJson.keys(); it1.hasNext(); ) {
                                        String key = it1.next();
                                        Log.i("onClick: ",key);
                                        if(sharedWithJson.getString(key).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                                        }else {
                                            Log.i("sharedKeys", sharedWithJson.getString(key));

                                            //Handle Notification
                                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                            Date date = new Date();
                                            String id  = formatter.format(date).substring(11);

                                            Notification notification = new Notification(id,"share",
                                                    (user1.getDisplayName() +" <b>organized</b> a trip <b>" +getIntent().getStringExtra("tripName")+"</b> in different way."),
                                                    formatter.format(date),user.getDisplayName(),getIntent().getStringExtra("currentUserPhotoUrl"), ServerValue.TIMESTAMP,false);

                                            mDatabase1.child("notifications/").child(sharedWithJson.getString(key)).child(id).setValue(notification);


                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                finish();

                            }
                        });


                        dragDropAndroidLinearLayout.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
                            @Override
                            public void onSwap(View firstView, int firstPosition, View secondView, int secondPosition) {
                                Log.i("onSwap: ", String.valueOf(firstView.getTag()));
                                Log.i("onSwap: ", String.valueOf(firstPosition));
                                Log.i("onSwap: ", String.valueOf(secondView.getTag()));
                                Log.i("onSwap: ", String.valueOf(secondPosition));

                                TextView textView1 = (TextView) firstView;
                                TextView textView2 = (TextView) secondView;

                                firstView.setElevation(20);

                                try {
                                    positions.put(String.valueOf(secondPosition),String.valueOf(firstView.getTag()));
                                    positions.put(String.valueOf(firstPosition),String.valueOf(secondView.getTag()));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(String.valueOf(secondView.getTag()).contains("header") || String.valueOf(secondView.getTag()).contains("obstacle")) {



                                    if (secondPosition < firstPosition) {

                                        if(String.valueOf(secondView.getTag()).contains("obstacle")){
                                            try {
                                                places.put(String.valueOf(firstView.getTag()),"header-"+(dayCount));
                                                firstView.setElevation(0);
                                                Snackbar.make(findViewById(android.R.id.content),textView1.getText() + " on Day-"+(dayCount), Snackbar.LENGTH_LONG)
                                                        .setAction("CLOSE", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                            }
                                                        })
                                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                                        .show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else {

                                            try {
                                                places.put(String.valueOf(firstView.getTag()),
                                                        String.valueOf(secondView.getTag()).split("-")[0] +"-"+ (Integer.valueOf(String.valueOf(secondView.getTag()).split("-")[1]) - 1));
                                                firstView.setElevation(0);
                                                Snackbar.make(findViewById(android.R.id.content), textView1.getText() + " on " + String.valueOf(textView2.getText()).split(" ")[0].split("-")[0] + (Integer.valueOf(String.valueOf(textView2.getText()).split(" ")[0].split("-")[1]) - 1), Snackbar.LENGTH_LONG)
                                                        .setAction("CLOSE", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                            }
                                                        })
                                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                                        .show();


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else if (secondPosition > firstPosition) {

                                        if(String.valueOf(secondView.getTag()).contains("obstacle")){
                                            try {
                                                places.put(String.valueOf(firstView.getTag()),"none");
                                                firstView.setElevation(0);
                                                Snackbar.make(findViewById(android.R.id.content),textView1.getText() + " on waiting list", Snackbar.LENGTH_LONG)
                                                        .setAction("CLOSE", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                            }
                                                        })
                                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                                        .show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else {

                                            try {
                                                places.put(String.valueOf(firstView.getTag()),
                                                        String.valueOf(secondView.getTag()));
                                                firstView.setElevation(0);
                                                Snackbar.make(findViewById(android.R.id.content), textView1.getText() + " on " + textView2.getText().subSequence(0,5), Snackbar.LENGTH_LONG)
                                                        .setAction("CLOSE", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                            }
                                                        })
                                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                                        .show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }else{

                                }

                                Log.i("onSwap: ",places.toString());
                                Log.i("onSwapos: ",positions.toString());

                            }
                        });




                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }









    }

}
