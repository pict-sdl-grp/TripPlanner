package com.project.sdl.tripplanner.ProfilePackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.sdl.tripplanner.AuthPackage.AuthActivity;
import com.project.sdl.tripplanner.R;

import java.util.ArrayList;

public class Setting_Activity extends AppCompatActivity {

    ListView settingops;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        settingops=findViewById(R.id.settingOptions);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.project.sdl.tripplanner", Context.MODE_PRIVATE);


        final ArrayList<String> options=new ArrayList<>();
        options.add("Logout");
        options.add("Reset Password");
        options.add("Verify Email");

        ArrayAdapter<String> opsAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,options);
        settingops.setAdapter(opsAdapter);

        settingops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedOption=options.get(i);
                if(selectedOption.equals("Logout")) {

                    FirebaseAuth.getInstance().signOut();
                    Intent AuthActivity_intent=new Intent(Setting_Activity.this, AuthActivity.class);
                    finishAffinity();
                    startActivity(AuthActivity_intent);
                    finish();
                }
                else if(selectedOption.equals("Reset Password")) {
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
                else if(selectedOption.equals("Verify Email")) {
                    final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
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
                }else{
                    Log.e("Check Intent","No intent selected");
                }

            }
        });

    }
}
