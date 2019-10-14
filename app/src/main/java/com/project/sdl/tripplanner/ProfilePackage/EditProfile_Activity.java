package com.project.sdl.tripplanner.ProfilePackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.sdl.tripplanner.HomePackage.AutoSuggestPackage.MainSearchActivity;
import com.project.sdl.tripplanner.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfile_Activity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;

    ImageView back_Image;
    CircularImageView profileImage;
    Button save;
    ProgressBar imageLoader;
    Boolean isUploaded = false;
    public static EditText countryEdit;

    String photoUrl;

    EditText name;
    EditText aboutYou;
    EditText phoneNo;

    final static String PLACE = "place";
    final static String NAME = "name";
    final static String PHONE= "phoneNo";
    final static String EMAIL = "email";
    final static String ABOUTYOU = "aboutYou";
    final static String PROFILE = "image";

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    DatabaseReference mRef;

//    final static String PREF_DATA = "USER_DATA";
    SharedPreferences.Editor editor ;
    SharedPreferences pref;

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
        countryEdit = findViewById(R.id.countryEdit);
        imageLoader = findViewById(R.id.imageLoader);

        editor = getSharedPreferences(mUser.getUid(), Context.MODE_PRIVATE).edit();
        pref = getSharedPreferences(mUser.getUid(),Context.MODE_PRIVATE);

        //Initially Displays the user data into fields
        displayData();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }

        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDatabase();
                backToProfile();
            }
        });

        countryEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainSearchActivity.class);
                intent.putExtra("editProfileSearch","true");
                startActivity(intent);
            }
        });


        countryEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(b){
                    Intent intent = new Intent(getApplicationContext(), MainSearchActivity.class);
                    intent.putExtra("editProfileSearch","true");
                    startActivity(intent);
                }
            }
        });


        back_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToProfile();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK ) {
            if (null != data){

                save.setVisibility(View.INVISIBLE);
                imageLoader.setVisibility(View.VISIBLE);



                final Uri imageUri = data.getData();
                photoUrl = imageUri.toString();
                final File myFile = new File(String.valueOf(imageUri));
                Log.i("onActivityResult",imageUri.toString());
                final InputStream imageStream;
                try {
                    imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    Bitmap bitmap = selectedImage;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

                    byte[] byteArray = stream.toByteArray();

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();


                    final UploadTask uploadTask = storageRef.child("users/"+mUser.getUid()+"/"+myFile.getName()).putBytes(byteArray);
                    Snackbar.make(findViewById(android.R.id.content), "Uploading.....", 5000)
                            .setAction("cancel", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    isUploaded = false;
                                    uploadTask.cancel();
                                    save.setVisibility(View.VISIBLE);
                                    imageLoader.setVisibility(View.INVISIBLE);
                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            isUploaded = true;
                            Toast.makeText(getApplicationContext(), "photo saved!!", Toast.LENGTH_SHORT).show();
                            save.setVisibility(View.VISIBLE);
                            imageLoader.setVisibility(View.INVISIBLE);
                            profileImage.setImageBitmap(selectedImage);
                            FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid()).child("photoUrl").setValue(String.valueOf(taskSnapshot.getDownloadUrl()));

                        }
                    });


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }







//                editor.putString(PROFILE,encodedImage);
//                editor.commit();
//        User userDb = new User(name.getText().toString(),mUser.getEmail(),phoneNo.getText().toString(),aboutYou.getText().toString(),"India, Maharashtra",mUser.isEmailVerified());

                Log.d("OnResult OF Gallery","PRofile image selected from gallery");
            }else {
                Toast.makeText(getApplicationContext(),"Profile image not set please give permission from mobile settings",Toast.LENGTH_SHORT);
            }

        }
    }

    protected void displayData(){
        name.setText(pref.getString(NAME,mUser.getDisplayName()));
        phoneNo.setText(pref.getString(PHONE,null));
        aboutYou.setText(pref.getString(ABOUTYOU,null));
        countryEdit.setText(pref.getString(PLACE,null));

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("users/"+mUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> userHash = (HashMap<String,Object>) dataSnapshot.getValue();
                Log.i("photoUrleee", String.valueOf(userHash.get("photoUrl")));

                if(String.valueOf(userHash.get("photoUrl")) != "null") {
//                    RequestOptions requestOptions = new RequestOptions();
//                    requestOptions.placeholder(R.drawable.profile1);
//                    requestOptions.error(R.drawable.profile1);

                    Glide.with(getApplicationContext())
//                            .setDefaultRequestOptions(requestOptions)
                            .load(String.valueOf(userHash.get("photoUrl")))
                            .thumbnail(Glide.with(getApplicationContext()).load(R.drawable.profile1))
                            .into(profileImage);
                }

                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void saveToDatabase(){

        String userName = name.getText().toString();
        String userPhone = phoneNo.getText().toString();
        String userAbout = aboutYou.getText().toString();

        editor.putString(NAME,userName);
        editor.putString(PHONE,userPhone);
        editor.putString(ABOUTYOU,userAbout);
        editor.putString(PLACE, String.valueOf(countryEdit.getText()));
        editor.commit();
        Log.e("Data updated"," ID: "+ mUser.getUid());

        backToProfile();
    }

    protected void backToProfile(){

        Intent resultIntent = new Intent();
        if(isUploaded) {
            resultIntent.putExtra("photoUrl", photoUrl);
        }
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
