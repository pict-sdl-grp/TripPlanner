package com.project.sdl.tripplanner.TripsPackage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.sdl.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class TripsFragment extends Fragment {

    LinearLayout tripCardHolder;
    FloatingActionButton fab1;
    boolean isFABOpen = false;
    RelativeLayout firstMessage;
    Button getStarted;
    FloatingActionButton fab;
    LinearLayout getStartedContainer;
    TextView shareText;
    String currentUserPhotoUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trips, null);
        tripCardHolder = root.findViewById(R.id.tripCardHolder);
        fab = root.findViewById(R.id.fab);
        fab1 = root.findViewById(R.id.fab1);
        firstMessage = root.findViewById(R.id.firstMessage);
        getStarted = root.findViewById(R.id.getStartedButton);
        getStartedContainer = root.findViewById(R.id.getStartedContainer);
        shareText = root.findViewById(R.id.shareText);


        shareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),SharedTripsActivity.class);
                startActivity(intent);
            }
        });

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),CreateTripFormActivity.class);
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),CreateTripFormActivity.class);
                startActivity(intent);
                closeFABMenu();
            }
        });


        fetchTrips();



        return root;
    }

    public void fetchTrips(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = database.getReference("trips/"+user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> tripHash = (HashMap<String,Object>) dataSnapshot.getValue();
                if(tripHash != null) {
                    final JSONObject tripJson = new JSONObject(tripHash);
                    Log.i("tripHash",tripHash.toString());
                    tripCardHolder.removeAllViews();
                    getStartedContainer.setVisibility(View.INVISIBLE);

                    if(getContext() != null) {
                        int p = 0;
                        for (String key : tripHash.keySet()) {
                            try {
                                createTripCardLayout(tripJson.getJSONObject(key), key, p, tripHash.keySet().size() - 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            p++;
                        }

                        for (int i = 0; i < tripCardHolder.getChildCount(); i++) {
                            final int finalI = i;
                            tripCardHolder.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), TripInfoActivity.class);
                                    intent.putExtra("selectedTripId", String.valueOf(tripCardHolder.getChildAt(finalI).getTag()));
                                    intent.putExtra("currentUserPhotoUrl", currentUserPhotoUrl);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                }else{
                    tripCardHolder.removeAllViews();
                    getStartedContainer.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        firstMessage.setAlpha((float) 0.3);
        firstMessage.setBackgroundColor(Color.parseColor("#68C4A5"));
        fab.setImageDrawable(getResources().getDrawable(R.drawable.multiply));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        firstMessage.setAlpha(1);
        firstMessage.setBackgroundColor(Color.TRANSPARENT);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
    }


    public void createTripCardLayout(final JSONObject tripJson, String key, int p, int i){

        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.card_height));
        cardView.setLayoutParams(params);
        ViewGroup.MarginLayoutParams params1 = new ViewGroup.MarginLayoutParams(cardView.getLayoutParams());
        if(p == i) {
            params1.setMargins((int) getResources().getDimension(R.dimen.card_marginLeft), 0, (int) getResources().getDimension(R.dimen.card_marginRight), (int) getResources().getDimension(R.dimen.card_marginBottom2));
        }else{
            params1.setMargins((int) getResources().getDimension(R.dimen.card_marginLeft), 0, (int) getResources().getDimension(R.dimen.card_marginRight), (int) getResources().getDimension(R.dimen.card_marginBottom1));
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(params1);
        cardView.setLayoutParams(layoutParams);
        cardView.setTag(key);

        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension((R.dimen.card_image_height)));
        imageView.setLayoutParams(params2);
        imageView.setImageResource(R.drawable.nat4);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        TextView tripName = new TextView(getContext());
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tripName.setLayoutParams(params3);
        ViewGroup.MarginLayoutParams params4 = new ViewGroup.MarginLayoutParams(tripName.getLayoutParams());
        params4.setMargins((int) getResources().getDimension(R.dimen.card_tripName_marginLeft),(int) getResources().getDimension( R.dimen.card_tripName_marginTop),0, 0);
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

        TextView creatorName = new TextView(getContext());
        LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        creatorName.setLayoutParams(params5);
        ViewGroup.MarginLayoutParams params6 = new ViewGroup.MarginLayoutParams(creatorName.getLayoutParams());
        params6.setMargins((int) getResources().getDimension(R.dimen.card_createdBy_marginLeft),(int) getResources().getDimension( R.dimen.card_createdBy_marginTop),0, 0);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params6);
        creatorName.setLayoutParams(layoutParams2);
        creatorName.setText("By "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        creatorName.setTextSize(14);

        final LinearLayout sharedUsersHolder = new LinearLayout(getContext());
        LinearLayout.LayoutParams paramsy = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.card_sharedUserHolder_image_height));
        sharedUsersHolder.setLayoutParams(paramsy);
        ViewGroup.MarginLayoutParams paramsi = new ViewGroup.MarginLayoutParams(sharedUsersHolder.getLayoutParams());
        paramsi.setMargins((int) getResources().getDimension(R.dimen.card_sharedUserHolder_image_marginLeft),(int) getResources().getDimension( R.dimen.card_sharedUserHolder_image_marginTop),0, 0);
        RelativeLayout.LayoutParams layoutParamsi = new RelativeLayout.LayoutParams(paramsi);
        sharedUsersHolder.setLayoutParams(layoutParamsi);
        sharedUsersHolder.setOrientation(LinearLayout.HORIZONTAL);
        sharedUsersHolder.setGravity(Gravity.CENTER_VERTICAL);


        try {
            final int[] j = {0};
            for (Iterator<String> it = tripJson.getJSONObject("sharedWith").keys(); it.hasNext(); ) {
                final String userKey = it.next();
                Log.i("createTripCardLayout: ",tripJson.getJSONObject("sharedWith").getString(userKey));

                final CircularImageView circularImageView = new CircularImageView(getContext());
                LinearLayout.LayoutParams paramst = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.card_sharedUser_image_width), (int) getResources().getDimension(R.dimen.card_sharedUser_image_height));
                circularImageView.setLayoutParams(paramst);
                circularImageView.setBorderColor(Color.parseColor("#eeeeee"));
                circularImageView.setImageResource(R.drawable.profile1);

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference ref = database.getReference("users/"+tripJson.getJSONObject("sharedWith").getString(userKey));
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> userHash = (HashMap<String,Object>) dataSnapshot.getValue();

                        if(String.valueOf(userHash.get("photoUrl")) != "null" && getContext() != null) {
                            ViewGroup.MarginLayoutParams paramsl = new ViewGroup.MarginLayoutParams(circularImageView.getLayoutParams());

                            try {
                                if(tripJson.getJSONObject("sharedWith").getString(userKey).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    currentUserPhotoUrl = String.valueOf(userHash.get("photoUrl"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(j[0] != 0) {
                                paramsl.setMargins((int) getResources().getDimension(R.dimen.card_sharedUser_image_marginLeft), 0, 0, 0);
                            }else{
                                paramsl.setMargins(0, 0, 0, 0);
                            }
                            RelativeLayout.LayoutParams layoutParaml = new RelativeLayout.LayoutParams(paramsl);
                            circularImageView.setLayoutParams(layoutParaml);

                            Glide.with(getContext())
                                    .load(String.valueOf(userHash.get("photoUrl")))
                                    .thumbnail(Glide.with(getContext()).load(R.drawable.profile1))
                                    .into(circularImageView);

                            sharedUsersHolder.addView(circularImageView);

                            j[0]++;
                            ref.removeEventListener(this);

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


        TextView itemsCount = new TextView(getContext());
        LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemsCount.setLayoutParams(params7);
        ViewGroup.MarginLayoutParams params8 = new ViewGroup.MarginLayoutParams(itemsCount.getLayoutParams());
        params8.setMargins((int) getResources().getDimension(R.dimen.card_items_marginLeft),(int) getResources().getDimension( R.dimen.card_items_marginTop),0, 0);
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
        cardView.addView(sharedUsersHolder);
        cardView.addView(itemsCount);

        tripCardHolder.addView(cardView);




    }
}
