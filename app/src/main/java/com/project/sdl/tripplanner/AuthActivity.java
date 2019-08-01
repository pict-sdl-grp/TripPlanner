package com.project.sdl.tripplanner;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.VideoView;

public class AuthActivity extends AppCompatActivity {

    SurfaceView videoView;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //hide action bar
        getSupportActionBar().hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

//        authBackground = (VideoView)findViewById(R.id.auth_background);
//        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video);
//        authBackground.setVideoURI(uri);
//        authBackground.requestFocus();
//        authBackground.start();

//        authBackground.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mediaPlayer.setLooping(true);
//            }
//        });

        videoView = findViewById(R.id.surfaceViewFrame);

        startVideoPlayBack();





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startVideoPlayBack() {

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.video);


            SurfaceHolder holder = videoView.getHolder();
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mediaPlayer.setDisplay(holder);
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
