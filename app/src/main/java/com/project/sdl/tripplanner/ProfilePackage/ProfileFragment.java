package com.project.sdl.tripplanner.ProfilePackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class ProfileFragment extends Fragment {

    Button editProfile;
    TextView nameDisplay;
    TextView emailDisplay;
    TextView phoneNo;
    TextView aboutYou1;
    TextView aboutYou2;
    TextView currentPlace;

    CircularImageView profileImage;
    ImageView setting;
    ImageView tick1,tick2;

    FirebaseUser mUser ;
    FirebaseAuth mAuth;
    DatabaseReference mRef;

    SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, null);
        editProfile = root.findViewById(R.id.editProf);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                new ImageLoadTask(mUser.getPhotoUrl().toString(), profileImage).execute();
//            }
//        },1000);


        profileImage = root.findViewById(R.id.profileImageInProfile);
        setting =   root.findViewById(R.id.settingInProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(),EditProfile_Activity.class),0);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),Setting_Activity.class));
            }
        });


        Log.e("BeforeInstance: ","This is log before initialising");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        nameDisplay = root.findViewById(R.id.nameInProfile);
        emailDisplay = root.findViewById(R.id.emailProf);
        currentPlace =root.findViewById(R.id.currenPlaceInProfile);
        phoneNo = root.findViewById(R.id.phoneInProfile);
        aboutYou1 = root.findViewById(R.id.aboutYouInProfile);
        aboutYou2 = root.findViewById(R.id.aboutYou2);
        tick1 = root.findViewById(R.id.tick1Done);
        tick2 = root.findViewById(R.id.tick2Done);


        pref = this.getActivity().getSharedPreferences(mUser.getUid(), Context.MODE_PRIVATE);

        displayUserData();

        Log.e("AfterInstance: ","This is log after initialising");

        return root;
    }

    public void displayUserData(){
        mRef = FirebaseDatabase.getInstance().getReference("/users/" + mUser.getUid());

        String name = pref.getString(EditProfile_Activity.NAME,mUser.getDisplayName()) ;
        nameDisplay.setText(name.length()<22?name:name.substring(0,20)+"...");
        emailDisplay.setText(mUser.getEmail().length()>22 ? mUser.getEmail().substring(0,20)+"...":mUser.getEmail());
        if(!String.valueOf(pref.getString(EditProfile_Activity.PLACE, null)).equals("null")) {
            currentPlace.setText(pref.getString(EditProfile_Activity.PLACE, null).length() > 19 ? pref.getString(EditProfile_Activity.PLACE, null).substring(0, 16) + "..." : pref.getString(EditProfile_Activity.PLACE, null));
        }
        phoneNo.setText(pref.getString(EditProfile_Activity.PHONE,null));

        if(pref.getString(EditProfile_Activity.PLACE,null)!=null){
            tick2.setVisibility(View.VISIBLE);
        }else{
            tick2.setVisibility(View.INVISIBLE);
        }
        if(pref.getString(EditProfile_Activity.PHONE,null)!=null){
            tick1.setVisibility(View.VISIBLE);
        }else{
            tick1.setVisibility(View.INVISIBLE);
        }

        if(pref.getString(EditProfile_Activity.ABOUTYOU,null) !=null){
            aboutYou2.setVisibility(View.VISIBLE);
            aboutYou2.setText(pref.getString(EditProfile_Activity.ABOUTYOU,null));
            aboutYou1.setVisibility(View.INVISIBLE);
        }


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("users/"+mUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> userHash = (HashMap<String,Object>) dataSnapshot.getValue();
                Log.i("photoUrlppp", String.valueOf(userHash.get("photoUrl")));

                if(String.valueOf(userHash.get("photoUrl")) != "null") {
//                    RequestOptions requestOptions = new RequestOptions();
//                    requestOptions.placeholder(R.drawable.profile1);
//                    requestOptions.error(R.drawable.profile1);

                    if(getContext() != null) {
                        Glide.with(getContext())
//                                .setDefaultRequestOptions(requestOptions)
                                .load(String.valueOf(userHash.get("photoUrl")))
                                .thumbnail(Glide.with(getContext()).load(R.drawable.profile1))
                                .into(profileImage);
                    }
                }

                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        displayUserData();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    if(data != null) {
                        Log.i("photop", "llllll");
                        String photoUrl = data.getStringExtra("photoUrl");
                            Glide.with(getContext())
                                    .load(photoUrl)
                                    .into(profileImage);
                    }
                }
                break;
            }
        }
    }
}
