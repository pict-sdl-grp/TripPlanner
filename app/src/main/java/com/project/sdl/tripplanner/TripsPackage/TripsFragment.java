package com.project.sdl.tripplanner.TripsPackage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.sdl.tripplanner.R;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class TripsFragment extends Fragment {

    LinearLayout tripCardHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_trips, null);
        tripCardHolder = root.findViewById(R.id.tripCardHolder);

        final Button getStarted = root.findViewById(R.id.getStartedButton);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),CreateTripFormActivity.class);
                startActivity(intent);
            }
        });


        for(int i=0;i<10;i++) {
            createTripCardLayout();
        }


        for (int i = 0;i<tripCardHolder.getChildCount();i++) {
            tripCardHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),TripInfoActivity.class);
                    startActivity(intent);
                }
            });
        }

        return root;
    }

    public void createTripCardLayout(){

        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
        cardView.setLayoutParams(params);
        ViewGroup.MarginLayoutParams params1 = new ViewGroup.MarginLayoutParams(cardView.getLayoutParams());
        params1.setMargins(40, 0,40, 40);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(params1);
        cardView.setLayoutParams(layoutParams);

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
        tripName.setText("Trip Name");
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
        creatorName.setText("By Manish Chougule");
        creatorName.setTextSize(14);

        TextView itemsCount = new TextView(getContext());
        LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemsCount.setLayoutParams(params7);
        ViewGroup.MarginLayoutParams params8 = new ViewGroup.MarginLayoutParams(itemsCount.getLayoutParams());
        params8.setMargins(20, 480,0, 0);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(params8);
        itemsCount.setLayoutParams(layoutParams3);
        itemsCount.setText("Featuring: 0 items");
        itemsCount.setTextSize(14);


        cardView.addView(imageView);
        cardView.addView(tripName);
        cardView.addView(creatorName);
        cardView.addView(itemsCount);

        tripCardHolder.addView(cardView);




    }
}
