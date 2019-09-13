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

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email,String photoUrl) {
        this.username = username;
        this.email = email;
        this.photoUrl = photoUrl;
    }
}
