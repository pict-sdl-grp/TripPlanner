package com.project.sdl.tripplanner.TripsPackage;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.sdl.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectTripActivity extends AppCompatActivity {

    LinearLayout tripListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_trip);
        getSupportActionBar().hide();

        tripListContainer = findViewById(R.id.selectTripContainer);


        HashMap<String, ArrayList<String>> tripHash = (HashMap<String, ArrayList<String>>) getIntent().getSerializableExtra("tripHash");
        final JSONObject tripJson = new JSONObject(tripHash);


        final ArrayList<String> placesToTrip = new ArrayList<>();

        for(String key : tripHash.keySet()){
            try {
                createTripListItem(tripJson.getJSONObject(key).getString("tripName"),key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i =0 ;i<getIntent().getStringArrayListExtra("tripNames").size();i++){

            final int finalI = i;
            final int finalI1 = i;
            tripListContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    try {

                        for(int i=0;i<tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").length();i++){
                            placesToTrip.add((String) tripJson.getJSONObject(String.valueOf(tripListContainer.getChildAt(finalI1).getTag())).getJSONArray("placesToTrip").get(i));
                        }

                        if(placesToTrip.contains(getIntent().getStringExtra("currentPlace"))){
                            placesToTrip.remove(getIntent().getStringExtra("currentPlace"));

                            mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/placesToTrip")
                                    .setValue(placesToTrip);

                            mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/noOfItems")
                                    .setValue(placesToTrip.size());

                                    Toast.makeText(SelectTripActivity.this, "removed from "+getIntent().getStringArrayListExtra("tripNames").get(finalI), Toast.LENGTH_SHORT).show();
                                    finish();

                        }else{
                            placesToTrip.add(getIntent().getStringExtra("currentPlace"));
                            mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/placesToTrip")
                                    .setValue(placesToTrip);

                            mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/noOfItems")
                                    .setValue(placesToTrip.size());

                                    Toast.makeText(SelectTripActivity.this, "saved to "+getIntent().getStringArrayListExtra("tripNames").get(finalI), Toast.LENGTH_SHORT).show();
                                    finish();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        placesToTrip.add(getIntent().getStringExtra("currentPlace"));
                        mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/placesToTrip")
                                .setValue(placesToTrip);

                        mDatabase.child("trips/"+user.getUid()+"/" + tripListContainer.getChildAt(finalI1).getTag() + "/noOfItems")
                                .setValue(placesToTrip.size());

                                Toast.makeText(SelectTripActivity.this, "saved to "+getIntent().getStringArrayListExtra("tripNames").get(finalI), Toast.LENGTH_SHORT).show();
                                finish();
                    }








                }
            });

        }

    }

    public void createTripListItem(String name, String key){

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
        listItemBlock.setTag(key);

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

        tripListContainer.addView(listItemBlock);




    }
}
