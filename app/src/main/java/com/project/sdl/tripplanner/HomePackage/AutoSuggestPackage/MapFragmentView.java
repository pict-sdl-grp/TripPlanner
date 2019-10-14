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

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
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
import com.project.sdl.tripplanner.ProfilePackage.EditProfile_Activity;
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
    LinearLayout imageHolder;
    LinearLayout imageForm;
    EditText mode;
    EditText inputId;
    FirebaseStorage storage;
    ProgressBar progressBar;
    String editFlag;

    public MapFragmentView(MainSearchActivity activity, String editProfileSearch) {

        editFlag = String.valueOf(editProfileSearch);


        m_activity = activity;
        m_searchListener = new SearchListener();
        m_autoSuggests = new ArrayList<>();
        imageHolder = activity.findViewById(R.id.imageHolder);
        imageForm = activity.findViewById(R.id.imageForm);
        mode = activity.findViewById(R.id.mode);
        inputId = activity.findViewById(R.id.idInput);
        progressBar = activity.findViewById(R.id.listProgress);

        storage = FirebaseStorage.getInstance();

        LinearLayout searchContainer = m_activity.findViewById(R.id.searchContainer);
        ImageButton imageButton = m_activity.findViewById(R.id.imageButton);
        HorizontalScrollView horizontalScrollView = m_activity.findViewById(R.id.horizontalScrollView1);
        Button clearButton = m_activity.findViewById(R.id.clearButton);
        TextView imageTitle = m_activity.findViewById(R.id.imageTitle);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(String.valueOf(user.getEmail()).equalsIgnoreCase("admin@admin.com")) {

            Toast.makeText(m_activity, "Admin Level!!!!", Toast.LENGTH_SHORT).show();

        }else{
            searchContainer.removeView(mode);
            searchContainer.removeView(inputId);
            searchContainer.removeView(imageForm);
            searchContainer.removeView(imageButton);
            searchContainer.removeView(horizontalScrollView);
            searchContainer.removeView(clearButton);
            searchContainer.removeView(imageTitle);
        }


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

        progressBar.setVisibility(View.INVISIBLE);
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
            progressBar.setVisibility(View.VISIBLE);
            m_resultsListView.setVisibility(View.VISIBLE);
        } else {
            m_mapFragmentContainer.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            m_resultsListView.setVisibility(View.INVISIBLE);
        }
    }

    private void handlePlace(final Place place, AutoSuggest autoSuggest) {


        if(editFlag.equals("true")){

            AutoSuggestPlace autoSuggestPlace = (AutoSuggestPlace) autoSuggest;
            String[] vicinity = autoSuggestPlace.getVicinity().split("<br/>");
            String updatedVicinity = "";
            for(int i=0;i<vicinity.length;i++){

                updatedVicinity+=autoSuggestPlace.getVicinity().split("<br/>")[i];
                if(vicinity.length > 1 && i != vicinity.length-1) {
                    updatedVicinity += ",";
                }
            }

            EditProfile_Activity.countryEdit.setText(place.getName()+","+updatedVicinity);

            m_activity.finish();

        }else {

            //============================================
            //# To Add Places to Database (Admin Level) #
            //============================================
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (String.valueOf(user.getEmail()).equalsIgnoreCase("admin@admin.com")) {

                AutoSuggestPlace autoSuggestPlace = (AutoSuggestPlace) autoSuggest;
                Log.i("autosuggest", autoSuggest.getType().toString());
                Log.i("autoSuggestPlace", autoSuggestPlace.getVicinity());
                Log.i("handlePlace: ", place.getCategories().get(0).getId());
                StringBuilder sb = new StringBuilder();
                sb.append("Name: ").append(place.getName() + "\n");
                sb.append("Alternative name:").append(place.getAlternativeNames());
                showMessage("Schema created", sb.toString(), false);


                ArrayList<String> imageRefsParams = new ArrayList();
                imageRefsParams.add("none");

                Places placeData = new Places(place.getId(), autoSuggest.getType().toString(), place.getName()
                        , autoSuggestPlace.getVicinity(), autoSuggestPlace.getCategory(),
                        autoSuggestPlace.getPosition(), autoSuggestPlace.getBoundingBox(),
                        autoSuggestPlace.getUrl(), imageRefsParams, place.getViewUri(), place.getRatings(),
                        place.getUserRatings(), place.getContacts(), place.getEditorials(), place.getRelated(),
                        place.getAlternativeNames(), place.getAttributionText());

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                //To add places
                if (String.valueOf(mode.getText()).equalsIgnoreCase("0")) {
                    Log.i("0", ")))))))))))))");
                    mDatabase.child("places").child(place.getId()).setValue(placeData);
                    imageForm.setVisibility(View.VISIBLE);
                } else if (String.valueOf(mode.getText()).equalsIgnoreCase("1")) {
                    if (inputId.getText().length() > 0) {
                        //To add placesNearYou
                        imageForm.setVisibility(View.VISIBLE);
                        PlacesNearYou placesNearYou = new PlacesNearYou(place.getId(), placeData);
                        mDatabase.child("placesNearYou")
                                .child(String.valueOf(inputId.getText())).child(place.getId()).setValue(placesNearYou);
                    }
                } else if (String.valueOf(mode.getText()).equalsIgnoreCase("2")) {
                    if (inputId.getText().length() > 0) {
                        //To add awesomePlacesToVisit
                        imageForm.setVisibility(View.VISIBLE);
                        PlacesNearYou placesNearYou = new PlacesNearYou(place.getId(), placeData);
                        mDatabase.child("sub-places")
                                .child(String.valueOf(inputId.getText())).child(place.getId()).setValue(placesNearYou);
                    }
                } else if (String.valueOf(mode.getText()).contains("3")) {
                    MainSearchActivity.currentPlaceId = place.getId();
                    MainSearchActivity.inputIdParam = inputId.getText().toString();
                    MainSearchActivity.modeParam = String.valueOf(mode.getText());

                    ImageButton pickImage = m_activity.findViewById(R.id.imageButton);
                    pickImage.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            m_activity.startActivityForResult(photoPickerIntent, 1);
                        }
                    });
                } else {
                    Toast.makeText(m_activity, "search & select place 1st!!!", Toast.LENGTH_SHORT).show();
                }

            } else {
                //============================================
                //# To Show Places Information (User Level)  #
                //============================================

                Log.i("placeId", place.getId() + place.getName());
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(user.getUid()).child("currentPlaceId").setValue(place.getId());
                m_activity.finish();
            }


        }


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
