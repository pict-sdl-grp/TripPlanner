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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.sdl.tripplanner.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Manish Chougule on 08-08-2019.
 */

public class ProfileFragment extends Fragment {

    Button editProfile;
    FirebaseUser user ;
    FirebaseAuth mAuth;
    TextView nameDisplay;
    TextView emailDisplay;
    CircularImageView profileImage;

    ImageView setting;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_profile, null);
        editProfile = root.findViewById(R.id.editProf);

        setting =   root.findViewById(R.id.settingProf);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfile_intent = new Intent(getContext(),EditProfile_Activity.class);
                startActivity(editProfile_intent);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setting_intent=new Intent(getContext(),Setting_Activity.class);
                startActivity(setting_intent);
            }
        });


        Log.e("BeforeInstance: ","This is log before initialising");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        nameDisplay = root.findViewById(R.id.nameProf);
        emailDisplay = root.findViewById(R.id.emailProf);
        profileImage = root.findViewById(R.id.profileImage);
        nameDisplay.setText(user.getDisplayName());
        emailDisplay.setText(user.getEmail());

        Log.e("AfterInstance: ","This is log after initialising");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new ImageLoadTask(user.getPhotoUrl().toString(), profileImage).execute();
            }
        },1000);

        return root;
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}
