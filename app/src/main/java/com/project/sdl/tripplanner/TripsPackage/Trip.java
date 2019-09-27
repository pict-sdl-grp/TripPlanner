package com.project.sdl.tripplanner.TripsPackage;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Manish Chougule on 02-08-2019.
 */

@IgnoreExtraProperties
public class Trip {

    public String tripName;

    public Trip() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Trip(String tripName) {
        this.tripName = tripName;
    }


}
