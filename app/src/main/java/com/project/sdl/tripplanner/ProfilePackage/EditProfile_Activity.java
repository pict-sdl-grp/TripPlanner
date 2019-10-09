package com.project.sdl.tripplanner.ProfilePackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;

public class EditProfile_Activity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;

    ImageView back_Image;
    CircularImageView profileImage;
    Button save;

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
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                profileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                Bitmap realImage = BitmapFactory.decodeFile(picturePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();

                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                Log.d("Profile Image","Base64 ImageCode = "+encodedImage);

                editor.putString(PROFILE,encodedImage);
                editor.commit();
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

        String imageCode = pref.getString(PROFILE,"");

        if(imageCode!=""){
            byte[] b = Base64.decode(imageCode, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            profileImage.setImageBitmap(bitmap);
            Log.d("Profile Image","Selected Profile Image");

        }else{
            profileImage.setImageResource(R.drawable.profile1);
            Log.d("Profile Image","Default Profile Image");
        }
    }

    protected void saveToDatabase(){

        String userName = name.getText().toString();
        String userPhone = phoneNo.getText().toString();
        String userAbout = aboutYou.getText().toString();

        editor.putString(NAME,userName);
        editor.putString(PHONE,userPhone);
        editor.putString(ABOUTYOU,userAbout);
        editor.putString(PLACE,"Pune, Maharashtra");
        editor.commit();
        Log.e("Data updated"," ID: "+ mUser.getUid());

        backToProfile();
    }

    protected void backToProfile(){
        finish();
    }
}
