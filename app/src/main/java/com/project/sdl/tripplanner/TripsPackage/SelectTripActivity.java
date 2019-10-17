package com.project.sdl.tripplanner.TripsPackage;

import android.content.Intent;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
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

public class SelectTripActivity extends AppCompatActivity {

    LinearLayout tripListContainer;
    JSONObject tripJson;
    int myTripCount = 0;
    HashMap<String,JSONObject> tripJsonHashMap;
    String currentUserPhotoUrl;
    ImageView backButton;

    public void sendNotification(String tripName, String action, JSONObject tripJson){
        JSONObject sharedWithJson;
        try {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            sharedWithJson = tripJson.getJSONObject("sharedWith");
            for (Iterator<String> it1 = sharedWithJson.keys(); it1.hasNext(); ) {
                String key = it1.next();

                if(sharedWithJson.getString(key).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                }else {
                    Log.i("sharedKeys", sharedWithJson.getString(key));

                    //Handle Notification
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String id  = formatter.format(date).substring(11);

                    Notification notification = new Notification(id,"share",
                            (user.getDisplayName() +" <b>"+action+"</b> a place <b>"+getIntent().getStringExtra("currentPlaceName")+"</b> "+(action.equals("removed")?"from":"in")+" <b>"+tripName+"</b>."),
                            formatter.format(date),user.getDisplayName(),currentUserPhotoUrl, ServerValue.TIMESTAMP,false);

                    mDatabase.child("notifications/").child(sharedWithJson.getString(key)).child(id).setValue(notification);


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_trip);
        getSupportActionBar().hide();

        tripListContainer = findViewById(R.id.selectTripContainer);
        tripJsonHashMap = new HashMap<>();
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = database.getReference("trips/"+user.getUid());

        final FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref1 = database1.getReference("sharedTrips/"+user1.getUid());


        final int[] check = {0};
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> tripHash = (HashMap<String,Object>) dataSnapshot.getValue();
                tripJson = new JSONObject(tripHash);


                final ArrayList<String> placesToTrip = new ArrayList<>();


                for(String key : tripHash.keySet()){
                    if(check[0] == 0) {
                        try {
                            createTripListItem(tripJson.getJSONObject(key).getString("tripName"), key, myTripCount, tripJson);
                            myTripCount++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                for (int i =0 ;i<getIntent().getStringArrayListExtra("tripNames").size();i++){

                    final int finalI = i;
                    final int finalI1 = i;

                    tripListContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            check[0] = 1;
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            try {

                                for(int i=0;i<tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").length();i++){
                                    placesToTrip.add((String) tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").get(i));
                                }

                                if(placesToTrip.contains(getIntent().getStringExtra("currentPlace"))){
                                    int removeIndex = placesToTrip.indexOf(getIntent().getStringExtra("currentPlace"))+1;

                                    placesToTrip.remove(getIntent().getStringExtra("currentPlace"));

                                    mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/placesToTrip")
                                            .setValue(placesToTrip);

                                    mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/noOfItems")
                                            .setValue(placesToTrip.size());

                                    mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/positions")
                                            .setValue(null);

                                    mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/placesToHeaders")
                                            .setValue(null);

//                                    try {
//                                        int length = tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").length()
//                                                +tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("tripDate").length();
//                                        JSONObject json = new JSONObject(tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getString("positions"));
//                                        for(int i=0;i<length+1;i++){
//                                            if(json.getString(String.valueOf(i)).equals("place-"+(removeIndex))){
//                                                json.remove(String.valueOf(i));
//                                                Log.i("remove: ",json.toString());
//                                            }
//                                        }

//                                        mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/positions")
//                                                .setValue(null);

//                                        JSONObject json1 = new JSONObject(tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getString("placesToHeaders"));
//                                        json1.remove("place-"+(removeIndex));
//                                        mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/placesToHeaders")
//                                                .setValue(null);
//                                    }catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }


                                    sendNotification(getIntent().getStringArrayListExtra("tripNames").get(finalI),"removed",tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())));

                                    Snackbar.make(findViewById(android.R.id.content), "removed from "+getIntent().getStringArrayListExtra("tripNames").get(finalI), 5000)
                                            .setAction("Close", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    finish();
                                                }
                                            })
                                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                            .show();





                                }else{
                                    placesToTrip.add(getIntent().getStringExtra("currentPlace"));
                                    mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/placesToTrip")
                                            .setValue(placesToTrip);

                                    mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/noOfItems")
                                            .setValue(placesToTrip.size());

                                    try{
                                        int length = tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").length()
                                                +tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("tripDate").length();
                                        JSONObject json = new JSONObject(tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getString("positions"));
                                        json.put(String.valueOf(length+1),"place-"+(tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").length()+1));
                                        mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/positions")
                                                .setValue(json.toString());
                                        JSONObject json1 = new JSONObject(tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getString("placesToHeaders"));
                                        json1.put("place-"+(tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").length()+1),"none");
                                        mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/placesToHeaders")
                                                .setValue(json1.toString());
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                    sendNotification(getIntent().getStringArrayListExtra("tripNames").get(finalI),"added",tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())));

                                    Snackbar.make(findViewById(android.R.id.content), "saved to "+getIntent().getStringArrayListExtra("tripNames").get(finalI), 5000)
                                            .setAction("View", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(getApplicationContext(),TripInfoActivity.class);
                                                    intent.putExtra("selectedTripId", String.valueOf(tripListContainer.getChildAt(finalI1).getTag()));
                                                    startActivity(intent);
                                                }
                                            }).setActionTextColor(getResources().getColor(android.R.color.holo_green_light ))
                                            .show();


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                placesToTrip.add(getIntent().getStringExtra("currentPlace"));
                                mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/placesToTrip")
                                        .setValue(placesToTrip);

                                mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/noOfItems")
                                        .setValue(placesToTrip.size());

                                try{
                                    int length = tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").length()
                                            +tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("tripDate").length();
                                    JSONObject json = new JSONObject(tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getString("positions"));
                                    json.put(String.valueOf(length+1),"place-"+(tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").length()+1));
                                    mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/positions")
                                            .setValue(json.toString());
                                    JSONObject json1 = new JSONObject(tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getString("placesToHeaders"));
                                    json1.put("place-"+(tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").length()+1),"none");
                                    mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/placesToHeaders")
                                            .setValue(json1.toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }


                                try {
                                    sendNotification(getIntent().getStringArrayListExtra("tripNames").get(finalI),"added",tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())));
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }


                                Snackbar.make(findViewById(android.R.id.content), "saved to "+getIntent().getStringArrayListExtra("tripNames").get(finalI), 5000)
                                        .setAction("View", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getApplicationContext(),TripInfoActivity.class);
                                                intent.putExtra("selectedTripId", String.valueOf(tripListContainer.getChildAt(finalI1).getTag()));
                                                startActivity(intent);
                                            }
                                        }).setActionTextColor(getResources().getColor(android.R.color.holo_green_light ))
                                        .show();


                            }



                        }
                    });

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



//MY TRIPS LIST
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//SHARED TRIPS LIST



        final int[] checkFinish = {0};

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> tripHash = (HashMap<String, Object>) dataSnapshot.getValue();
                    ArrayList<String> keyset = new ArrayList<>(tripHash.keySet());
                    final ArrayList<String> sharedByUserIdArray = new ArrayList<>();
                    int userInt = 0;
                    for (String parentKey : keyset) {
                        final String currentUserId = parentKey.split("separator")[0];
                        final String sharedByUserId = parentKey.split("separator")[1];
                        sharedByUserIdArray.add(sharedByUserId);
                        JSONObject tripJson = null;
                        try {
                            tripJson = new JSONObject(String.valueOf(tripHash.get(parentKey)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("sharedTrips", tripJson.toString());
                        for (Iterator<String> it = tripJson.keys(); it.hasNext(); ) {
                            final String key = it.next();

                            try {
                                Log.i("tripId", tripJson.getString(key));

                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference ref1 = database.getReference("trips/" + sharedByUserId + "/" + tripJson.getString(key));

                                final int finalUserInt = userInt;
                                ref1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, Object> tripHash = (HashMap<String, Object>) dataSnapshot.getValue();
                                        if (tripHash != null) {
                                            final JSONObject tripJson1 = new JSONObject(tripHash);
                                            try {
                                                tripJsonHashMap.put(tripJson1.getString("id"),tripJson1);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Log.i("tripHashs", tripHash.toString());


//                                            int p = 0;
//                                            for (String key : tripHash.keySet()) {

                                            if(checkFinish[0] == 0) {
                                                try {
                                                    createTripListItem(tripJson1.getString("tripName"), tripJson1.getString("id"), finalUserInt,tripJson1);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
//                                                p++;
                                            final ArrayList<String> placesToTrip = new ArrayList<>();
                                            for (int i = myTripCount; i < tripListContainer.getChildCount(); i++) {
                                                final int finalI = i;
                                                tripListContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        checkFinish[0] = 1;
                                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                                                        try {

                                                            for(int i=0;i<tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getJSONArray("placesToTrip").length();i++){
                                                                Log.i("onClick: ",String.valueOf(i));
                                                                placesToTrip.add((String) tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getJSONArray("placesToTrip").get(i));
                                                            }

                                                            if(placesToTrip.contains(getIntent().getStringExtra("currentPlace"))){
                                                                int removeIndex = placesToTrip.indexOf(getIntent().getStringExtra("currentPlace"))+1;

                                                                placesToTrip.remove(getIntent().getStringExtra("currentPlace"));

                                                                Log.i("onClickSelect",placesToTrip.toString());
                                                                mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/placesToTrip")
                                                                        .setValue(placesToTrip);

                                                                mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/noOfItems")
                                                                        .setValue(placesToTrip.size());

                                                                mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/positions")
                                                                        .setValue(null);

                                                                mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/placesToHeaders")
                                                                        .setValue(null);

                                                                sendNotification(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getString("tripName"),"removed",tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()));

                                                                Snackbar.make(findViewById(android.R.id.content), "removed from "+tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getString("tripName"), 5000)
                                                                        .setAction("Close", new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View view) {
                                                                                finish();
                                                                            }
                                                                        })
                                                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                                                        .show();





                                                            }else{
                                                                Log.i("onClickSelect","llllll");
                                                                placesToTrip.add(getIntent().getStringExtra("currentPlace"));
                                                                mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/placesToTrip")
                                                                        .setValue(placesToTrip);

                                                                mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/noOfItems")
                                                                        .setValue(placesToTrip.size());

                                                                try{
                                                                    int length = tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getJSONArray("placesToTrip").length()
                                                                            +tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getJSONArray("tripDate").length();
                                                                    JSONObject json = new JSONObject(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getString("positions"));
                                                                    json.put(String.valueOf(length+1),"place-"+(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getJSONArray("placesToTrip").length()+1));
                                                                    mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/positions")
                                                                            .setValue(json.toString());
                                                                    JSONObject json1 = new JSONObject(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getString("placesToHeaders"));
                                                                    json1.put("place-"+(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getJSONArray("placesToTrip").length()+1),"none");
                                                                    mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/placesToHeaders")
                                                                            .setValue(json1.toString());
                                                                } catch (JSONException e1) {
                                                                    e1.printStackTrace();
                                                                }


                                                                sendNotification(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getString("tripName"),"added",tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()));


                                                                Snackbar.make(findViewById(android.R.id.content), "saved to "+tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getString("tripName"), 5000)
                                                                        .setAction("View", new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View view) {
                                                                                Intent intent = new Intent(getApplicationContext(),TripInfoActivity.class);
                                                                                intent.putExtra("selectedTripId", String.valueOf(tripListContainer.getChildAt(finalI).getTag()));
                                                                                intent.putExtra("selectedTripUserId", sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId()));
                                                                                startActivity(intent);
                                                                            }
                                                                        }).setActionTextColor(getResources().getColor(android.R.color.holo_green_light ))
                                                                        .show();

                                                            }



                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            placesToTrip.add(getIntent().getStringExtra("currentPlace"));
                                                            Log.i("onClickSelect",placesToTrip.toString());
                                                            Log.i("onClickSelect", (String) tripListContainer.getChildAt(finalI).getTag());

                                                            mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/placesToTrip")
                                                                    .setValue(placesToTrip);

                                                            mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/noOfItems")
                                                                    .setValue(placesToTrip.size());
//
                                                            try{
                                                                int length = tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getJSONArray("placesToTrip").length()
                                                                        +tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getJSONArray("tripDate").length();
                                                                JSONObject json = new JSONObject(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getString("positions"));
                                                                json.put(String.valueOf(length+1),"place-"+(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getJSONArray("placesToTrip").length()+1));
                                                                mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/positions")
                                                                        .setValue(json.toString());
                                                                JSONObject json1 = new JSONObject(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getString("placesToHeaders"));
                                                                json1.put("place-"+(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getJSONArray("placesToTrip").length()+1),"none");
                                                                mDatabase.child("trips/"+sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId())+"/" + tripListContainer.getChildAt(finalI).getTag() + "/placesToHeaders")
                                                                        .setValue(json1.toString());
                                                            } catch (JSONException e1) {
                                                                e1.printStackTrace();
                                                            }
//

                                                            try {

                                                                sendNotification(tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getString("tripName"),"added",tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()));



                                                                Snackbar.make(findViewById(android.R.id.content), "saved to "+tripJsonHashMap.get(tripListContainer.getChildAt(finalI).getTag()).getString("tripName"), 5000)
                                                                        .setAction("View", new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View view) {
                                                                                Intent intent = new Intent(getApplicationContext(),TripInfoActivity.class);
                                                                                intent.putExtra("selectedTripId", String.valueOf(tripListContainer.getChildAt(finalI).getTag()));
                                                                                intent.putExtra("selectedTripUserId", sharedByUserIdArray.get(tripListContainer.getChildAt(finalI).getId()));
                                                                                startActivity(intent);
                                                                            }
                                                                        }).setActionTextColor(getResources().getColor(android.R.color.holo_green_light ))
                                                                        .show();
                                                            } catch (JSONException ex) {
                                                                ex.printStackTrace();
                                                            }

                                                        }


                                                    }
                                                });
                                            }
//                                        }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        Log.i("sharedByUserId", sharedByUserId);
                        userInt++;

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    public void createTripListItem(String name, String key, int userId, final JSONObject tripJson){

        LinearLayout listItemBlock = new LinearLayout(this);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listItemBlock.setLayoutParams(params1);
        ViewGroup.MarginLayoutParams params2 = new ViewGroup.MarginLayoutParams(listItemBlock.getLayoutParams());
        params2.setMargins(0, 0,0, 10);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(params2);
        listItemBlock.setLayoutParams(layoutParams1);
        listItemBlock.setOrientation(LinearLayout.HORIZONTAL);
        listItemBlock.setPadding(40,40,40,40);
        listItemBlock.setBackgroundColor(Color.parseColor("#E8EAF6"));
        listItemBlock.setElevation(3);
        listItemBlock.setGravity(Gravity.CENTER_VERTICAL);
        listItemBlock.setTag(key);
        listItemBlock.setId(userId);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(120, 120);
        imageView.setLayoutParams(params3);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.nat4);

        TextView textView =new TextView(this);
        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params4);
        ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(textView.getLayoutParams());
        params5.setMargins(40, 0,0, 0);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
        textView.setLayoutParams(layoutParams2);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setText(name.toUpperCase());

        listItemBlock.addView(imageView);
        listItemBlock.addView(textView);


        try {
            for (Iterator<String> it = tripJson.getJSONObject("sharedWith").keys(); it.hasNext(); ) {
                final String userKey = it.next();
                Log.i("createTripCardLayout: ",tripJson.getJSONObject("sharedWith").getString(userKey));


                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference ref = database.getReference("users/"+tripJson.getJSONObject("sharedWith").getString(userKey));
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> userHash = (HashMap<String,Object>) dataSnapshot.getValue();

                        if(String.valueOf(userHash.get("photoUrl")) != "null") {

                            try {
                                if(tripJson.getJSONObject("sharedWith").getString(userKey).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    currentUserPhotoUrl = String.valueOf(userHash.get("photoUrl"));
                                    ref.removeEventListener(this);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }




        tripListContainer.addView(listItemBlock);




    }
}
