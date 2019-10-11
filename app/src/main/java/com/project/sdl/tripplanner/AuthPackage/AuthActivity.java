package com.project.sdl.tripplanner.AuthPackage;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.sdl.tripplanner.IntroPackage.WelcomeActivity;
import com.project.sdl.tripplanner.R;
import com.project.sdl.tripplanner.UserPackage.UserActivity;


public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    TextView switchText1;
    TextView switchText2;
    TextView appbar_title1;
    TextView appbar_title2;
    EditText username;
    EditText email;
    EditText password;
    EditText confirmPassword;
    EditText loginEmail;
    EditText loginPassword;
    RelativeLayout login_relative_layout;
    RelativeLayout signup_relative_layout;
    ImageView auth_appbar;
    Button auth_button;
    ProgressBar auth_progress;

    Boolean authMode = true; //if auth mode is true --- login mode
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    //Some change
    public void changeToLoginLayout(){
        login_relative_layout.setVisibility(View.VISIBLE);
        signup_relative_layout.setVisibility(View.INVISIBLE);

        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.colorPrimary)));
        auth_appbar.setImageResource(R.drawable.auth_login_appbar);
        appbar_title1.setText("Hello again, login to");
        appbar_title2.setText("explore your place!");
        switchText1.setText("Didn't have account yet?");
        switchText2.setText("Signup");
        switchText2.setTextColor(getResources().getColor(R.color.colorPrimary));
        switchText2.setPaintFlags(switchText2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        auth_button.setText("login");
        auth_button.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorPrimary));


    }
    public void changeToSignupLayout(){
        login_relative_layout.setVisibility(View.INVISIBLE);
        signup_relative_layout.setVisibility(View.VISIBLE);

        getSupportActionBar().setTitle("Signup");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.colorSecondary)));
        auth_appbar.setImageResource(R.drawable.auth_signup_appbar);
        appbar_title1.setText("Enjoy everything with our");
        appbar_title2.setText("wonderful trip advice!");
        switchText1.setText("Do you already have an account?");
        switchText2.setText("Login");
        switchText2.setTextColor(getResources().getColor(R.color.colorSecondary));
        switchText2.setPaintFlags(switchText2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        auth_button.setText("signup");
        auth_button.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorSecondary));
    }

    public void setProgressBar(Boolean progress){
        if(progress){
            auth_progress.setVisibility(View.VISIBLE);
            auth_button.setVisibility(View.INVISIBLE);
        }else{
            auth_progress.setVisibility(View.INVISIBLE);
            auth_button.setVisibility(View.VISIBLE);
        }
    }

    //on click auth button:
    public void onClickAuthenticate(View view){

        Log.i("onClickAuthenticate: ",authMode.toString());
        setProgressBar(true);
        //for login
        if(authMode){
            if(loginEmail.getText().length() > 0 && loginPassword.getText().length() > 0) {
                mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user != null) {
                                        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                                        startActivity(intent);
                                        mDatabase=FirebaseDatabase.getInstance().getReference();
                                        mDatabase.child("users").child(user.getUid()).child("isEmailVerified").setValue(user.isEmailVerified());
                                        Log.i("run: ",String.valueOf(user.isEmailVerified()));
                                        finish();//destroy this activity
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                setProgressBar(false);
                                            }
                                        },1000);
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("signInWithEmail:failure", task.getException().getLocalizedMessage());
                                    Toast.makeText(AuthActivity.this, task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    setProgressBar(false);
                                }
                                // ...
                            }
                        });
            }else {
                Toast.makeText(this, "All fields are required!!!", Toast.LENGTH_SHORT).show();
                setProgressBar(false);

            }
        }
        //for signup
        else{
            if(username.getText().length() > 0 && email.getText().length() > 0 && password.getText().length() > 0) {
                if(password.getText().toString().equals(confirmPassword.getText().toString())) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        final FirebaseUser user = mAuth.getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username.getText().toString())
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            mDatabase.child("users").child(user.getUid()).child("username").setValue(username.getText().toString());
                                                            mDatabase.child("users").child(user.getUid()).child("email").setValue(email.getText().toString());

                                                            if (user != null) {

                                                                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);

                                                                startActivity(intent);
                                                                finish();
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        setProgressBar(false);
                                                                        changeToLoginLayout();
                                                                    }
                                                                },1000);
                                                            }
                                                        }
                                                    }
                                                });


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("failure", task.getException().getLocalizedMessage());
                                        Toast.makeText(AuthActivity.this, task.getException().getLocalizedMessage(),
                                                Toast.LENGTH_SHORT).show();
                                        setProgressBar(false);
                                    }

                                    // ...
                                }
                            });
                }else{
                    Toast.makeText(this, "Passwords don't match!!!", Toast.LENGTH_SHORT).show();
                    setProgressBar(false);
                }

            }else{
                Toast.makeText(this, "All fields are required!!!", Toast.LENGTH_SHORT).show();
                setProgressBar(false);
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //Initialize layout components
        login_relative_layout = findViewById(R.id.login_relative_layout);
        signup_relative_layout = findViewById(R.id.signup_relative_layout);
        auth_appbar = findViewById(R.id.auth_appbar);
        switchText1 = findViewById(R.id.switch_text1);
        switchText2 = findViewById(R.id.switch_text2);
        appbar_title1 = findViewById(R.id.appbar_title1);
        appbar_title2 = findViewById(R.id.appbar_title2);
        auth_button = findViewById(R.id.auth_button);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        auth_progress = findViewById(R.id.auth_progress);

        //Initialize firebase components
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //set action bar
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.colorPrimary)));
        auth_appbar.setImageResource(R.drawable.auth_login_appbar);

        switchText2.setPaintFlags(switchText2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        switchText2.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.switch_text2){
            if(authMode) {
                changeToSignupLayout();
            }else{
                changeToLoginLayout();
            }
            authMode = !authMode;
            Log.i("onClick: ",authMode.toString());

        }
    }
}
