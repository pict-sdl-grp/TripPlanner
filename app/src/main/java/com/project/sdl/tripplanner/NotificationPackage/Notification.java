package com.project.sdl.tripplanner.NotificationPackage;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

/**
 * Created by Manish Chougule on 02-08-2019.
 */

@IgnoreExtraProperties
public class Notification {

    public String id;
    public String type;
    public String message;
    public String timestamp;
    public String senderName;
    public String senderPhotoUrl;
    public Map<String, String> serverTime;
    public Boolean seen;

    public Notification() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public Notification(String id, String type, String message, String timestamp, String senderName,String senderPhotoUrl,Map<String, String> serverTime,Boolean seen) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.senderPhotoUrl = senderPhotoUrl;
        this.serverTime = serverTime;
        this.seen = seen;
    }
}
