package com.project.sdl.tripplanner.TripsPackage;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Manish Chougule on 02-08-2019.
 */

@IgnoreExtraProperties
public class Trip {

    public String tripName;
    public String createdBy;
    public int noOfItems;
    public String id;



    public Trip() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Trip(String tripName,String createdBy,int noOfItems,String id) {
        this.tripName = tripName;
        this.createdBy = createdBy;
        this.noOfItems = noOfItems;
        this.id = id;
    }


}
