package com.project.sdl.tripplanner.HomePackage.AutoSuggestPackage;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class PlacesNearYou {

    public String id;
    public Places places;



    public PlacesNearYou() {
        // Default constructor required for calls to DataSnapshot.getValue(AutoSuggest.class)
    }

    public PlacesNearYou(String id, Places places) {
        this.id = id;
        this.places = places;
    }
}
