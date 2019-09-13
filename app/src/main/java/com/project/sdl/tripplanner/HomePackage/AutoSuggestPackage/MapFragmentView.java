/*
 * Copyright (c) 2011-2019 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.project.sdl.tripplanner.HomePackage.AutoSuggestPackage;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.search.AutoSuggest;
import com.here.android.mpa.search.AutoSuggestPlace;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.Place;
import com.here.android.mpa.search.PlaceRequest;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.TextAutoSuggestionRequest;
import com.project.sdl.tripplanner.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * This class encapsulates the properties and functionality of the Map view. It also implements 3
 * types of AutoSuggest requests that HERE Android SDK provides as example.
 */
public class MapFragmentView extends AppCompatActivity {
    public static List<DiscoveryResult> s_discoverResultList;
    private SupportMapFragment m_mapFragment;
    private AppCompatActivity m_activity;
    private View m_mapFragmentContainer;
    private Map m_map;
    private SearchView m_searchView;
    private SearchListener m_searchListener;
    private AutoSuggestAdapter m_autoSuggestAdapter;
    private List<AutoSuggest> m_autoSuggests;
    private ListView m_resultsListView;



    public MapFragmentView(MainSearchActivity activity) {
        m_activity = activity;
        m_searchListener = new SearchListener();
        m_autoSuggests = new ArrayList<>();
        /*
         * The map fragment is not required for executing AutoSuggest requests. However in this example,
         * we will use it to simplify of the SDK initialization.
         */
        initMapFragment();
        initControls();
    }

    private SupportMapFragment getMapFragment() {
        return (SupportMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = getMapFragment();
        m_mapFragmentContainer = m_activity.findViewById(R.id.mapfragment);

        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity
                    .getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: "
                    + e.getMessage());
        }


            if (m_mapFragment != null) {
            /* Initialize the SupportMapFragment, results will be given via the called back. */
                m_mapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                        if (error == Error.NONE) {
                            m_map = m_mapFragment.getMap();
                            m_map.setCenter(new GeoCoordinate(20.0038855,64.3692876),
                                    Map.Animation.NONE);
                            m_map.setZoomLevel(13.2);
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

    private void initControls() {
        m_searchView = m_activity.findViewById(R.id.search);
        m_searchView.setOnQueryTextListener(m_searchListener);

        m_resultsListView = m_activity.findViewById(R.id.resultsListViev);
        m_autoSuggestAdapter = new AutoSuggestAdapter(m_activity,
                android.R.layout.simple_list_item_1, m_autoSuggests);
        m_resultsListView.setAdapter(m_autoSuggestAdapter);
        m_resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AutoSuggest item = (AutoSuggest) parent.getItemAtPosition(position);
                handleSelectedAutoSuggest(item);
            }
        });

    }

    private class SearchListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (!newText.isEmpty()) {
                doSearch(newText);
            } else {
                setSearchMode(false);
            }
            return false;
        }
    }

    private void doSearch(String query) {
        setSearchMode(true);
/*
 Creates new TextAutoSuggestionRequest with current map position as search center
 and selected collection size with applied filters and chosen locale.
 For more details how to use TextAutoSuggestionRequest
 please see documentation for HERE SDK for Android.
 */
        TextAutoSuggestionRequest textAutoSuggestionRequest = new TextAutoSuggestionRequest(query);
        textAutoSuggestionRequest.setSearchCenter(m_map.getCenter());
        textAutoSuggestionRequest.setCollectionSize(100);

/*
   The textAutoSuggestionRequest returns its results to non-UI thread.
   So, we have to pass the UI update with returned results to UI thread.
 */
        textAutoSuggestionRequest.execute(new ResultListener<List<AutoSuggest>>() {
            @Override
            public void onCompleted(final List<AutoSuggest> autoSuggests, ErrorCode errorCode) {
                if (errorCode == errorCode.NONE) {
                    processSearchResults(autoSuggests);
                } else {
                    handleError(errorCode);
                }
            }
        });
    }

    private void processSearchResults(final List<AutoSuggest> autoSuggests) {

        final ArrayList<AutoSuggest> filteredAutoSuggests = new ArrayList<>();

        for(AutoSuggest a:autoSuggests){
            if(a.getType().toString() == "PLACE") {
                filteredAutoSuggests.add(a);
            }
        }

        m_activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_autoSuggests.clear();
                m_autoSuggests.addAll(filteredAutoSuggests);
                m_autoSuggestAdapter.notifyDataSetChanged();
            }
        });
    }

    private void handleSelectedAutoSuggest(final AutoSuggest autoSuggest) {
        switch (autoSuggest.getType()) {
            case PLACE:
                /*
                 Gets initialized PlaceRequest with location context that allows retrieving details
                 about the place on the selected location.
                 */
                AutoSuggestPlace autoSuggestPlace = (AutoSuggestPlace) autoSuggest;
                PlaceRequest detailsRequest = autoSuggestPlace.getPlaceDetailsRequest();
                detailsRequest.execute(new ResultListener<Place>() {
                    @Override
                    public void onCompleted(Place place, ErrorCode errorCode) {
                        if (errorCode == ErrorCode.NONE) {
                            handlePlace(place,autoSuggest);
                        } else {
                            handleError(errorCode);
                        }
                    }
                });
                break;
            //Do nothing.
            case UNKNOWN:
                default:
        }
    }

    public void setSearchMode(boolean isSearch) {
        if (isSearch) {
            m_mapFragmentContainer.setVisibility(View.INVISIBLE);
            m_resultsListView.setVisibility(View.VISIBLE);
        } else {
            m_mapFragmentContainer.setVisibility(View.INVISIBLE);
            m_resultsListView.setVisibility(View.INVISIBLE);
        }
    }

    private void handlePlace(Place place, AutoSuggest autoSuggest) {

                //============================================
                //# To Add Places to Database (Admin Level) #
                //============================================
//
//        AutoSuggestPlace autoSuggestPlace = (AutoSuggestPlace) autoSuggest;
//        Log.i("autosuggest",autoSuggest.getType().toString());
//        Log.i("autoSuggestPlace",autoSuggestPlace.getVicinity());
//        Log.i("handlePlace: ", place.getCategories().get(0).getId());
//        StringBuilder sb = new StringBuilder();
//        sb.append("Name: ").append(place.getName() + "\n");
//        sb.append("Alternative name:").append(place.getAlternativeNames());
//        showMessage("Schema created", sb.toString(), false);
//
//        ArrayList<String> imageRefsParams = new ArrayList();
//        imageRefsParams.add("none");
//
//        Places placeData = new Places(place.getId(),autoSuggest.getType().toString(),place.getName()
//                ,autoSuggestPlace.getVicinity(),autoSuggestPlace.getCategory(),
//                autoSuggestPlace.getPosition(),autoSuggestPlace.getBoundingBox(),
//                autoSuggestPlace.getUrl(),imageRefsParams,place.getViewUri(),place.getRatings(),
//                place.getUserRatings(),place.getContacts(),place.getEditorials(),place.getRelated(),
//                place.getAlternativeNames(),place.getAttributionText());
//
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        //To add places
////        mDatabase.child("places").child(place.getId()).setValue(placeData);
//
////        To add placesNearYou
////        PlacesNearYou placesNearYou = new PlacesNearYou(place.getId(),placeData);
////        mDatabase.child("placesNearYou")
////                .child("356tevwm-f6e196eee83b4aadb172f916c30355cf").child(place.getId()).setValue(placesNearYou);


                    //============================================
                    //# To Show Places Information (User Level)  #
                    //============================================


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("users").child(user.getUid()).child("currentPlaceId").setValue(place.getId());
        m_activity.finish();

    }



    private void handleError(ErrorCode errorCode) {
        showMessage("Error", "Error description: " + errorCode.name(), true);
    }

    private void showMessage(String title, String message, boolean isError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(m_activity);
        builder.setTitle(title).setMessage(message);
        if (isError) {
            builder.setIcon(android.R.drawable.ic_dialog_alert);
        } else {
            builder.setIcon(android.R.drawable.ic_dialog_info);
        }
        builder.setNeutralButton("OK", null);
        builder.create().show();
    }
}
