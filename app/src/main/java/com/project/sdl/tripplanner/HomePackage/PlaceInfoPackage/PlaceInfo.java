package com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;
import com.here.android.mpa.common.GeoCoordinate;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.sdl.tripplanner.ObjectSerializer;
import com.project.sdl.tripplanner.R;
import com.project.sdl.tripplanner.TripsPackage.SelectTripActivity;
import com.project.sdl.tripplanner.UserPackage.UserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PlaceInfo extends AppCompatActivity {

    ImageView placeInfoBg;
    TextView overviewTitle;
    RatingBar overviewRating;
    RatingBar reviewRateBar;
    TextView reviewText;
    TextView reviewRateText;
    TextView vicinityText;
    TextView suggestedDuration;
    TextView suggestedDistance;
    ScrollView scrollView;
    LinearLayout photosHolder;
    ArrayList<String> imageRefs;
    FirebaseStorage storage;
    DatabaseReference mDatabase;
    String currentPlaceId;
    LinearLayout writeReviewContainer;
    ImageView backButton;
    LinearLayout userReviewContainer;
    ImageButton wishlistNo;
    ImageButton wishlistYes;
    SharedPreferences sharedPreferences;
    HashMap<String, String> wishlistHash;
    ShimmerLayout placeInfoShimmer;
    ScrollView placeInfoShimmerScroll;
    ImageView addPlaceToTripIcon;
    JSONObject tripJson;
    Map<String,Object> tripHash;


    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;

                    result+=current;

                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("onPostExecute: ","llll");

            try {
                JSONObject jsonObject = new JSONObject(s);
                Log.i("route",jsonObject.getJSONObject("response").getJSONArray("route").getJSONObject(0).getJSONObject("summary").getString("text"));
                String duration = convertSectoDay(Integer.valueOf(jsonObject.getJSONObject("response").getJSONArray("route").getJSONObject(0).getJSONObject("summary").getString("travelTime")));
                String distance = String.valueOf((Double.valueOf(Integer.valueOf(jsonObject.getJSONObject("response").getJSONArray("route").getJSONObject(0).getJSONObject("summary").getString("distance"))/1000)));
                suggestedDuration.setText("Suggested duration : More than "+duration);
                suggestedDistance.setText("distance : "+distance+" km");

                placeInfoShimmer.stopShimmerAnimation();
                placeInfoShimmerScroll.setVisibility(View.INVISIBLE);
                placeInfoShimmerScroll.setSmoothScrollingEnabled(false);
                scrollView.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public String convertSectoDay(int n)
    {
        int day = n / (24 * 3600);

        n = n % (24 * 3600);
        int hour = n / 3600;

        n %= 3600;
        int minutes = n / 60 ;

        n %= 60;
        int seconds = n;

        return day+" days "+hour+" hrs "+minutes+" min "+seconds+" sec ";

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        getSupportActionBar().hide();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        placeInfoBg = findViewById(R.id.placeInfoBg);
        overviewTitle = findViewById(R.id.overviewTitle);
        overviewRating = findViewById(R.id.overviewRating);
        reviewText = findViewById(R.id.reviewText);
        reviewRateText = findViewById(R.id.reviewRateText);
        vicinityText = findViewById(R.id.vicinityText);
        suggestedDistance = findViewById(R.id.aboutDistance);
        suggestedDuration = findViewById(R.id.aboutSuggestedDuration);
        reviewRateBar = findViewById(R.id.reviewRateBar);
        writeReviewContainer = findViewById(R.id.writeReviewContainer);
        backButton = findViewById(R.id.wishlistIcon3);
        userReviewContainer = findViewById(R.id.userReviewsContainer);
        wishlistYes = findViewById(R.id.wishlistIconYes);
        wishlistNo = findViewById(R.id.wishlistIconNo);
        addPlaceToTripIcon = findViewById(R.id.addPlaceToTripIcon);



        sharedPreferences = getApplicationContext().getSharedPreferences(FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        scrollView = findViewById(R.id.placeInfoScrollBar);
        placeInfoShimmer = findViewById(R.id.placeInfoShimmer);
        placeInfoShimmerScroll = findViewById(R.id.placeInfoShimmerScroll);

        placeInfoShimmer.startShimmerAnimation();


        wishlistHash = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("selectedPlace"));
            wishlistHash = (HashMap<String,String>) ObjectSerializer.deserialize(sharedPreferences.getString("wishlistHash", ObjectSerializer.serialize(new HashMap<String,String>())));
            if(wishlistHash.keySet().contains(jsonObject.getString("id"))){
                wishlistYes.setVisibility(View.VISIBLE);
                wishlistNo.setVisibility(View.INVISIBLE);
            }else{
                wishlistNo.setVisibility(View.VISIBLE);
                wishlistYes.setVisibility(View.INVISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference ref = database.getReference("trips/"+user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tripHash = (HashMap<String,Object>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



//        ==========================================
//        Handle Add/Removed Place To Trip
//        ==========================================

        addPlaceToTripIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        if(tripHash != null) {
                            tripJson = new JSONObject(tripHash);
                            ArrayList<String> tripNames = new ArrayList<>();
                            for(String key : tripHash.keySet()){
                                try{
                                    tripNames.add(tripJson.getJSONObject(key).getString("tripName"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(getIntent().getStringExtra("selectedPlace"));

                                Intent intent = new Intent(getApplicationContext(), SelectTripActivity.class);
                                intent.putStringArrayListExtra("tripNames",tripNames);
                                intent.putExtra("currentPlace",getIntent().getStringExtra("selectedPlace"));
                                intent.putExtra("currentPlaceName",jsonObject.getString("name"));
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }else{
                            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                            intent.putExtra("flag","trip");
                            startActivity(intent);

                        }
                    }


        });


//        ==========================================
//        Handle Wish-list
//        ==========================================

        wishlistNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    if(wishlistHash.keySet().contains(currentPlaceId)){
                        wishlistHash.remove(currentPlaceId);
                        wishlistYes.setVisibility(View.INVISIBLE);
                        wishlistNo.setVisibility(View.VISIBLE);

                    }else{
                        JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("selectedPlace"));
                        jsonObject.put("parentId",getIntent().getStringExtra("currentParentId"));

                        wishlistHash.put(currentPlaceId,jsonObject.toString());
                        wishlistYes.setVisibility(View.VISIBLE);
                        wishlistNo.setVisibility(View.INVISIBLE);
                    }

                    Log.i("onClick: ",wishlistHash.toString());
                    sharedPreferences.edit().putString("wishlistHash", ObjectSerializer.serialize(wishlistHash)).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        wishlistYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    if(wishlistHash.keySet().contains(currentPlaceId)){
                        wishlistHash.remove(currentPlaceId);
                        wishlistYes.setVisibility(View.INVISIBLE);
                        wishlistNo.setVisibility(View.VISIBLE);

                    }else{
                        JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("selectedPlace"));
                        jsonObject.put("parentId",getIntent().getStringExtra("currentParentId"));

                        wishlistHash.put(currentPlaceId,jsonObject.toString());
                        wishlistYes.setVisibility(View.VISIBLE);
                        wishlistNo.setVisibility(View.INVISIBLE);
                    }

                    Log.i("onClick: ",wishlistHash.toString());
                    sharedPreferences.edit().putString("wishlistHash", ObjectSerializer.serialize(wishlistHash)).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        ==========================================
//        Go Back
//        ==========================================

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


//        ==========================================
//        Handle Tab Actions
//        ==========================================

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.smoothScrollTo(0,0);
                            }
                        });
                        break;
                    case 1:
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.smoothScrollTo(0,300);
                            }
                        });
                        break;
                    case 2:
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.smoothScrollTo(0,1060);
                            }
                        });
                        break;
                    case 3:
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.smoothScrollTo(0,1600);
                            }
                        });
                        break;
                    default:
                        return;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case 0:
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.smoothScrollTo(0,0);
                            }
                        });
                        break;
                    case 1:
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.smoothScrollTo(0,300);
                            }
                        });
                        break;
                    case 2:
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.smoothScrollTo(0,1060);
                            }
                        });
                        break;
                    case 3:
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.smoothScrollTo(0,1600);
                            }
                        });
                        break;
                    default:
                        return;
                }

            }
        });


//        ==================================================
//        Handle Add Photos
//        ==================================================

        TextView addPhoto = findViewById(R.id.addPhoto);
        photosHolder = findViewById(R.id.photosHolder);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

//        ==========================================
//        Show Reviews
//        ==========================================

        fetchReviews();

//        ==========================================
//        Show All Reviews
//        ==========================================

        userReviewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ShowAllReviews.class);
                intent.putExtra("selectedPlace", getIntent().getStringExtra("selectedPlace"));
                startActivity(intent);
            }
        });


//        ==========================================
//        Open Write Review Activity
//        ==========================================

        writeReviewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WritePlaceReview.class);
                intent.putExtra("currentPlaceId",currentPlaceId);
                startActivity(intent);
            }
        });





        Intent intent = getIntent();


        try {
            final JSONObject jsonObject = new JSONObject(intent.getStringExtra("selectedPlace"));

            final String cord1 = intent.getStringExtra("currentLatitude")+"%2C"+intent.getStringExtra("currentLongitude");
            final String cord2 = jsonObject.getJSONObject("position").getString("latitude")+"%2C"+jsonObject.getJSONObject("position").getString("longitude");

            currentPlaceId = jsonObject.getString("id");


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadUIElements(jsonObject);
                }
            },1000);

            final DownloadTask task = new DownloadTask();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    task.execute("https://route.api.here.com/routing/7.2/calculateroute.json?waypoint0="+cord1+"&waypoint1="+cord2+"&mode=fastest%3Bcar%3Btraffic%3Aenabled&app_id=4SxTSk1WVmA9G62J6qqD&app_code=dHz3QjYEWM_cecRN_aAHqQ&departure=now");

                }
            },2000);




        } catch (JSONException e) {
            e.printStackTrace();
        }


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
                        reviewText.setText(reviewHash.keySet().size() + "  Reviews");
                        reviewRateText.setText(reviewHash.keySet().size() + "  Reviews");

                        int i = 0;
                        double rating = 0.0;
                        for(String key:reviewHash.keySet()){

                            if(i < 3) {
                                try {
                                    Log.i("reviews", jsonReview.getString(key));
                                    createReviewLayout(jsonReview.getJSONObject(key));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{

                            }


                            try {
                                rating = rating + Double.parseDouble(jsonReview.getJSONObject(key).getString("rating"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            i++;


                        }

                        reviewRateBar.setRating(Float.parseFloat(String.valueOf((float) (rating/(i)))));
                        overviewRating.setRating(Float.parseFloat(String.valueOf((float) (rating/(i)))));

                    }else{
                        reviewRateBar.setRating(0);
                        overviewRating.setRating(0);
                        reviewText.setText(0+ "  Reviews");
                        reviewRateText.setText(0+ "  Reviews");
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final File myFile = new File(String.valueOf(imageUri));
                        Log.i("onActivityResult",imageUri.toString());
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        ImageView imageItem = new ImageView(this);
                        imageItem.setImageBitmap(selectedImage);
                        imageItem.setScaleType(ImageView.ScaleType.FIT_XY);
                        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(220,220);
                        imageItem.setLayoutParams(parms);

                        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(imageItem.getLayoutParams());
                        marginParams.setMargins(8, 0,0, 0);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                        imageItem.setLayoutParams(layoutParams);

                        photosHolder.addView(imageItem);


                        Bitmap bitmap = selectedImage;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

                        byte[] byteArray = stream.toByteArray();

                        StorageReference storageRef = storage.getReference();
                        if(currentPlaceId.length() > 0) {
                            UploadTask uploadTask = storageRef.child("places/"+currentPlaceId+"/"+myFile.getName()).putBytes(byteArray);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getApplicationContext(), "error!!!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(), "photo saved!!", Toast.LENGTH_SHORT).show();
                                    imageRefs.add(myFile.getName());
                                    mDatabase.child("sub-places").child(getIntent().getStringExtra("currentParentId")).child(currentPlaceId).child("places").child("imageRefs").setValue(imageRefs);

                                }
                            });
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    public void createReviewLayout(JSONObject reviewJson){

        try{

            if(getApplicationContext() != null) {
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

    public void loadUIElements(JSONObject jsonObject){
        try {
            overviewTitle.setText(jsonObject.getString("name"));
            String[] vicinity = jsonObject.getString("vicinity").split("<br/>");
            String updatedVicinity = "";
            for(int i=0;i<vicinity.length;i++){

                updatedVicinity+=jsonObject.getString("vicinity").split("<br/>")[i];
                if(vicinity.length > 1 && i != vicinity.length-1) {
                    updatedVicinity += ",";
                }
            }

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("sub-places/"+ getIntent().getStringExtra("currentParentId") +"/"+currentPlaceId+"/places/imageRefs");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    imageRefs = (ArrayList<String>) dataSnapshot.getValue();

                    StorageReference storageRef = storage.getReference();
                    for(int i=0;i<imageRefs.size();i++) {

                        storageRef.child("places/" + currentPlaceId + "/" + imageRefs.get(i)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                ImageView imageItem = new ImageView(getApplicationContext());
                                imageItem.setScaleType(ImageView.ScaleType.FIT_XY);
                                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(220,220);
                                imageItem.setLayoutParams(parms);

                                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(imageItem.getLayoutParams());
                                marginParams.setMargins(8, 0,0, 0);
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                                imageItem.setLayoutParams(layoutParams);


                                String imageURL = uri.toString();

                                Glide.with(getApplicationContext())
                                        .load(imageURL)
                                        .into(imageItem);

                                photosHolder.addView(imageItem);
                            }
                        });




                    }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });



            vicinityText.setText(updatedVicinity);
            Log.i("rate",jsonObject.getJSONObject("userRatings").getString("average"));

            GeoCoordinate currentPlaceCoords = new GeoCoordinate(
                    jsonObject.getJSONObject("position").getDouble("latitude"),jsonObject.getJSONObject("position").getDouble("longitude"));

            PlaceMapInfo placeMapInfo = new PlaceMapInfo(this,currentPlaceCoords);


            String imageURL = getIntent().getStringExtra("selectedPlaceImage");

            Glide.with(getApplicationContext())
                    .load(imageURL)
                    .into(placeInfoBg);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
