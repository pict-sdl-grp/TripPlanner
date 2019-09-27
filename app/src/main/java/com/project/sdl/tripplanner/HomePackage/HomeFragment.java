package com.project.sdl.tripplanner.HomePackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.project.sdl.tripplanner.AuthPackage.User;
import com.project.sdl.tripplanner.HomePackage.AutoSuggestPackage.MainSearchActivity;
import com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage.PlaceInfo;
import com.project.sdl.tripplanner.ObjectSerializer;
import com.project.sdl.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class HomeFragment extends Fragment {

    TextView searchIcon;
    RatingBar ratingBar;
    TextView countText;
    ImageView homebg;
    ImageView scrollImage1;
    ImageView scrollImage2;
    ImageView scrollImage3;
    ImageView scrollImage4;
    ImageView scrollImage5;
    ImageView scrollImage6;
    TextView scrollText1;
    TextView scrollText2;
    TextView scrollText3;
    TextView scrollText4;
    TextView scrollText5;
    TextView scrollText6;
    ImageView currentLocationIcon;

    SharedPreferences sharedPreferences;

    ShimmerLayout shimmerLayout;
    ShimmerLayout shimmerScrollLayout;

    HorizontalScrollView shimmerHolderForScroll1;
    HorizontalScrollView scroll1;

    LinearLayout linearLayout1;

    String currentPlaceId;
    JSONObject currentLocation;
    JSONObject currentPlace;
    JSONObject currentPlaceNearYou;
    ArrayList<String> keysetArray;
    ArrayList<JSONObject> placesArray;
    ArrayList<byte[]> placesImagesArray;
    ArrayList<byte[]> homeBgArray;
    TextView currentCityName;

    FirebaseStorage storage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, null);
        searchIcon = root.findViewById(R.id.searchIcon);
        ratingBar = root.findViewById(R.id.ratingBar);
        countText = root.findViewById(R.id.countText);
        homebg = root.findViewById(R.id.homebg);
        shimmerLayout = root.findViewById(R.id.shimmer_layout);
        shimmerScrollLayout = root.findViewById(R.id.shimmer_scroll_layout);
        linearLayout1 = root.findViewById(R.id.linearLayout1);
        currentCityName = root.findViewById(R.id.currentCityName);
        currentLocationIcon = root.findViewById(R.id.currentLocationIcon);

        shimmerHolderForScroll1 = root.findViewById(R.id.shimmerHolderForScroll1);
        scroll1 = root.findViewById(R.id.scroll1);

        scrollImage1 = root.findViewById(R.id.scrollImage1);
        scrollImage2 = root.findViewById(R.id.scrollImage2);
        scrollImage3 = root.findViewById(R.id.scrollImage3);
        scrollImage4 = root.findViewById(R.id.scrollImage4);
        scrollImage5 = root.findViewById(R.id.scrollImage5);
        scrollImage6 = root.findViewById(R.id.scrollImage6);

        scrollText1 = root.findViewById(R.id.scrollText1);
        scrollText2 = root.findViewById(R.id.scrollText2);
        scrollText3 = root.findViewById(R.id.scrollText3);
        scrollText4 = root.findViewById(R.id.scrollText4);
        scrollText5 = root.findViewById(R.id.scrollText5);
        scrollText6 = root.findViewById(R.id.scrollText6);

        sharedPreferences = getContext().getSharedPreferences("com.project.sdl.tripplanner", Context.MODE_PRIVATE);

        placesArray = new ArrayList<>();

        placesImagesArray = new ArrayList<byte[]>();
        homeBgArray = new ArrayList<byte[]>();


        storage = FirebaseStorage.getInstance();


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

            for(int i=0;i<6;i++) {
                linearLayout1.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("onClick: ", String.valueOf(view.getTag()));
                        Intent intent = new Intent(getContext(), PlaceInfo.class);
                        intent.putExtra("selectedPlace", placesArray.get(Integer.valueOf(String.valueOf(view.getTag()))).toString());
                        intent.putExtra("selectedPlaceImage",placesImagesArray.get(Integer.valueOf(String.valueOf(view.getTag()))));
                        intent.putExtra("currentParentId",currentPlaceId);
                        try {
                            intent.putExtra("currentLatitude",currentLocation.getString("latitude"));
                            intent.putExtra("currentLongitude",currentLocation.getString("longitude"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });
            }




        startShimmerEffect();
        startShimmerEffectOnScroll1();
        keepTrackOfUserData();





        return root;
    }

    public void startShimmerEffect(){
        shimmerLayout.startShimmerAnimation();
        homebg.setVisibility(View.INVISIBLE);
        searchIcon.setText("Where to ?");
        ratingBar.setVisibility(View.INVISIBLE);
        countText.setVisibility(View.INVISIBLE);
    }

    public void stopShimmerEffect(){
        shimmerLayout.stopShimmerAnimation();
        homebg.setVisibility(View.VISIBLE);
    }

    public void startShimmerEffectOnScroll1(){
        shimmerLayout.startShimmerAnimation();
        shimmerHolderForScroll1.setVisibility(View.VISIBLE);
        scroll1.setVisibility(View.INVISIBLE);
    }

    public void stopShimmerEffectOnScroll1(){
        shimmerLayout.stopShimmerAnimation();
        shimmerHolderForScroll1.setVisibility(View.INVISIBLE);
        scroll1.setVisibility(View.VISIBLE);
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
                startShimmerEffectOnScroll1();

                User currentUserData = dataSnapshot.getValue(User.class);
                Map<String, Object> userHash = (HashMap<String,Object>) dataSnapshot.getValue();
                JSONObject jsonUser = new JSONObject(userHash);

                System.out.println(currentUserData.currentPlaceId);
                currentPlaceId = currentUserData.currentPlaceId;
                try {
                    currentLocation = jsonUser.getJSONObject("currentLocation");
                    currentCityName.setText(jsonUser.getString("currentLocationCityName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(currentUserData.currentPlaceId != null) {
                    placesArray.clear();
                    placesImagesArray.clear();
                    byte[] bytes = new byte[10];
                    Arrays.fill( bytes, (byte) 1 );
                    for(int i=0;i<6;i++){
                        placesImagesArray.add(i,bytes);
                    }

                    checkIfPlaceExistInDatabase(currentUserData.currentPlaceId);
                    checkIfPlacesNearYouExistInDatabase(currentUserData.currentPlaceId);
                }else{
                    stopShimmerEffect();
                    stopShimmerEffectOnScroll1();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
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
                }else{
                    homebg.setImageResource(R.drawable.nat4);
                    ratingBar.setVisibility(View.INVISIBLE);
                    countText.setVisibility(View.INVISIBLE);
                    searchIcon.setText("Where to ?");
                    countText.setText("");
                    stopShimmerEffect();
                    stopShimmerEffectOnScroll1();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
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
                                searchIcon.setText(currentPlace.getString("name"));
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
                        searchIcon.setText(currentPlace.getString("name"));
                        ratingBar.setRating(Float.valueOf(currentPlace.getJSONObject("userRatings").getString("average")));
                        countText.setText(currentPlace.getJSONObject("userRatings").getString("count"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    stopShimmerEffect();
            }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void checkIfPlacesNearYouExistInDatabase(final String currentPlaceId){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("placesNearYou/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> autoSuggest = (HashMap<String,Object>) dataSnapshot.getValue();

                if(autoSuggest.containsKey(currentPlaceId)){

                    DatabaseReference ref = database.getReference("placesNearYou/"+currentPlaceId);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Map<String, Object> autoSuggest = (HashMap<String,Object>) dataSnapshot.getValue();
                            for(String id : autoSuggest.keySet()){
                                autoSuggest.get(id);
                            }

                            keepTrackOfPlacesNearYouData(currentPlaceId,autoSuggest.keySet());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                    stopShimmerEffectOnScroll1();
                    scrollImage1.setImageResource(R.drawable.nat4);
                    scrollImage2.setImageResource(R.drawable.nat4);
                    scrollImage3.setImageResource(R.drawable.nat4);
                    scrollImage4.setImageResource(R.drawable.nat4);
                    scrollImage5.setImageResource(R.drawable.nat4);
                    scrollImage6.setImageResource(R.drawable.nat4);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void keepTrackOfPlacesNearYouData(final String currentPlaceId, final Set<String> keySet){



            keysetArray = new ArrayList<>(keySet);

            for(int i=0;i<keysetArray.size();i++) {
                    loadPlacesNearYouScroll(i, keysetArray.get(i), currentPlaceId);
            }



    }

    public void loadPlacesNearYouScroll(final int index, final String id, String currentPlaceId){

        final ArrayList<ImageView> scrollImagesHolder = new ArrayList<>();
        scrollImagesHolder.add(scrollImage1);
        scrollImagesHolder.add(scrollImage2);
        scrollImagesHolder.add(scrollImage3);
        scrollImagesHolder.add(scrollImage4);
        scrollImagesHolder.add(scrollImage5);
        scrollImagesHolder.add(scrollImage6);

        final ArrayList<TextView> scrollTextHolder = new ArrayList<>();
        scrollTextHolder.add(scrollText1);
        scrollTextHolder.add(scrollText2);
        scrollTextHolder.add(scrollText3);
        scrollTextHolder.add(scrollText4);
        scrollTextHolder.add(scrollText5);
        scrollTextHolder.add(scrollText6);

        final Bitmap[] bm = new Bitmap[1];


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("placesNearYou/"+ currentPlaceId +"/"+id+"/places");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> autoSuggest = (HashMap<String, Object>) dataSnapshot.getValue();
                currentPlaceNearYou = new JSONObject(autoSuggest);

                placesArray.add(currentPlaceNearYou);

                try {
                    Log.i("placesImagesArray", String.valueOf((ArrayList<byte[]>) ObjectSerializer.deserialize(sharedPreferences.getString("placesImagesArray", ObjectSerializer.serialize(new ArrayList<byte[]>())))));


                if(((ArrayList<byte[]>) ObjectSerializer
                        .deserialize(sharedPreferences
                                .getString("placesImagesArray",
                                        ObjectSerializer
                                                .serialize(new ArrayList<byte[]>())))).size() == 0){

                try {
                    System.out.printf("JSON: %s", currentPlaceNearYou.toString(2));
                    StorageReference storageRef = storage.getReference();

                    scrollTextHolder.get(index).setText(currentPlaceNearYou.getString("name"));

                    storageRef.child("places/" + id + "/" + currentPlaceNearYou.getJSONArray("imageRefs").get(0)).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Use the bytes to display the image
                            System.out.println(bytes);
                            bm[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            scrollImagesHolder.get(index).setImageBitmap(bm[0]);
                            placesImagesArray.set(index, bytes);

                            Log.i("onSuccess: ", placesImagesArray.toString());

                            if (index == 5) {
                                stopShimmerEffectOnScroll1();
                                try {
                                    sharedPreferences.edit().putString("placesImagesArray", ObjectSerializer.serialize(placesImagesArray)).apply();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });


                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                    Log.i("onDataChange: ","if im");

            }else{
                    placesImagesArray = (ArrayList<byte[]>) ObjectSerializer.deserialize(sharedPreferences.getString("placesImagesArray", ObjectSerializer.serialize(new ArrayList<byte[]>())));
                    Log.i("onDataChange: ","else im");
                    Bitmap bm = BitmapFactory.decodeByteArray(placesImagesArray.get(index), 0, placesImagesArray.get(index).length);
                    scrollImagesHolder.get(index).setImageBitmap(bm);
                    try {
                        scrollTextHolder.get(index).setText(currentPlaceNearYou.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (index == 5) {
                        stopShimmerEffectOnScroll1();
                    }
            }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
