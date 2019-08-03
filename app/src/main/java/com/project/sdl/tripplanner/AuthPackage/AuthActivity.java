package com.project.sdl.tripplanner.AuthPackage;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;

import com.project.sdl.tripplanner.R;


public class AuthActivity extends AppCompatActivity {

    TextView switchText1;
    TextView switchText2;
    TextView appbar_title1;
    TextView appbar_title2;
    RelativeLayout login_relative_layout;
    RelativeLayout signup_relative_layout;
    ImageView auth_appbar;

    Boolean authMode = true;

    public void onChangeAuthmode(View view){

        authMode = !authMode;

        if(authMode) {
            login_relative_layout.setVisibility(View.VISIBLE);
            signup_relative_layout.setVisibility(View.INVISIBLE);

            getSupportActionBar().setTitle("Login");
            appbar_title1.setText("Hello again, login to");
            appbar_title2.setText("explore your place!");
            switchText1.setText("Didn't have account yet?");
            switchText2.setText("SignUp");
            switchText2.setPaintFlags(switchText2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }else{
            login_relative_layout.setVisibility(View.INVISIBLE);
            signup_relative_layout.setVisibility(View.VISIBLE);

            getSupportActionBar().setTitle("Signup");
            appbar_title1.setText("Enjoy everything with our");
            appbar_title2.setText("wonderful trip advice!");
            switchText1.setText("Didn't have account yet?");
            switchText2.setText("SignUp");
            switchText2.setPaintFlags(switchText2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        login_relative_layout = (RelativeLayout)findViewById(R.id.login_relative_layout);
        signup_relative_layout = (RelativeLayout)findViewById(R.id.signup_relative_layout);
        auth_appbar = (ImageView)findViewById(R.id.auth_appbar);
        switchText1 = (TextView)findViewById(R.id.switch_text1);
        switchText2 = (TextView)findViewById(R.id.switch_text2);
        appbar_title1 = (TextView)findViewById(R.id.appbar_title1);
        appbar_title2 = (TextView)findViewById(R.id.appbar_title2);

        //set action bar title
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setElevation(0);

        auth_appbar.setImageResource(R.drawable.auth_appbar);


        switchText2.setPaintFlags(switchText2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }



}
