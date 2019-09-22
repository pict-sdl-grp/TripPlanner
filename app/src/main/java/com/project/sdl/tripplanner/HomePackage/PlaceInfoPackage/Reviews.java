package com.project.sdl.tripplanner.HomePackage.PlaceInfoPackage;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Manish Chougule on 02-08-2019.
 */

@IgnoreExtraProperties
public class Reviews {

    public Double rating;
    public String date;
    public String visitType;
    public String title;
    public String review;

    public Reviews() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Reviews(Double rating,String date,String visitType,String title,String review) {
        this.rating = rating;
        this.date = date;
        this.visitType = visitType;
        this.title = title;
        this.review = review;
    }


}

