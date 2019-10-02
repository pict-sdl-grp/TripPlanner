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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
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


    public void createTripCardLayout(JSONObject tripJson, String key, int p, int i){

        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
        cardView.setLayoutParams(params);
        ViewGroup.MarginLayoutParams params1 = new ViewGroup.MarginLayoutParams(cardView.getLayoutParams());
        if(p == i) {
            params1.setMargins(40, 0, 40, 240);
        }else{
            params1.setMargins(40, 0, 40, 40);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(params1);
        cardView.setLayoutParams(layoutParams);
        cardView.setTag(key);

        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
        imageView.setLayoutParams(params2);
        imageView.setImageResource(R.drawable.nat4);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        TextView tripName = new TextView(getContext());
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

        TextView creatorName = new TextView(getContext());
        LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        creatorName.setLayoutParams(params5);
        ViewGroup.MarginLayoutParams params6 = new ViewGroup.MarginLayoutParams(creatorName.getLayoutParams());
        params6.setMargins(20, 400,0, 0);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params6);
        creatorName.setLayoutParams(layoutParams2);
        creatorName.setText("By "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        creatorName.setTextSize(14);

        TextView itemsCount = new TextView(getContext());
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
