package com.project.sdl.tripplanner;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.sdl.tripplanner.AuthPackage.AuthActivity;
import com.project.sdl.tripplanner.IntroPackage.PrefManager;
import com.project.sdl.tripplanner.IntroPackage.WelcomeActivity;
import com.project.sdl.tripplanner.UserPackage.UserActivity;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private VideoView videoView;
    FirebaseAuth isUserAuthenticated;
    private DatabaseReference mDatabase;


        @Override
    protected void onDestroy() {
        super.onDestroy();
            videoView.pause();
            videoView = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isUserAuthenticated = FirebaseAuth.getInstance();
//        isUserAuthenticated.signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );


        videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if(isUserAuthenticated.getCurrentUser() != null){
//                    Intent i = new Intent(SplashScreen.this, UserActivity.class);
//                    startActivity(i);
                    Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                    startActivity(intent);
                    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                    mDatabase=FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(user.getUid()).child("isEmailVerified").setValue(user.isEmailVerified());
                    Log.i("run: ",String.valueOf(user.isEmailVerified()));
                }else{
                    Intent i = new Intent(SplashScreen.this, AuthActivity.class);
                    startActivity(i);
                }

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
