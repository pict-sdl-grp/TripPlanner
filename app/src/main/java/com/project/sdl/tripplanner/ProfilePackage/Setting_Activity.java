package com.project.sdl.tripplanner.ProfilePackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.sdl.tripplanner.AuthPackage.AuthActivity;
import com.project.sdl.tripplanner.R;

public class Setting_Activity extends AppCompatActivity {
    Button logout;
    Button resetpwd;
    Button verify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        logout=findViewById(R.id.buttonLogout);
        resetpwd=findViewById(R.id.buttonResetPwd);
        verify=findViewById(R.id.buttonverify);

        // logging out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent AuthActivity_intent=new Intent(Setting_Activity.this, AuthActivity.class);
                finishAffinity();
//                AuthActivity_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                AuthActivity_intent.putExtra("EXIT", true);
                startActivity(AuthActivity_intent);
                finish();

            }
        });

        // resetting password
        resetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                String email=user.getEmail();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(Setting_Activity.this, "Check Email to reset your password", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Setting_Activity.this, "Failed to send email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                FirebaseAuth.getInstance().signOut();
                Intent AuthActivity_intent=new Intent(Setting_Activity.this, AuthActivity.class);
                finishAffinity();
                startActivity(AuthActivity_intent);
                finish();
            }
        });

        // verifying email
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                findViewById(R.id.buttonverify).setEnabled(true);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Setting_Activity.this,
                                            "Verification email sent to " + user.getEmail(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Setting_Activity.this,
                                            "Failed to send verification email.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }
}
