package com.project.sdl.tripplanner.TripsPackage;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.project.sdl.tripplanner.R;

import java.io.IOException;
import java.util.ArrayList;

public class PlacesToTripMap extends FragmentActivity {

    private ArrayList<GeoCoordinate> positionArray;
    private SupportMapFragment m_mapFragment;
    private AppCompatActivity m_activity;
    private View m_mapFragmentContainer;
    private Map map = null;

    public PlacesToTripMap(TripInfoActivity activity, ArrayList positionArrayparam)  {
        m_activity = activity;
        positionArray = new ArrayList<>(positionArrayparam);

        initMapFragment();
    }

    private SupportMapFragment getMapFragment() {
        return (SupportMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.multiplePlaceMapLocation);
    }

    private void initMapFragment() {

        /* Locate the mapFragment UI element */
        m_mapFragment = getMapFragment();
        m_mapFragmentContainer = m_activity.findViewById(R.id.placeMapLocation);

        if (m_mapFragment != null) {
            /* Initialize the SupportMapFragment, results will be given via the called back. */
            m_mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {
                    if (error == Error.NONE) {
                        map = m_mapFragment.getMap();
                        map.setMapScheme(map.getMapSchemes().get(4));
                        // Set the map center to the current place region (no animation)
                        map.setCenter(positionArray.get(0),
                                Map.Animation.NONE);
                        // Set the zoom level to the average between min and max
                        map.setZoomLevel(8.2);

                        for(GeoCoordinate position:positionArray) {
                            Image image = new Image();
                            try {
                                image.setImageResource(R.drawable.location_pin);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                        map.getPositionIndicator().setMarker(image).setVisible(true);

                            MapMarker mapMarker = new MapMarker();
                            mapMarker.setIcon(image);
                            mapMarker.setCoordinate(new GeoCoordinate(position));
                            map.addMapObject(mapMarker);
                        }


                    } else {
                        Toast.makeText(m_activity,
                                "ERROR: Cannot initialize Map with error " + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
//        }
    }



}
