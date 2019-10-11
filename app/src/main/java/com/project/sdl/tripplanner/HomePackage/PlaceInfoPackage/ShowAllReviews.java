package com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

            if(getSupportActionBar() != null) {
                CardView cardView = new CardView(getApplicationContext());
                LinearLayout.LayoutParams paramsc = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cardView.setLayoutParams(paramsc);
                ViewGroup.MarginLayoutParams paramscd = new ViewGroup.MarginLayoutParams(cardView.getLayoutParams());
                paramscd.setMargins((int) getResources().getDimension(R.dimen.card_review_marginLeft), (int) getResources().getDimension(R.dimen.card_review_marginTop), (int) getResources().getDimension(R.dimen.card_review_marginRight), 0);
                RelativeLayout.LayoutParams layoutParamscd = new RelativeLayout.LayoutParams(paramscd);
                cardView.setLayoutParams(layoutParamscd);


                //Make review block
                LinearLayout reviewBlock = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                reviewBlock.setLayoutParams(params);
                ViewGroup.MarginLayoutParams paramsh = new ViewGroup.MarginLayoutParams(reviewBlock.getLayoutParams());
                paramsh.setMargins(0, (int) getResources().getDimension(R.dimen.card_review__block_marginTop), 0, 0);
                RelativeLayout.LayoutParams layoutParamsh = new RelativeLayout.LayoutParams(paramsh);
                reviewBlock.setLayoutParams(layoutParamsh);
                reviewBlock.setOrientation(LinearLayout.VERTICAL);

                //Make User block
                LinearLayout userBlock = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                userBlock.setLayoutParams(params1);
                ViewGroup.MarginLayoutParams paramsu = new ViewGroup.MarginLayoutParams(userBlock.getLayoutParams());
                paramsu.setMargins((int) getResources().getDimension(R.dimen.card_review_user_marginLeft), (int) getResources().getDimension(R.dimen.card_review_user_marginTop), 0, 0);
                RelativeLayout.LayoutParams layoutParamsu = new RelativeLayout.LayoutParams(paramsu);
                reviewBlock.setLayoutParams(layoutParamsu);
                userBlock.setOrientation(LinearLayout.HORIZONTAL);
                userBlock.setGravity(Gravity.CENTER_VERTICAL);

                //Make User Circular Image View
                final CircularImageView circularImageView = new CircularImageView(getApplicationContext());
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

                            Glide.with(getApplicationContext())
                                    .load(String.valueOf(userHash.get("photoUrl")))
                                    .thumbnail(Glide.with(getApplicationContext()).load(R.raw.video))
                                    .into(circularImageView);


                            ref.removeEventListener(this);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                //Make container holder for rating and date
                LinearLayout ratingAndDateHolder = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ratingAndDateHolder.setLayoutParams(params3);
                ratingAndDateHolder.setOrientation(LinearLayout.VERTICAL);


                //Make Star Rating bar
                RatingBar ratingBar = new RatingBar(getApplicationContext());
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
                TextView date = new TextView(getApplicationContext());
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
                TextView reviewTitle = new TextView(getApplicationContext());
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
                TextView reviewMain = new TextView(getApplicationContext());
                LinearLayout.LayoutParams params9 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                reviewMain.setLayoutParams(params9);
                ViewGroup.MarginLayoutParams params10 = new ViewGroup.MarginLayoutParams(reviewTitle.getLayoutParams());
                params10.setMargins((int) getResources().getDimension(R.dimen.card_review_main_marginLeft), (int) getResources().getDimension(R.dimen.card_review_main_marginTop), 0, (int) getResources().getDimension(R.dimen.card_review_main_marginBottom));
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params10);
                reviewMain.setLayoutParams(layoutParams2);
                reviewMain.setText(reviewJson.getString("review"));
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
