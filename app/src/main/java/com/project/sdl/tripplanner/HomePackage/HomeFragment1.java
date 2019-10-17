package com.project.sdl.tripplanner.HomePackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.project.sdl.tripplanner.R;
import com.project.sdl.tripplanner.TripsPackage.CreateTripFormActivity;

import org.json.JSONException;
import org.json.JSONObject;

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
    Button welcomeButton;


    LinearLayout userReviewContainer;

    SwipeRefreshLayout mySwipeRefreshLayout;

    SharedPreferences sharedPreferences;

    ShimmerLayout shimmerLayout1;
    ShimmerLayout shimmerLayout2;

    ScrollView shimmer_scroll2;
    ScrollView main_scroll;


    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;

    String currentPlaceId;
    public static JSONObject currentLocation;
    JSONObject currentPlace;
    ArrayList<String> keysetArray;
    ArrayList<String> placesImagesArray;
    ArrayList<String> placesArray;
    ArrayList<String> homeBgArray;
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
        placesImagesArray = new ArrayList<String>();
        placesArray = new ArrayList<String>();
        fab = root.findViewById(R.id.fab);
        fab1 = root.findViewById(R.id.fab1);
        fab2 = root.findViewById(R.id.fab2);
        main_scroll = root.findViewById(R.id.mainScroll);
        mySwipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        placeLayout1 = root.findViewById(R.id.placeLayout1);
        introLayout = root.findViewById(R.id.introLayout);
        welcomeButton = root.findViewById(R.id.welcomeButton);


        sharedPreferences = getContext().getSharedPreferences("com.project.sdl.tripplanner", Context.MODE_PRIVATE);

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


        sharedPreferences.edit().remove("placesImagesArray").commit();
        sharedPreferences.edit().remove("homeBg").commit();
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

                    introLayout.setVisibility(View.VISIBLE);
                    welcomeButton.setText("Data is not available.Try to explore other places");
                    homebg.setImageResource(R.drawable.nat4);
                    ratingBar.setVisibility(View.INVISIBLE);
                    countText.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) searchIcon.getChildAt(0);
                    textView.setText("Where to ?");
                    countText.setText("");
                    placeSelected = false;
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
                        System.out.printf("JSON: %s", currentPlace.toString(2));

                        StorageReference storageRef = storage.getReference();

                        storageRef.child("places/" + currentPlaceId + "/" + currentPlace.getJSONArray("imageRefs").get(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String imageURL = uri.toString();

                                if(getContext() != null) {
                                    Glide.with(getContext())
                                            .load(imageURL)
                                            .into(homebg);
                                }

                                ratingBar.setVisibility(View.VISIBLE);
                                countText.setVisibility(View.VISIBLE);

                                try {
                                    TextView textView = (TextView) searchIcon.getChildAt(0);
                                    textView.setText(currentPlace.getString("name"));
//                                    ratingBar.setRating(Float.valueOf(currentPlace.getJSONObject("userRatings").getString("average")));
//                                    countText.setText(currentPlace.getJSONObject("userRatings").getString("count"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                stopShimmerEffect();
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("onDataChange: ","if");


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
                    introLayout.setVisibility(View.VISIBLE);
                    welcomeButton.setText("Data is not available.Try to explore other places");
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
            LinearLayout.LayoutParams paramt = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.place_image_width), (int) getResources().getDimension(R.dimen.place_image_width));
            cardView.setLayoutParams(paramt);
            cardView.setRadius(40);

            final ImageView imageItem = new ImageView(getContext());
            imageItem.setScaleType(ImageView.ScaleType.FIT_XY);


            final TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams parmw = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.place_image_width), ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(parmw);
            textView.setPadding(8, 8, 8, 8);
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);

            try {
                Log.i("onDataChange: ", "if im");
                System.out.printf("JSON: %s", currentPlaceNearYou.toString(2));
                StorageReference storageRef = storage.getReference();

                storageRef.child("places/" + id + "/" + currentPlaceNearYou.getJSONArray("imageRefs").get(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String imageURL = uri.toString();

                        if(getContext() != null) {
                            Glide.with(getContext())
                                    .load(imageURL)
                                    .into(imageItem);
                        }

                        cardView.addView(imageItem);
                        placeBlock.addView(cardView);
                        placeBlock.setTag(currentPlaceNearYou.toString());
                        try {
                            textView.setText(currentPlaceNearYou.getString("name"));
                            placeBlock.addView(textView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        placesImagesArray.add(imageURL);
                        placesArray.add(currentPlaceNearYou.toString());
                        placeBlock.setId(placesImagesArray.indexOf(imageURL));

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

                            Log.i("shimmer", "stopped");

                            stopShimmerEffect2();
                            mySwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
                stopShimmerEffect2();
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
                        double rating = 0.0;
                        for(String key:reviewHash.keySet()){

                                try {
                                    Log.i("reviews", jsonReview.getString(key));
                                    createReviewLayout(jsonReview.getJSONObject(key));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            try {
                                rating = rating + Double.parseDouble(jsonReview.getJSONObject(key).getString("rating"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            i++;



                        }

                        ratingBar.setRating(Float.parseFloat(String.valueOf((float) (rating/(i)))));
                        countText.setText(String.valueOf(i)+" Reviews");

                    }else{
                        ratingBar.setRating(0);
                        countText.setText("");
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
                paramscd.setMargins((int) getResources().getDimension(R.dimen.card_review_marginLeft), (int) getResources().getDimension(R.dimen.card_review_marginTop), (int) getResources().getDimension(R.dimen.card_review_marginRight), 0);
                RelativeLayout.LayoutParams layoutParamscd = new RelativeLayout.LayoutParams(paramscd);
                cardView.setLayoutParams(layoutParamscd);


                //Make review block
                LinearLayout reviewBlock = new LinearLayout(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                reviewBlock.setLayoutParams(params);
                ViewGroup.MarginLayoutParams paramsh = new ViewGroup.MarginLayoutParams(reviewBlock.getLayoutParams());
                paramsh.setMargins(0, (int) getResources().getDimension(R.dimen.card_review__block_marginTop), 0, 0);
                RelativeLayout.LayoutParams layoutParamsh = new RelativeLayout.LayoutParams(paramsh);
                reviewBlock.setLayoutParams(layoutParamsh);
                reviewBlock.setOrientation(LinearLayout.VERTICAL);

                //Make User block
                LinearLayout userBlock = new LinearLayout(getContext());
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                userBlock.setLayoutParams(params1);
                ViewGroup.MarginLayoutParams paramsu = new ViewGroup.MarginLayoutParams(userBlock.getLayoutParams());
                paramsu.setMargins((int) getResources().getDimension(R.dimen.card_review_user_marginLeft), (int) getResources().getDimension(R.dimen.card_review_user_marginTop), 0, 0);
                RelativeLayout.LayoutParams layoutParamsu = new RelativeLayout.LayoutParams(paramsu);
                reviewBlock.setLayoutParams(layoutParamsu);
                userBlock.setOrientation(LinearLayout.HORIZONTAL);
                userBlock.setGravity(Gravity.CENTER_VERTICAL);

                //Make User Circular Image View
                final CircularImageView circularImageView = new CircularImageView(getContext());
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.card_review_image_width), (int) getResources().getDimension(R.dimen.card_review_image_height));
                circularImageView.setLayoutParams(params2);
                circularImageView.setBorderColor(Color.parseColor("#eeeeee"));
                ViewGroup.MarginLayoutParams paramsl = new ViewGroup.MarginLayoutParams(circularImageView.getLayoutParams());
                paramsl.setMargins((int) getResources().getDimension(R.dimen.card_review_image_marginLeft), (int) getResources().getDimension(R.dimen.card_review_image_marginTop), 0, 0);
                RelativeLayout.LayoutParams layoutParaml = new RelativeLayout.LayoutParams(paramsl);
                circularImageView.setLayoutParams(layoutParaml);
                circularImageView.setImageResource(R.drawable.profile1);

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference ref = database.getReference("users/"+reviewJson.getString("userId"));
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> userHash = (HashMap<String,Object>) dataSnapshot.getValue();

                        if(String.valueOf(userHash.get("photoUrl")) != "null") {
                            Log.i("photoUrlooo", String.valueOf(userHash.get("photoUrl")));

                            if(getContext() != null) {
                                Glide.with(getContext())
                                        .load(String.valueOf(userHash.get("photoUrl")))
                                        .thumbnail(Glide.with(getContext()).load(R.drawable.profile1))
                                        .into(circularImageView);
                            }


                            ref.removeEventListener(this);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



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
                params5.setMargins((int) getResources().getDimension(R.dimen.card_review_ratingBar_marginLeft), 0, 0, 0);
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
                LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                date.setLayoutParams(params6);
                ViewGroup.MarginLayoutParams paramsd = new ViewGroup.MarginLayoutParams(date.getLayoutParams());
                paramsd.setMargins(0, (int) getResources().getDimension(R.dimen.card_review_date_marginTop), 0, 0);
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
                params8.setMargins((int) getResources().getDimension(R.dimen.card_review_title_marginLeft), (int) getResources().getDimension(R.dimen.card_review_title_marginTop), 0, 0);
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
                params10.setMargins((int) getResources().getDimension(R.dimen.card_review_main_marginLeft), (int) getResources().getDimension(R.dimen.card_review_main_marginTop), 0, (int) getResources().getDimension(R.dimen.card_review_main_marginBottom));
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
