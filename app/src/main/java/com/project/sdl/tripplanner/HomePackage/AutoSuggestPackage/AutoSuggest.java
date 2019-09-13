package com.project.sdl.tripplanner.HomePackage.AutoSuggestPackage;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Manish Chougule on 02-08-2019.
 */

@IgnoreExtraProperties
public class AutoSuggest {

    public String id;
    public String city;
    public String state;
    public String country;

    public AutoSuggest() {
        // Default constructor required for calls to DataSnapshot.getValue(AutoSuggest.class)
    }

    public AutoSuggest(String id, String city, String state, String country) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.country = country;
    }
}
