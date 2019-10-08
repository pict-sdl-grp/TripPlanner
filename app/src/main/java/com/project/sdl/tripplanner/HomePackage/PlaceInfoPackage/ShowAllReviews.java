package com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.Map;

public class ShowAllReviews extends AppCompatActivity {

    LinearLayout userReviewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_reviews);

        userReviewContainer = findViewById(R.id.userReviewsContainer);


//        ==========================================
//        Show Reviews
//        ==========================================

        fetchReviews();



    }

    public void fetchReviews() {

        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("selectedPlace"));

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

                        for(String key:reviewHash.keySet()){

                                try {
                                    Log.i("reviews", jsonReview.getString(key));
                                    createReviewLayout(jsonReview.getJSONObject(key));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


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

            //Make review block
            LinearLayout reviewBlock = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            reviewBlock.setLayoutParams(params);
            ViewGroup.MarginLayoutParams paramsh = new ViewGroup.MarginLayoutParams(reviewBlock.getLayoutParams());
            paramsh.setMargins(0, 16,0, 0);
            RelativeLayout.LayoutParams layoutParamsh = new RelativeLayout.LayoutParams(paramsh);
            reviewBlock.setLayoutParams(layoutParamsh);
            reviewBlock.setOrientation(LinearLayout.VERTICAL);

            //Make User block
            LinearLayout userBlock = new LinearLayout(this);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            userBlock.setLayoutParams(params1);
            userBlock.setOrientation(LinearLayout.HORIZONTAL);

            //Make User Circular Image View
            CircularImageView circularImageView = new CircularImageView(this);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(140, 140);
            circularImageView.setLayoutParams(params2);
            circularImageView.setImageResource(R.drawable.profile1);
            circularImageView.setBorderColor(Color.parseColor("#eeeeee"));



            //Make container holder for rating and date
            LinearLayout ratingAndDateHolder = new LinearLayout(this);
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ratingAndDateHolder.setLayoutParams(params3);
            ratingAndDateHolder.setOrientation(LinearLayout.VERTICAL);


            //Make Star Rating bar
            RatingBar ratingBar = new RatingBar(this);
            LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ratingBar.setLayoutParams(params4);
            ratingBar.setIsIndicator(true);
            ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(ratingBar.getLayoutParams());
            params5.setMargins(-120, 0,0, 0);
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
            TextView date = new TextView(this);
            LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT);
            date.setLayoutParams(params6);
            ViewGroup.MarginLayoutParams paramsd = new ViewGroup.MarginLayoutParams(date.getLayoutParams());
            paramsd.setMargins(0, -32,0, 0);
            RelativeLayout.LayoutParams layoutParamsd = new RelativeLayout.LayoutParams(paramsd);
            date.setLayoutParams(layoutParamsd);
            date.setText(reviewJson.getString("userName")+" on "+reviewJson.getString("date"));
            date.setTextSize(15);

            ratingAndDateHolder.addView(ratingBar);
            ratingAndDateHolder.addView(date);

            userBlock.addView(circularImageView);
            userBlock.addView(ratingAndDateHolder);

            //Make text view for review title part
            TextView reviewTitle = new TextView(this);
            LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            reviewTitle.setLayoutParams(params7);
            ViewGroup.MarginLayoutParams params8 = new ViewGroup.MarginLayoutParams(reviewTitle.getLayoutParams());
            params8.setMargins(140, 5,0, 0);
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(params8);
            reviewTitle.setLayoutParams(layoutParams1);
            reviewTitle.setText(reviewJson.getString("title"));
            reviewTitle.setTextSize(15);
            reviewTitle.setTextColor(Color.BLACK);
            reviewTitle.setTypeface(reviewTitle.getTypeface(), Typeface.BOLD);


            //Make text view for review main content
            TextView reviewMain = new TextView(this);
            LinearLayout.LayoutParams params9 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            reviewMain.setLayoutParams(params9);
            ViewGroup.MarginLayoutParams params10 = new ViewGroup.MarginLayoutParams(reviewTitle.getLayoutParams());
            params10.setMargins(140, 10,0, 16);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params10);
            reviewMain.setLayoutParams(layoutParams2);
            reviewMain.setText(reviewJson.getString("review"));
            reviewMain.setTextSize(15);

            // Make the separator
            TextView separator = new TextView(this);
            LinearLayout.LayoutParams params11 = new LinearLayout.LayoutParams(390, 2, 1);
            separator.setLayoutParams(params11);
            ViewGroup.MarginLayoutParams params12 = new ViewGroup.MarginLayoutParams(separator.getLayoutParams());
            params12.setMargins(0, 16,0, 0);
            RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(params12);
            separator.setLayoutParams(layoutParams3);
            separator.setBackgroundColor(Color.GRAY);
            LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(636, 2);
            paramss.gravity = Gravity.CENTER;
            separator.setLayoutParams(paramss);
            reviewBlock.addView(userBlock);
            reviewBlock.addView(reviewTitle);
            reviewBlock.addView(reviewMain);
            reviewBlock.addView(separator);

            userReviewContainer.addView(reviewBlock);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
