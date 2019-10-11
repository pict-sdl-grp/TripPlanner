package com.project.sdl.tripplanner.HomePackage;

import android.Manifest;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.project.sdl.tripplanner.R;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SetCurrentLocation extends FragmentActivity {
    private static final String LOG_TAG = SetCurrentLocation.class.getSimpleName();

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private SupportMapFragment mapFragment = null;



    private PositioningManager positioningManager = null;
    private PositioningManager.OnPositionChangedListener positionListener;
    private GeoCoordinate currentPosition = new GeoCoordinate(37.7397, -121.4252);

    ProgressBar gpsLoader;
    TextView currentLocationText;
    TextView loadingText;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();
    }

    private SupportMapFragment getSupportMapFragment() {
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private void initialize() {
        setContentView(R.layout.activity_basic_maps);

        gpsLoader = findViewById(R.id.gpsLoader);
        currentLocationText = findViewById(R.id.currentLocationText);
        loadingText = findViewById(R.id.loadingText);

        gpsLoader.setVisibility(View.VISIBLE);

        // Search for the map fragment to finish setup by calling init().
        mapFragment = getSupportMapFragment();

        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(this
                    .getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: "
                    + e.getMessage());
        }


        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();
                    map.setMapScheme(map.getMapSchemes().get(4));
                    // Set the map center to the Pune region (no animation)
                    map.setCenter(new GeoCoordinate(18.542592, 73.8770944, 0.0),
                            Map.Animation.NONE);
                    // Set the zoom level to the average between min and max
                    map.setZoomLevel(14.2);

                    positioningManager = PositioningManager.getInstance();
                    positionListener = new PositioningManager.OnPositionChangedListener() {
                        @Override
                        public void onPositionUpdated(PositioningManager.LocationMethod method, final GeoPosition position, boolean isMapMatched) {
                            currentPosition = position.getCoordinate();
                            map.setCenter(position.getCoordinate(), Map.Animation.NONE);
                            Log.i("onPositionUpdated: ",position.getPositionSource());

                            String cityName = null;
                            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = gcd.getFromLocation(position.getCoordinate().getLatitude(),
                                        position.getCoordinate().getLongitude(), 1);
                                if (addresses.size() > 0) {

                                    System.out.println(addresses.get(0).getLocality());
                                    cityName = addresses.get(0).getLocality();
                                    Log.i("city name",cityName);
                                }
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }

                            gpsLoader.setVisibility(View.INVISIBLE);
                            currentLocationText.setText("Your current location is : "+cityName);
                            loadingText.setVisibility(View.INVISIBLE);


                            DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            mDatabase.child("users").child(user.getUid()).child("currentLocation").setValue(position.getCoordinate());
                            mDatabase.child("users").child(user.getUid()).child("currentLocationCityName").setValue(cityName);

                            positioningManager.removeListener(this);

                            Snackbar.make(findViewById(android.R.id.content), "Your current location is : "+cityName, 5000)
                                    .setAction("Close", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                    .show();



                        }
                        @Override
                        public void onPositionFixChanged(PositioningManager.LocationMethod method, PositioningManager.LocationStatus status) { }
                    };
                    try {
                        positioningManager.addListener(new WeakReference<>(positionListener));
                        if(!positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK)) {
                            Log.e("HERE", "PositioningManager.start: Failed to start...");
                        }
                    } catch (Exception e) {
                        Log.e("HERE", "Caught: " + e.getMessage());
                    }

                    Image image = new Image();
                    try {
                        image.setImageResource(R.drawable.marker);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    map.getPositionIndicator().setMarker(image).setVisible(true);


                } else {
                    Log.e(LOG_TAG, "Cannot initialize SupportMapFragment (" + error + ")");
                }
            }
        });
    }

    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

}
