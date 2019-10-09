package com.project.sdl.tripplanner.HomePackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.sdl.tripplanner.AuthPackage.User;
import com.project.sdl.tripplanner.HomePackage.AutoSuggestPackage.MainSearchActivity;
import com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage.PlaceInfo;
import com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage.ShowAllReviews;
import com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage.WritePlaceReview;
import com.project.sdl.tripplanner.ObjectSerializer;
import com.project.sdl.tripplanner.R;
import com.project.sdl.tripplanner.TripsPackage.CreateTripFormActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class HomeFragment1 extends Fragment {

    LinearLayout searchIcon;
    RatingBar ratingBar;
    TextView countText;
    ImageView homebg;
    ImageView currentLocationIcon;
    ImageView blurBg;
    LinearLayout placesHolder;
    LinearLayout placeLayout1;
    RelativeLayout introLayout;

    LinearLayout userReviewContainer;

    SwipeRefreshLayout mySwipeRefreshLayout;

    SharedPreferences sharedPreferences;
    SharedPreferences pref;

    ShimmerLayout shimmerLayout1;
    ShimmerLayout shimmerLayout2;

    ScrollView shimmer_scroll2;
    ScrollView main_scroll;


    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;

    String currentPlaceId;
    JSONObject currentLocation;
    JSONObject currentPlace;
    ArrayList<String> keysetArray;
    ArrayList<byte[]> placesImagesArray;
    ArrayList<String> placesArray;
    ArrayList<byte[]> homeBgArray;
    TextView currentCityName;
    Boolean isFABOpen = false;
    int onCompleteCount = 0;

    FirebaseStorage storage;

    Boolean placeSelected = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, null);
        searchIcon = root.findViewById(R.id.searchIcon);
        ratingBar = root.findViewById(R.id.ratingBar);
        countText = root.findViewById(R.id.countText);
        homebg = root.findViewById(R.id.homebg);
        shimmerLayout1 = root.findViewById(R.id.shimmer_layout1);
        shimmerLayout2 = root.findViewById(R.id.shimmer_layout2);
        shimmer_scroll2 = root.findViewById(R.id.shimmer_scroll2);
        currentCityName = root.findViewById(R.id.currentCityName);
        currentLocationIcon = root.findViewById(R.id.currentLocationIcon);
        blurBg = root.findViewById(R.id.blurBg);
        userReviewContainer = root.findViewById(R.id.userReviewsContainer);
        storage = FirebaseStorage.getInstance();
        placesHolder = root.findViewById(R.id.placesHolder1);
        placesImagesArray = new ArrayList<byte[]>();
        placesArray = new ArrayList<String>();
        fab = root.findViewById(R.id.fab);
        fab1 = root.findViewById(R.id.fab1);
        fab2 = root.findViewById(R.id.fab2);
        main_scroll = root.findViewById(R.id.mainScroll);
        mySwipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        placeLayout1 = root.findViewById(R.id.placeLayout1);
        introLayout = root.findViewById(R.id.introLayout);


        sharedPreferences = getContext().getSharedPreferences("com.project.sdl.tripplanner", Context.MODE_PRIVATE);
        pref = this.getActivity().getSharedPreferences(FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);


        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("onRefresh: ","Hurrah");
                        sharedPreferences.edit().remove("placesImagesArray").commit();
                        sharedPreferences.edit().remove("homeBg").commit();
                        startShimmerEffect();
                        startShimmerEffect2();
                        keepTrackOfUserData();

                    }
                }
        );


        currentCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),SetCurrentLocation.class);
                startActivity(intent);
            }
        });

        currentLocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),SetCurrentLocation.class);
                startActivity(intent);
            }
        });


        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().remove("placesImagesArray").commit();
                sharedPreferences.edit().remove("homeBg").commit();
                Intent intent = new Intent(getContext(), MainSearchActivity.class);
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    isFABOpen=true;
                    fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
                    if(placeSelected) {
                        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
                    }
                    blurBg.setVisibility(View.VISIBLE);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.multiply));
                }else{
                    isFABOpen=false;
                    fab1.animate().translationY(0);
                    fab2.animate().translationY(0);
                    blurBg.setVisibility(View.INVISIBLE);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateTripFormActivity.class);
                startActivity(intent);
                isFABOpen=false;
                fab1.animate().translationY(0);
                fab2.animate().translationY(0);
                blurBg.setVisibility(View.INVISIBLE);
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), WritePlaceReview.class);
                intent.putExtra("currentPlaceId",currentPlaceId);
                startActivity(intent);
                isFABOpen=false;
                fab1.animate().translationY(0);
                fab2.animate().translationY(0);
                blurBg.setVisibility(View.INVISIBLE);
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));


            }
        });

//        ==========================================
//        Show All Reviews
//        ==========================================

        userReviewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ShowAllReviews.class);
                intent.putExtra("selectedPlace", currentPlace.toString());
                startActivity(intent);
            }
        });




        startShimmerEffect();
        startShimmerEffect2();
        keepTrackOfUserData();


        return root;
    }

    public void startShimmerEffect(){
        shimmerLayout1.startShimmerAnimation();
        homebg.setVisibility(View.INVISIBLE);
        TextView textView = (TextView) searchIcon.getChildAt(0);
        textView.setText("Where to ?");
        ratingBar.setVisibility(View.INVISIBLE);
        countText.setVisibility(View.INVISIBLE);

    }

    public void stopShimmerEffect(){
        shimmerLayout1.stopShimmerAnimation();
        homebg.setVisibility(View.VISIBLE);
    }

    public void startShimmerEffect2(){
        shimmerLayout2.startShimmerAnimation();
        main_scroll.setVisibility(View.INVISIBLE);
        shimmer_scroll2.setVisibility(View.VISIBLE);
        placeLayout1.setVisibility(View.INVISIBLE);
    }

    public void stopShimmerEffect2(){
        shimmerLayout2.stopShimmerAnimation();
        main_scroll.setVisibility(View.VISIBLE);
        shimmer_scroll2.setVisibility(View.INVISIBLE);
    }



    public void keepTrackOfUserData(){
        // Attach a listener to read the data at our posts reference
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = database.getReference("users/"+user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                startShimmerEffect();
                startShimmerEffect2();
                placesHolder.removeAllViews();
                placesImagesArray.clear();
                placesArray.clear();
                onCompleteCount = 0;


                User currentUserData = dataSnapshot.getValue(User.class);
                Map<String, Object> userHash = (HashMap<String,Object>) dataSnapshot.getValue();
                JSONObject jsonUser = new JSONObject(userHash);

                currentPlaceId = currentUserData.currentPlaceId;
                try {
                    currentLocation = jsonUser.getJSONObject("currentLocation");
                    currentCityName.setText(jsonUser.getString("currentLocationCityName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(currentUserData.currentPlaceId != null) {

                    introLayout.setVisibility(View.INVISIBLE);
                    checkIfPlaceExistInDatabase(currentUserData.currentPlaceId);
                    checkIfSubPlacesExistInDatabase(currentUserData.currentPlaceId);

                }else{
                    stopShimmerEffect();
                    stopShimmerEffect2();
                    mySwipeRefreshLayout.setRefreshing(false);
                    introLayout.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                stopShimmerEffect();
                stopShimmerEffect2();
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void checkIfPlaceExistInDatabase(final String currentPlaceId){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("places/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> autoSuggest = (HashMap<String,Object>) dataSnapshot.getValue();

                if(autoSuggest.containsKey(currentPlaceId)){
                    keepTrackOfPlaceData(currentPlaceId);
                    placeSelected = true;
                    introLayout.setVisibility(View.INVISIBLE);
                }else{
                    stopShimmerEffect();
                    stopShimmerEffect2();
                    mySwipeRefreshLayout.setRefreshing(false);

                    homebg.setImageResource(R.drawable.nat4);
                    ratingBar.setVisibility(View.INVISIBLE);
                    countText.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) searchIcon.getChildAt(0);
                    textView.setText("Where to ?");
                    countText.setText("");
                    placeSelected = false;
                    introLayout.setVisibility(View.VISIBLE);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                mySwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public void keepTrackOfPlaceData(final String currentPlaceId){
        // Attach a listener to read the data at our posts reference
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("places/"+currentPlaceId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> autoSuggest = (HashMap<String, Object>) dataSnapshot.getValue();
                currentPlace = new JSONObject(autoSuggest);

//        ==========================================
//        Show Reviews
//        ==========================================

                try {
                    fetchReviews(currentPlace);

                } catch (Exception e) {
                    e.printStackTrace();

                }

                try {
                    homeBgArray = (ArrayList<byte[]>) ObjectSerializer.deserialize(sharedPreferences.getString("homeBg", ObjectSerializer.serialize(new ArrayList<byte[]>())));
                } catch (IOException e) {
                    e.printStackTrace();

                }

                if(homeBgArray.size() == 0){

                    try {
                        System.out.printf("JSON: %s", currentPlace.toString(2));

                        StorageReference storageRef = storage.getReference();
                        storageRef.child("places/" + currentPlaceId + "/" + currentPlace.getJSONArray("imageRefs").get(0)).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                // Use the bytes to display the image
                                System.out.println(bytes);
                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                homebg.setImageBitmap(bm);
                                homeBgArray.add(bytes);
                                try {
                                    sharedPreferences.edit().putString("homeBg", ObjectSerializer.serialize(homeBgArray)).apply();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ratingBar.setVisibility(View.VISIBLE);
                                countText.setVisibility(View.VISIBLE);

                                try {
                                    TextView textView = (TextView) searchIcon.getChildAt(0);
                                    textView.setText(currentPlace.getString("name"));
                                    ratingBar.setRating(Float.valueOf(currentPlace.getJSONObject("userRatings").getString("average")));
                                    countText.setText(currentPlace.getJSONObject("userRatings").getString("count"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                stopShimmerEffect();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                stopShimmerEffect();
                                mySwipeRefreshLayout.setRefreshing(false);

                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("onDataChange: ","if");

                }else{
                    Log.i("onDataChange: ","else");
                    Bitmap bm = BitmapFactory.decodeByteArray(homeBgArray.get(0), 0, homeBgArray.get(0).length);
                    homebg.setImageBitmap(bm);

                    ratingBar.setVisibility(View.VISIBLE);
                    countText.setVisibility(View.VISIBLE);

                    try {
                        TextView textView = (TextView) searchIcon.getChildAt(0);
                        textView.setText(currentPlace.getString("name"));
                        ratingBar.setRating(Float.valueOf(currentPlace.getJSONObject("userRatings").getString("average")));
                        countText.setText(currentPlace.getJSONObject("userRatings").getString("count"));
                    } catch (JSONException e) {
                        stopShimmerEffect();
                        e.printStackTrace();
                    }


                    stopShimmerEffect();
//                    mySwipeRefreshLayout.setRefreshing(false);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void checkIfSubPlacesExistInDatabase(final String currentPlaceId){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("sub-places/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> autoSuggest = (HashMap<String,Object>) dataSnapshot.getValue();

                if(autoSuggest.containsKey(currentPlaceId)){

                    DatabaseReference ref = database.getReference("sub-places/"+currentPlaceId);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Map<String, Object> autoSuggest = (HashMap<String,Object>) dataSnapshot.getValue();

                            keepTrackOfSubPlacesData(currentPlaceId,autoSuggest.keySet());
                            placeLayout1.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }else{
                    stopShimmerEffect2();
                    mySwipeRefreshLayout.setRefreshing(false);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void keepTrackOfSubPlacesData(final String currentPlaceId, final Set<String> keySet){


        keysetArray = new ArrayList<>(keySet);

        for(int i=0;i<keysetArray.size();i++) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("sub-places/"+ currentPlaceId +"/"+keysetArray.get(i)+"/places");
            final int finalI = i;
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> autoSuggest = (HashMap<String, Object>) dataSnapshot.getValue();
                    JSONObject currentPlaceNearYou = new JSONObject(autoSuggest);

                    loadSubPlacesScroll(finalI, keysetArray.get(finalI), currentPlaceId,currentPlaceNearYou);



            }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }



    }

    public void loadSubPlacesScroll(final int index, final String id, final String currentPlaceId, final JSONObject currentPlaceNearYou){


        if(getContext() != null) {
            final LinearLayout placeBlock = new LinearLayout(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            placeBlock.setLayoutParams(params);
            ViewGroup.MarginLayoutParams paramsh = new ViewGroup.MarginLayoutParams(placeBlock.getLayoutParams());
            paramsh.setMargins(10, 0, 0, 0);
            RelativeLayout.LayoutParams layoutParamsh = new RelativeLayout.LayoutParams(paramsh);
            placeBlock.setLayoutParams(layoutParamsh);
            placeBlock.setOrientation(LinearLayout.VERTICAL);

            final CardView cardView = new CardView(getContext());
            LinearLayout.LayoutParams paramt = new LinearLayout.LayoutParams(400, 400);
            cardView.setLayoutParams(paramt);
            cardView.setRadius(40);

            final ImageView imageItem = new ImageView(getContext());
            imageItem.setScaleType(ImageView.ScaleType.FIT_XY);


            final TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams parmw = new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(parmw);
            textView.setPadding(8, 8, 8, 8);
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);


            try {
                Log.i("placesImagesArray", String.valueOf((ArrayList<byte[]>) ObjectSerializer.deserialize(sharedPreferences.getString("placesImagesArray", ObjectSerializer.serialize(new ArrayList<byte[]>())))));


                if(((ArrayList<byte[]>) ObjectSerializer
                        .deserialize(sharedPreferences
                                .getString("placesImagesArray",
                                        ObjectSerializer
                                                .serialize(new ArrayList<byte[]>())))).size() == 0){


            try {
                Log.i("onDataChange: ","if im");
                System.out.printf("JSON: %s", currentPlaceNearYou.toString(2));
                StorageReference storageRef = storage.getReference();

                storageRef.child("places/" + id + "/" + currentPlaceNearYou.getJSONArray("imageRefs").get(0)).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Use the bytes to display the image
                        System.out.println(bytes);
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageItem.setImageBitmap(bm);
                        cardView.addView(imageItem);
                        placeBlock.addView(cardView);
                        placeBlock.setTag(currentPlaceNearYou.toString());
                        try {
                            textView.setText(currentPlaceNearYou.getString("name"));
                            placeBlock.addView(textView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        placesImagesArray.add(bytes);
                        placesArray.add(currentPlaceNearYou.toString());
                        placeBlock.setId(placesImagesArray.indexOf(bytes));

                        placesHolder.addView(placeBlock);

                        onCompleteCount++;
                        Log.i("count", String.valueOf(onCompleteCount));

                        if (onCompleteCount == keysetArray.size()) {
                            for (int i = 0; i < placesHolder.getChildCount(); i++) {
                                final int finalI = i;
                                placesHolder.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.i("onClick: ", String.valueOf(view.getTag()));
                                        Intent intent = new Intent(getContext(), PlaceInfo.class);
                                        intent.putExtra("selectedPlace", String.valueOf(placesHolder.getChildAt(finalI).getTag()));
                                        intent.putExtra("selectedPlaceImage", placesImagesArray.get(placesHolder.getChildAt(finalI).getId()));
                                        intent.putExtra("currentParentId", currentPlaceId);
                                        try {
                                            intent.putExtra("currentLatitude", currentLocation.getString("latitude"));
                                            intent.putExtra("currentLongitude", currentLocation.getString("longitude"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(intent);
                                    }
                                });
                            }

                            Log.i("shimmer","stopped");

                            try {
                                sharedPreferences.edit().putString("placesImagesArray", ObjectSerializer.serialize(placesImagesArray)).apply();
                                sharedPreferences.edit().putString("placesArray", ObjectSerializer.serialize(placesArray)).apply();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            stopShimmerEffect2();
                            mySwipeRefreshLayout.setRefreshing(false);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        stopShimmerEffect2();

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
                stopShimmerEffect2();
            }



            } else{
                    placesImagesArray = (ArrayList<byte[]>) ObjectSerializer.deserialize(sharedPreferences.getString("placesImagesArray", ObjectSerializer.serialize(new ArrayList<byte[]>())));
                    placesArray = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("placesArray", ObjectSerializer.serialize(new ArrayList<String>())));
                    Log.i("onDataChange: ","else im");
                    Bitmap bm = BitmapFactory.decodeByteArray(placesImagesArray.get(index), 0, placesImagesArray.get(index).length);

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(placesArray.get(index));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    imageItem.setImageBitmap(bm);
                    cardView.addView(imageItem);
                    placeBlock.addView(cardView);
                    placeBlock.setTag(placesArray.get(index));
                    try {
                        textView.setText(jsonObject.getString("name"));
                        placeBlock.addView(textView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    placeBlock.setId(index);

                    placesHolder.addView(placeBlock);

                    onCompleteCount++;
                    Log.i("count", String.valueOf(onCompleteCount));

                    if (onCompleteCount == keysetArray.size()) {
                        for (int i = 0; i < placesHolder.getChildCount(); i++) {
                            final int finalI = i;
                            placesHolder.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.i("onClick: ", String.valueOf(view.getTag()));
                                    Intent intent = new Intent(getContext(), PlaceInfo.class);
                                    intent.putExtra("selectedPlace", String.valueOf(placesHolder.getChildAt(finalI).getTag()));
                                    intent.putExtra("selectedPlaceImage", placesImagesArray.get(placesHolder.getChildAt(finalI).getId()));
                                    intent.putExtra("currentParentId", currentPlaceId);
                                    try {
                                        intent.putExtra("currentLatitude", currentLocation.getString("latitude"));
                                        intent.putExtra("currentLongitude", currentLocation.getString("longitude"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(intent);
                                }
                            });
                        }

                        Log.i("shimmer","stopped");

                        stopShimmerEffect2();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
            }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public void fetchReviews(JSONObject currentPlace) {

        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(currentPlace));

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref = database.getReference("reviews/"+jsonObject.getString("id"));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> reviewHash = (HashMap<String,Object>) dataSnapshot.getValue();
                    userReviewContainer.removeAllViews();
                    if(reviewHash != null) {
                        JSONObject jsonReview = new JSONObject(reviewHash);

                        int i = 0;
                        for(String key:reviewHash.keySet()){

                            if(i < 3) {
                                try {
                                    Log.i("reviews", jsonReview.getString(key));
                                    createReviewLayout(jsonReview.getJSONObject(key));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                break;
                            }
                            i++;


                        }

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

    public void createReviewLayout(JSONObject reviewJson){

        try{

            if(getContext() != null) {
                CardView cardView = new CardView(getContext());
                LinearLayout.LayoutParams paramsc = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardView.setLayoutParams(paramsc);
                ViewGroup.MarginLayoutParams paramscd = new ViewGroup.MarginLayoutParams(cardView.getLayoutParams());
                paramscd.setMargins(10, 16, 10, 0);
                RelativeLayout.LayoutParams layoutParamscd = new RelativeLayout.LayoutParams(paramscd);
                cardView.setLayoutParams(layoutParamscd);


                //Make review block
                LinearLayout reviewBlock = new LinearLayout(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                reviewBlock.setLayoutParams(params);
                ViewGroup.MarginLayoutParams paramsh = new ViewGroup.MarginLayoutParams(reviewBlock.getLayoutParams());
                paramsh.setMargins(0, 16, 0, 0);
                RelativeLayout.LayoutParams layoutParamsh = new RelativeLayout.LayoutParams(paramsh);
                reviewBlock.setLayoutParams(layoutParamsh);
                reviewBlock.setOrientation(LinearLayout.VERTICAL);

                //Make User block
                LinearLayout userBlock = new LinearLayout(getContext());
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                userBlock.setLayoutParams(params1);
                ViewGroup.MarginLayoutParams paramsu = new ViewGroup.MarginLayoutParams(userBlock.getLayoutParams());
                paramsu.setMargins(16, 16, 0, 0);
                RelativeLayout.LayoutParams layoutParamsu = new RelativeLayout.LayoutParams(paramsu);
                reviewBlock.setLayoutParams(layoutParamsu);
                userBlock.setOrientation(LinearLayout.HORIZONTAL);

                //Make User Circular Image View
                CircularImageView circularImageView = new CircularImageView(getContext());
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(140, 140);
                circularImageView.setLayoutParams(params2);
                String imageCode = pref.getString("image","");

                if(imageCode!=""){
                    byte[] b = Base64.decode(imageCode, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    circularImageView.setImageBitmap(bitmap);

                    Log.d("Profile Image","Selected Profile Image");

                }else{
                    Log.d("Profile Image","Default Profile Image");
                    circularImageView.setImageResource(R.drawable.profile1);
                }



                circularImageView.setBorderColor(Color.parseColor("#eeeeee"));


                //Make container holder for rating and date
                LinearLayout ratingAndDateHolder = new LinearLayout(getContext());
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ratingAndDateHolder.setLayoutParams(params3);
                ratingAndDateHolder.setOrientation(LinearLayout.VERTICAL);


                //Make Star Rating bar
                RatingBar ratingBar = new RatingBar(getContext());
                LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ratingBar.setLayoutParams(params4);
                ratingBar.setIsIndicator(true);
                ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(ratingBar.getLayoutParams());
                params5.setMargins(-120, 0, 0, 0);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(params5);
                ratingBar.setLayoutParams(layoutParams);
                ratingBar.setNumStars(5);
                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                ratingBar.setRating(Float.parseFloat(reviewJson.getString("rating")));
                ratingBar.setScaleX((float) 0.5);
                ratingBar.setScaleY((float) 0.5);
                ratingBar.setScrollBarSize(4);

                //Make date text
                TextView date = new TextView(getContext());
                LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT);
                date.setLayoutParams(params6);
                ViewGroup.MarginLayoutParams paramsd = new ViewGroup.MarginLayoutParams(date.getLayoutParams());
                paramsd.setMargins(0, -32, 0, 0);
                RelativeLayout.LayoutParams layoutParamsd = new RelativeLayout.LayoutParams(paramsd);
                date.setLayoutParams(layoutParamsd);
                date.setText(reviewJson.getString("userName") + " on " + reviewJson.getString("date"));
                date.setTextSize(15);

                ratingAndDateHolder.addView(ratingBar);
                ratingAndDateHolder.addView(date);

                userBlock.addView(circularImageView);
                userBlock.addView(ratingAndDateHolder);

                //Make text view for review title part
                TextView reviewTitle = new TextView(getContext());
                LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                reviewTitle.setLayoutParams(params7);
                ViewGroup.MarginLayoutParams params8 = new ViewGroup.MarginLayoutParams(reviewTitle.getLayoutParams());
                params8.setMargins(16, 5, 0, 0);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(params8);
                reviewTitle.setLayoutParams(layoutParams1);
                reviewTitle.setText(reviewJson.getString("title"));
                reviewTitle.setTextSize(15);
                reviewTitle.setTextColor(Color.BLACK);
                reviewTitle.setTypeface(reviewTitle.getTypeface(), Typeface.BOLD);


                //Make text view for review main content
                TextView reviewMain = new TextView(getContext());
                LinearLayout.LayoutParams params9 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                reviewMain.setLayoutParams(params9);
                ViewGroup.MarginLayoutParams params10 = new ViewGroup.MarginLayoutParams(reviewTitle.getLayoutParams());
                params10.setMargins(16, 5, 0, 16);
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params10);
                reviewMain.setLayoutParams(layoutParams2);
                String trimmedReview = "";
                if (reviewJson.getString("review").length() > 120) {
                    trimmedReview = reviewJson.getString("review").substring(0, 120) + "....";
                } else {
                    trimmedReview = reviewJson.getString("review");
                }
                reviewMain.setText(trimmedReview);
                reviewMain.setTextSize(15);


                reviewBlock.addView(userBlock);
                reviewBlock.addView(reviewTitle);
                reviewBlock.addView(reviewMain);

                cardView.addView(reviewBlock);

                userReviewContainer.addView(cardView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
