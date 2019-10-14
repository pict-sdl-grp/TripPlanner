package com.project.sdl.tripplanner.HomePackage.AutoSuggestPackage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.sdl.tripplanner.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainSearchActivity extends AppCompatActivity {

    LinearLayout imageHolder;
    static String currentPlaceId;
    FirebaseStorage storage;
    static String modeParam;
    static String inputIdParam;

    DatabaseReference mDatabase;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    private MapFragmentView m_mapFragmentView;


    public void addData(View view){


        String key  = mDatabase.child("auto-suggest").push().getKey();

        AutoSuggest autoSuggest = new AutoSuggest(key,"test","Maharashtra","India");
        mDatabase.child("auto-suggest/"+key).setValue(autoSuggest);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autosuggest_main);
        Log.i("onCreate: ","start");

        imageHolder = findViewById(R.id.imageHolder);
        storage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
//***********************************************************
//        // Get a reference to our posts
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference("auto-suggest");
//
//// Attach a listener to read the data at our posts reference
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map<String, Object> autoSuggest = (HashMap<String,Object>) dataSnapshot.getValue();
//
//
//                System.out.println(autoSuggest.keySet());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });
//****************************************************************

        if (hasPermissions(this, RUNTIME_PERMISSIONS)) {
            setupMapFragmentView();
        } else {
            ActivityCompat
                    .requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
        }

    }

    /**
     * Only when the app's target SDK is 23 or higher, it requests each dangerous permissions it
     * needs when the app is running.
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        /*
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat
                                .shouldShowRequestPermissionRationale(this, permissions[index])) {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                            + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                    + " not granted", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                setupMapFragmentView();
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onClearImages(View view){
        imageHolder.removeAllViews();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final File myFile = new File(String.valueOf(imageUri));
                        Log.i("onActivityResult",imageUri.toString());
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        ImageView imageItem = new ImageView(this);
                        imageItem.setImageBitmap(selectedImage);

                        imageHolder.addView(imageItem);


                        Bitmap bitmap = selectedImage;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

                        byte[] byteArray = stream.toByteArray();

                        StorageReference storageRef = storage.getReference();
                        if(currentPlaceId.length() > 0) {
                            UploadTask uploadTask = storageRef.child("places/"+currentPlaceId+"/"+myFile.getName()).putBytes(byteArray);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(getApplicationContext(), "error!!!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(), "image added!!", Toast.LENGTH_SHORT).show();
                                    ArrayList<String> imageRefs = new ArrayList<>();
                                    imageRefs.add(myFile.getName());
                                    if(inputIdParam.length() > 0){
                                        if(modeParam.equals("3.1")) {
                                            mDatabase.child("placesNearYou").child(inputIdParam).child(currentPlaceId).child("places").child("imageRefs").setValue(imageRefs);
                                        }else if(modeParam.equals("3.2")){
                                            mDatabase.child("sub-places").child(inputIdParam).child(currentPlaceId).child("places").child("imageRefs").setValue(imageRefs);

                                        }
                                    }else{
                                        mDatabase.child("places").child(currentPlaceId).child("imageRefs").setValue(imageRefs);
                                    }
                                }
                            });
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    private void setupMapFragmentView() {
        // All permission requests are being handled. Create map fragment view. Please note
        // the HERE SDK requires all permissions defined above to operate properly.

        String editProfileSearch = getIntent().getStringExtra("editProfileSearch");

        m_mapFragmentView = new MapFragmentView(this,editProfileSearch);

    }


}
