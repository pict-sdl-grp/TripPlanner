package com.project.sdl.tripplanner.HomePackage;

import android.content.Intent;
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
import com.project.sdl.tripplanner.R;

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

    ShimmerLayout shimmerLayout;
    ShimmerLayout shimmerScrollLayout;

    HorizontalScrollView shimmerHolderForScroll1;
    HorizontalScrollView scroll1;

    String currentPlaceId;
    JSONObject currentPlace;
    JSONObject currentPlaceNearYou;

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



        storage = FirebaseStorage.getInstance();

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainSearchActivity.class);
                startActivity(intent);
            }
        });



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

                System.out.println(currentUserData.currentPlaceId);
                currentPlaceId = currentUserData.currentPlaceId;

                if(currentUserData.currentPlaceId != null) {
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

                Map<String, Object> autoSuggest = (HashMap<String,Object>) dataSnapshot.getValue();
                currentPlace = new JSONObject(autoSuggest);

                try {
                    System.out.printf( "JSON: %s", currentPlace.toString(2) );

                    StorageReference storageRef = storage.getReference();
                    storageRef.child("places/"+currentPlaceId+"/"+currentPlace.getJSONArray("imageRefs").get(0)).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Use the bytes to display the image
                            System.out.println(bytes);
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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


        final int[] i = {0};
        final int[] j = {0};
        for(final String id : keySet){
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("placesNearYou/"+currentPlaceId+"/"+id+"/places");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> autoSuggest = (HashMap<String,Object>) dataSnapshot.getValue();
                    currentPlaceNearYou = new JSONObject(autoSuggest);

                    try {
                        System.out.printf( "JSON: %s", currentPlaceNearYou.toString(2) );
                        scrollTextHolder.get(j[0]).setText(currentPlaceNearYou.getString("name"));
                        j[0]++;
                        StorageReference storageRef = storage.getReference();
                        storageRef.child("places/"+id+"/"+currentPlaceNearYou.getJSONArray("imageRefs").get(0)).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                // Use the bytes to display the image
                                System.out.println(bytes);
                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                scrollImagesHolder.get(i[0]).setImageBitmap(bm);

                                i[0]++;


                         if(i[0] == keySet.size()){
                             stopShimmerEffectOnScroll1();
                         }

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

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }


}
