package com.project.sdl.tripplanner.ProfilePackage;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.sdl.tripplanner.AuthPackage.User;
import com.project.sdl.tripplanner.R;

public class EditProfile_Activity extends AppCompatActivity {


    ImageView back_Image;
    CircularImageView profileImage;
    Button save;

    EditText name;
    EditText aboutYou;
    EditText phoneNo;

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference();
        Log.e("mRefPath",""+mRef);

        back_Image = findViewById(R.id.backButtonEdit);
        save = findViewById(R.id.save);

        profileImage = findViewById(R.id.profileEdit);
        name = findViewById(R.id.nameEdit);
        aboutYou = findViewById(R.id.aboutEdit);
        phoneNo = findViewById(R.id.phoneEdit);

        //Initially Displays the user data into fields
        displayData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDatabase();
                backToProfile();
            }
        });

        back_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToProfile();
            }
        });

    }

    protected void displayData(){
        final DatabaseReference mRef1 =  FirebaseDatabase.getInstance().getReference("/users/" + mUser.getUid());

        mRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userDetails = dataSnapshot.getValue(User.class);

                name.setText(userDetails.username);
                phoneNo.setText(userDetails.phoneNo);
                aboutYou.setText(userDetails.aboutYou);

                Log.e("Data displayed","Current user " + userDetails.username +" URL: "+ mRef1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("onCancelled","Datasnapshot not caught");
            }
        });
    }

    protected void saveToDatabase(){


        User userDb = new User(name.getText().toString(),mUser.getEmail(),phoneNo.getText().toString(),aboutYou.getText().toString(),"India, Maharashtra",mUser.isEmailVerified());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Change username", "User profile updated.");
                        }
                    }
                });

        mRef.child("users").child(mUser.getUid()).setValue(userDb);

        Log.e("Data updated","Current user " + userDb.username +" ID: "+ mUser.getUid() + "\n URL");

        backToProfile();
    }

    protected void backToProfile(){
        finish();
    }
}
