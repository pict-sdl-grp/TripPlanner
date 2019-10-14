package com.project.sdl.tripplanner.WishListPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage.PlaceInfo;
import com.project.sdl.tripplanner.ObjectSerializer;
import com.project.sdl.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import static com.project.sdl.tripplanner.HomePackage.HomeFragment1.currentLocation;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class WishListFragment extends Fragment {


    SharedPreferences sharedPreferences;
    HashMap<String, String> wishlistHash;
    LinearLayout wishListContainer;
    TextView toolbar_title;

    @Override
    public void onResume() {
        super.onResume();

        initialize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_wish_list, null);

        wishListContainer = root.findViewById(R.id.wishListContainer);
        toolbar_title = root.findViewById(R.id.toolbar_title);

        sharedPreferences = getContext().getSharedPreferences(FirebaseAuth.getInstance().getCurrentUser().getUid(), Context.MODE_PRIVATE);

        initialize();


        return root;
    }

    public void initialize(){
        wishListContainer.removeAllViews();
        wishlistHash = new HashMap<>();
        try {
            wishlistHash = (HashMap<String,String>) ObjectSerializer.deserialize(sharedPreferences.getString("wishlistHash", ObjectSerializer.serialize(new HashMap<String,String>())));
            toolbar_title.setText("WIsh List ("+wishlistHash.size()+")");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject(wishlistHash);
        for (Iterator<String> it1 = jsonObject.keys(); it1.hasNext(); ) {
            String jsonKey = it1.next();

            try {
                JSONObject jsonObject1 = new JSONObject(String.valueOf(jsonObject.getString(jsonKey)));
                Log.i("wish-list2", String.valueOf(jsonObject1));

                createWishListItem(jsonObject1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    public View createWishListItem(final JSONObject jsonObject){


        final LinearLayout listItemBlock = new LinearLayout(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listItemBlock.setLayoutParams(params1);
        ViewGroup.MarginLayoutParams params2 = new ViewGroup.MarginLayoutParams(listItemBlock.getLayoutParams());
        params2.setMargins(0, 0,0, 10);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(params2);
        listItemBlock.setLayoutParams(layoutParams1);
        listItemBlock.setOrientation(LinearLayout.HORIZONTAL);
        listItemBlock.setPadding(40,40,40,40);
        listItemBlock.setBackgroundColor(Color.parseColor("#E0F2F1"));
        listItemBlock.setElevation(3);
        listItemBlock.setGravity(Gravity.CENTER_VERTICAL);
        try {
            listItemBlock.setTag(jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(120, 120);
        imageView.setLayoutParams(params3);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.nat4);


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        try {

            storageRef.child("places/" + jsonObject.getString("id") + "/" + jsonObject.getJSONArray("imageRefs").get(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    String imageURL = uri.toString();

                    if(getContext() != null) {
                        Glide.with(getContext())
                                .load(imageURL)
                                .thumbnail(Glide.with(getContext()).load(R.raw.video))
                                .into(imageView);
                        listItemBlock.setTag(imageURL);

                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


        TextView textView =new TextView(getContext());
        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params4);
        ViewGroup.MarginLayoutParams params5 = new ViewGroup.MarginLayoutParams(textView.getLayoutParams());
        params5.setMargins(40, 0,0, 0);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(params5);
        textView.setLayoutParams(layoutParams2);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        try {
            textView.setText(jsonObject.getString("name").toUpperCase());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        listItemBlock.addView(imageView);
        listItemBlock.addView(textView);




        listItemBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlaceInfo.class);
                try {
                    intent.putExtra("selectedPlace", jsonObject.toString());
                    intent.putExtra("currentParentId", jsonObject.getString("parentId"));
                    intent.putExtra("selectedPlaceImage", String.valueOf(listItemBlock.getTag()));
                    intent.putExtra("currentLatitude", currentLocation.getString("latitude"));
                    intent.putExtra("currentLongitude", currentLocation.getString("longitude"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });



        wishListContainer.addView(listItemBlock);

        return listItemBlock;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {



                }
                break;
            }
        }
    }

}
