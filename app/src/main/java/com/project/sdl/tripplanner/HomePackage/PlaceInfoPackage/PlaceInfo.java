package com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage;

import android.content.Intent;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.project.sdl.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PlaceInfo extends AppCompatActivity {

    ImageView placeInfoBg;
    TextView overviewTitle;
    RatingBar overviewRating;
    RatingBar reviewRateBar;
    TextView reviewText;
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
                String duration = String.valueOf(Integer.valueOf(jsonObject.getJSONObject("response").getJSONArray("route").getJSONObject(0).getJSONObject("summary").getString("travelTime"))/3600);
                String distance = String.valueOf(Integer.valueOf(jsonObject.getJSONObject("response").getJSONArray("route").getJSONObject(0).getJSONObject("summary").getString("distance"))/1000);
                suggestedDuration.setText("Suggested duration : More than "+duration+" hours");
                suggestedDistance.setText("distance : "+distance+" km");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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
        vicinityText = findViewById(R.id.vicinityText);
        suggestedDistance = findViewById(R.id.aboutDistance);
        suggestedDuration = findViewById(R.id.aboutSuggestedDuration);
        reviewRateBar = findViewById(R.id.reviewRateBar);
        writeReviewContainer = findViewById(R.id.writeReviewContainer);
        backButton = findViewById(R.id.wishlistIcon3);
        userReviewContainer = findViewById(R.id.userReviewsContainer);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        scrollView = findViewById(R.id.placeInfoScrollBar);


        storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        createReviewLayout();
        createReviewLayout();
        createReviewLayout();



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
                    try {
                        String s = task.execute("https://route.api.here.com/routing/7.2/calculateroute.json?waypoint0="+cord1+"&waypoint1="+cord2+"&mode=fastest%3Bcar%3Btraffic%3Aenabled&app_id=4SxTSk1WVmA9G62J6qqD&app_code=dHz3QjYEWM_cecRN_aAHqQ&departure=now").get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },2000);




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
                                    mDatabase.child("placesNearYou").child(getIntent().getStringExtra("currentParentId")).child(currentPlaceId).child("places").child("imageRefs").setValue(imageRefs);

                                }
                            });
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    public void createReviewLayout(){

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
                ratingBar.setRating(0);
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
                date.setText("Manish Chougule on  "+" -- / -- / --");
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
        reviewTitle.setText("Excellent monsoon destination for a day trip from pune");
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
        reviewMain.setText("The below review is for travel with family mainly and may not be applicable for trekking and adventures trips.It's good place to visit in monsoon and better to plan early morning...");
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
            DatabaseReference ref = database.getReference("placesNearYou/"+ getIntent().getStringExtra("currentParentId") +"/"+currentPlaceId+"/places/imageRefs");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    imageRefs = (ArrayList<String>) dataSnapshot.getValue();

                    StorageReference storageRef = storage.getReference();
                    for(int i=0;i<imageRefs.size();i++) {
                        storageRef.child("places/" + currentPlaceId + "/" + imageRefs.get(i)).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

                            @Override
                            public void onSuccess(byte[] bytes) {
                                // Use the bytes to display the image
                                System.out.println(bytes);
                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                ImageView imageItem = new ImageView(getApplicationContext());
                                imageItem.setImageBitmap(bm);
                                imageItem.setScaleType(ImageView.ScaleType.FIT_XY);
                                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(220,220);
                                imageItem.setLayoutParams(parms);

                                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(imageItem.getLayoutParams());
                                marginParams.setMargins(8, 0,0, 0);
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                                imageItem.setLayoutParams(layoutParams);

                                photosHolder.addView(imageItem);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
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
            overviewRating.setRating(Float.parseFloat(jsonObject.getJSONObject("userRatings").getString("average")));
            reviewRateBar.setRating(Float.parseFloat(jsonObject.getJSONObject("userRatings").getString("average")));
            reviewText.setText("0  Reviews");

            GeoCoordinate currentPlaceCoords = new GeoCoordinate(
                    jsonObject.getJSONObject("position").getDouble("latitude"),jsonObject.getJSONObject("position").getDouble("longitude"));

            PlaceMapInfo placeMapInfo = new PlaceMapInfo(this,currentPlaceCoords);



            byte[] bytes = getIntent().getByteArrayExtra("selectedPlaceImage");
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            placeInfoBg.setImageBitmap(bm);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
