package com.project.sdl.tripplanner.AuthPackage;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Manish Chougule on 02-08-2019.
 */

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String photoUrl;
    public String currentPlaceId;
    public String phoneNo;
    public String aboutYou;
    public String currentPlace;
    public boolean isEmailVerified;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String username,String email,String phoneNo,String aboutYou,String currentPlace,boolean isEmailVerified,String currentPlaceId) {
        this.username = username;
        this.phoneNo = phoneNo;
        this.email = email;
        this.aboutYou = aboutYou;
        this.currentPlace = currentPlace;
        this.isEmailVerified = isEmailVerified;
        this.currentPlaceId = currentPlaceId;
    }
}
