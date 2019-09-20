package com.project.sdl.tripplanner.ProfilePackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.sdl.tripplanner.AuthPackage.User;
import com.project.sdl.tripplanner.HomePackage.HomeFragment;
import com.project.sdl.tripplanner.R;
import com.project.sdl.tripplanner.UserPackage.UserActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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


    FirebaseUser mUser ;
    FirebaseAuth mAuth;
    DatabaseReference mRef;

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


        setting =   root.findViewById(R.id.settingInProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),EditProfile_Activity.class));
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



        displayUserData();

        Log.e("AfterInstance: ","This is log after initialising");

        return root;
    }

    public void displayUserData(){
        mRef = FirebaseDatabase.getInstance().getReference("/users/" + mUser.getUid());

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userData = dataSnapshot.getValue(User.class);

                nameDisplay.setText(userData.username);
                emailDisplay.setText(mUser.getEmail());
                currentPlace.setText(userData.currentPlace);
                phoneNo.setText(userData.phoneNo);

                if(userData.aboutYou !=null){
                    aboutYou2.setVisibility(View.VISIBLE);
                    aboutYou2.setText(userData.aboutYou);
                    aboutYou1.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled","Datasnapshot not caught");
            }
        });
    }
//    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
//
//        private String url;
//        private ImageView imageView;
//
//        public ImageLoadTask(String url, ImageView imageView) {
//            this.url = url;
//            this.imageView = imageView;
//        }
//
//        @Override
//        protected Bitmap doInBackground(Void... params) {
//            try {
//                URL urlConnection = new URL(url);
//                HttpURLConnection connection = (HttpURLConnection) urlConnection
//                        .openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                Bitmap myBitmap = BitmapFactory.decodeStream(input);
//                return myBitmap;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            super.onPostExecute(result);
//            imageView.setImageBitmap(result);
//        }
//    }
}
