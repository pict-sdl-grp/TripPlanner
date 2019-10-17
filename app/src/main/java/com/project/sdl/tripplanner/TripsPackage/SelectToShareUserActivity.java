package com.project.sdl.tripplanner.TripsPackage;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.sdl.tripplanner.NotificationPackage.Notification;
import com.project.sdl.tripplanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SelectToShareUserActivity extends AppCompatActivity {

    LinearLayout userListContainer;
    JSONObject userJson;
    Boolean isAlreadyShared=false;
    String currentUserName = "";
    String currentUserPhotoUrl = "";
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_to_share_user);
        userListContainer = findViewById(R.id.selectUserContainer);
        getSupportActionBar().hide();
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final int[] checkInt = {0};
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = database.getReference("users/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    Map<String, Object> userHash = (HashMap<String, Object>) dataSnapshot.getValue();
                    userJson = new JSONObject(userHash);

                    Log.i("userJson",userJson.toString());

                    for (Iterator<String> it = userJson.keys(); it.hasNext(); ) {
                        String userKey = it.next();

                        try {
                            JSONObject jsonObject = new JSONObject(userJson.getString(userKey));

                            if(checkInt[0] == 0) {
                                if (user.getUid().equals(userKey)) {

                                    currentUserName = jsonObject.getString("username");
                                    try{
                                        currentUserPhotoUrl = jsonObject.getString("photoUrl");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    createUserListItem(jsonObject.getString("username"), userKey, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    for(int i =0 ;i<userListContainer.getChildCount();i++){
                        final int finalI = i;
                        userListContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkInt[0] = 1;
                                fetchFromSharedUsers("fetch", (String) userListContainer.getChildAt(finalI).getTag());

                            }
                        });
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    public void fetchFromSharedUsers(String params, final String paramKey){
        isAlreadyShared = false;
        final int[] checkInt = {0};
        final String[] p1 = {params};
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = database.getReference("sharedTrips/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null && String.valueOf(p1[0]) == "fetch" && checkInt[0] == 0) {
                    Map<String, Object> hash1 = (HashMap<String, Object>) dataSnapshot.getValue();
                    JSONArray hash1JsonArray = new JSONArray(hash1.values());


                    ArrayList<String> keyset1 = new ArrayList<>(hash1.keySet());

                    for(String key1:keyset1) {
                        Log.i("check1", key1);

                        if (key1.equals(paramKey)){

                            for (int i = 0; i < hash1JsonArray.length(); i++) {
                                try {
                                    JSONObject jsonPart = hash1JsonArray.getJSONObject(i);
                                    for (Iterator<String> it = jsonPart.keys(); it.hasNext(); ) {
                                        String parentKey = it.next();
                                        final String currentUserId = parentKey.split("separator")[0];
                                        final String sharedByUserId = parentKey.split("separator")[1];

                                        Log.i("check3", parentKey);
                                        Log.i("check4", jsonPart.getString(parentKey));
                                        JSONObject jsonPart2 = new JSONObject(jsonPart.getString(parentKey));

                                        for (Iterator<String> it1 = jsonPart2.keys(); it1.hasNext(); ) {
                                            String tripKey = it1.next();

                                            if (getIntent().getStringExtra("selectedTripId").equals(jsonPart2.getString(tripKey))) {
                                                isAlreadyShared = true;
                                                checkInt[0] = 1;
                                                Log.i("already", "true");
                                                Toast.makeText(SelectToShareUserActivity.this, "Already Shared!!!", Toast.LENGTH_LONG).show();
                                                p1[0] = null;
                                            }else{
                                                Log.i("yes","no");
                                            }
                                        }


                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.i("yes","l");
                                }


                            }

                        }



                    }

                    if(!isAlreadyShared){
                        Log.i("yes","yes");
                        checkInt[0] = 1;
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        String id  = formatter.format(date).substring(11);


                        Notification notification = new Notification(id,"share",
                                (user.getDisplayName() +" <b>shared</b> a trip named <b>" +getIntent().getStringExtra("selectedTripName")+"</b> with you."),
                                formatter.format(date),currentUserName,currentUserPhotoUrl, ServerValue.TIMESTAMP,false);

                        mDatabase.child("notifications/").child(paramKey).child(id).setValue(notification);


                        mDatabase.child("sharedTrips/"+paramKey+"/"+(paramKey+"separator"+user.getUid())).push().setValue(getIntent().getStringExtra("selectedTripId"));
                        mDatabase.child("trips/"+user.getUid()+"/"+getIntent().getStringExtra("selectedTripId")+"/sharedWith").push().setValue(paramKey);
                        Toast.makeText(SelectToShareUserActivity.this, "Shared successfully!!!", Toast.LENGTH_LONG).show();
                    }

                }else{
                    checkInt[0] = 1;
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String id  = formatter.format(date).substring(11);

                    Notification notification = new Notification(id,"share",
                            (user.getDisplayName() +" <b>shared</b> a trip named <b>" +getIntent().getStringExtra("selectedTripName")+"</b> with you."),
                            formatter.format(date),currentUserName,currentUserPhotoUrl,ServerValue.TIMESTAMP,false);

                    mDatabase.child("notifications/").child(paramKey).child(id).setValue(notification);

                    mDatabase.child("sharedTrips/"+paramKey+"/"+(paramKey+"separator"+user.getUid())).push().setValue(getIntent().getStringExtra("selectedTripId"));
                    mDatabase.child("trips/"+user.getUid()+"/"+getIntent().getStringExtra("selectedTripId")+"/sharedWith").push().setValue(paramKey);
                    Toast.makeText(SelectToShareUserActivity.this, "Shared successfully!!!", Toast.LENGTH_LONG).show();
                }

                ref.removeEventListener(this);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void createUserListItem(String name, String key,JSONObject jsonObject){

        LinearLayout listItemBlock = new LinearLayout(this);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listItemBlock.setLayoutParams(params1);
        ViewGroup.MarginLayoutParams params2 = new ViewGroup.MarginLayoutParams(listItemBlock.getLayoutParams());
        params2.setMargins(0, 0,0, 10);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(params2);
        listItemBlock.setLayoutParams(layoutParams1);
        listItemBlock.setOrientation(LinearLayout.HORIZONTAL);
        listItemBlock.setPadding(40,40,40,40);
        listItemBlock.setBackgroundColor(Color.WHITE);
        listItemBlock.setElevation(3);
        listItemBlock.setGravity(Gravity.CENTER_VERTICAL);
        listItemBlock.setTag(key);

        final CircularImageView imageView = new CircularImageView(getApplicationContext());
        LinearLayout.LayoutParams paramsw = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.card_review_image_width), (int) getResources().getDimension(R.dimen.card_review_image_height));
        imageView.setLayoutParams(paramsw);
        imageView.setBorderColor(Color.parseColor("#eeeeee"));
//        ViewGroup.MarginLayoutParams paramsl = new ViewGroup.MarginLayoutParams(imageView.getLayoutParams());
//        paramsl.setMargins((int) getResources().getDimension(R.dimen.card_review_image_marginLeft), (int) getResources().getDimension(R.dimen.card_review_image_marginTop), 0, 0);
//        RelativeLayout.LayoutParams layoutParaml = new RelativeLayout.LayoutParams(paramsl);
//        imageView.setLayoutParams(layoutParaml);
        imageView.setImageResource(R.drawable.profile1);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("users/"+key);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> userHash = (HashMap<String,Object>) dataSnapshot.getValue();

                if(String.valueOf(userHash.get("photoUrl")) != "null") {
                    Log.i("photoUrlooo", String.valueOf(userHash.get("photoUrl")));

                    Glide.with(getApplicationContext())
                            .load(String.valueOf(userHash.get("photoUrl")))
                            .thumbnail(Glide.with(getApplicationContext()).load(R.drawable.profile1))
                            .into(imageView);


                    ref.removeEventListener(this);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        TextView textView =new TextView(this);
        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params4);
        ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(textView.getLayoutParams());
        params5.setMargins(40, 0,0, 0);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
        textView.setLayoutParams(layoutParams2);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        String name1 = name.toUpperCase() ;
        try {
            name1 = name1 + "  ("+jsonObject.getString("phoneNo")+")";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView.setText(name1);


        listItemBlock.addView(imageView);
        listItemBlock.addView(textView);




        userListContainer.addView(listItemBlock);




    }
}
