package com.project.sdl.tripplanner.TripsPackage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;
import com.project.sdl.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SharedTripsActivity extends AppCompatActivity {

    LinearLayout tripCardHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_trips);
        getSupportActionBar().hide();

        tripCardHolder = findViewById(R.id.tripCardHolder);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = database.getReference("sharedTrips/"+user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    tripCardHolder.removeAllViews();
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
                            String key = it.next();

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
                                            final JSONObject tripJson = new JSONObject(tripHash);
                                            Log.i("tripHash1", tripHash.toString());


//                                            int p = 0;
//                                            for (String key : tripHash.keySet()) {
                                            Log.i("tripHash2", "pppppp");
//                                                try {
                                            createTripCardLayout(tripJson, finalUserInt);
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                                p++;

                                            for (int i = 0; i < tripCardHolder.getChildCount(); i++) {
                                                final int finalI = i;
                                                tripCardHolder.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent intent = new Intent(getApplicationContext(), TripInfoActivity.class);
                                                        intent.putExtra("selectedTripId", String.valueOf(tripCardHolder.getChildAt(finalI).getTag()));
                                                        intent.putExtra("selectedTripUserId", sharedByUserIdArray.get(tripCardHolder.getChildAt(finalI).getId()));
                                                        startActivity(intent);
                                                        finish();
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

    public void createTripCardLayout(JSONObject tripJson,int userInt){

        CardView cardView = new CardView(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
        cardView.setLayoutParams(params);
        ViewGroup.MarginLayoutParams params1 = new ViewGroup.MarginLayoutParams(cardView.getLayoutParams());
        params1.setMargins(40, 0, 40, 40);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(params1);
        cardView.setLayoutParams(layoutParams);
        try{
            cardView.setTag(tripJson.getString("id"));
            cardView.setId(userInt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView imageView = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
        imageView.setLayoutParams(params2);
        imageView.setImageResource(R.drawable.nat4);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        TextView tripName = new TextView(getApplicationContext());
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tripName.setLayoutParams(params3);
        ViewGroup.MarginLayoutParams params4 = new ViewGroup.MarginLayoutParams(tripName.getLayoutParams());
        params4.setMargins(20, 340,0, 0);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(params4);
        tripName.setLayoutParams(layoutParams1);
        try {
            tripName.setText(tripJson.getString("tripName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tripName.setTypeface(tripName.getTypeface(), Typeface.BOLD);
        tripName.setTextColor(Color.parseColor("#424242"));
        tripName.setTextSize(19);

        TextView creatorName = new TextView(getApplicationContext());
        LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        creatorName.setLayoutParams(params5);
        ViewGroup.MarginLayoutParams params6 = new ViewGroup.MarginLayoutParams(creatorName.getLayoutParams());
        params6.setMargins(20, 400,0, 0);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params6);
        creatorName.setLayoutParams(layoutParams2);
        try {
            creatorName.setText("By "+tripJson.getString("createdBy"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        creatorName.setTextSize(14);

        TextView itemsCount = new TextView(getApplicationContext());
        LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemsCount.setLayoutParams(params7);
        ViewGroup.MarginLayoutParams params8 = new ViewGroup.MarginLayoutParams(itemsCount.getLayoutParams());
        params8.setMargins(20, 480,0, 0);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(params8);
        itemsCount.setLayoutParams(layoutParams3);
        try {
            itemsCount.setText("Featuring: "+tripJson.getString("noOfItems")+" items");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        itemsCount.setTextSize(14);


        cardView.addView(imageView);
        cardView.addView(tripName);
        cardView.addView(creatorName);
        cardView.addView(itemsCount);

        tripCardHolder.addView(cardView);




    }
}
