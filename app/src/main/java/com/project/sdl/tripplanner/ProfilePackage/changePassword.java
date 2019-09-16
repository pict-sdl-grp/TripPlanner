package com.project.sdl.tripplanner.ProfilePackage;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.sdl.tripplanner.AuthPackage.AuthActivity;
import com.project.sdl.tripplanner.R;

public class changePassword extends AppCompatActivity {

    Button change;
    EditText oldPassword ;
    EditText newPassword;
    EditText confirmPassword;
    boolean flag;
    FirebaseUser mUser1;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        mUser1 = mAuth.getCurrentUser();

        change = findViewById(R.id.change);
        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = checkCurrentPassword();
                if(check){
                    if(newPassword.getText().toString().equals(confirmPassword.getText().toString())){
                        mUser1.updatePassword(newPassword.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Password", "User password updated.");
                                            Toast.makeText(getApplicationContext(),"Password changed",Toast.LENGTH_LONG);

                                            FirebaseAuth.getInstance().signOut();
                                            Intent AuthActivity_intent=new Intent(changePassword.this, AuthActivity.class);
                                            finishAffinity();
                                            startActivity(AuthActivity_intent);

                                        }else{
                                            Log.d("Password", "User password not set.");
                                            Toast.makeText(getApplicationContext(),"Unable to change Password ",Toast.LENGTH_SHORT);
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(getApplicationContext(),"Passwords dont match",Toast.LENGTH_SHORT);
                    }
                }
            }
        });
    }

    protected boolean checkCurrentPassword(){

        mAuth.signInWithEmailAndPassword(mUser1.getEmail(), oldPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Password Confirmation: ", "success");
                            flag = true;

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Password Confirmation: ", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Old Password is incorrect",
                                    Toast.LENGTH_SHORT).show();
                            flag = false;
                        }
                    }
                });
        return flag;
    }
}
