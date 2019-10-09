package com.project.sdl.tripplanner.ProfilePackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.sdl.tripplanner.R;
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
        currentPlace.setText("Pune, Maharashtra");
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

        String imageCode = pref.getString(EditProfile_Activity.PROFILE,"");

        if(imageCode!=""){
            byte[] b = Base64.decode(imageCode, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            profileImage.setImageBitmap(bitmap);
            Log.d("Profile Image","Selected Profile Image");

        }else{
            profileImage.setImageResource(R.drawable.profile1);
            Log.d("Profile Image","Default Profile Image");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        displayUserData();
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
