package com.project.sdl.tripplanner.NotificationPackage;

import android.text.Spanned;

public class NotificationListItem {

    private String sender;
    private String date;
    private Spanned message;
    private String string;
    private String senderPhotoUrl;
    private String exactTime;
    private int color;


    public NotificationListItem(String sender, String date, Spanned message, String senderPhotoUrl, int color, String mString,String exactTime){
        this.sender = sender;
        this.date = date;
        this.message = message;
        this.senderPhotoUrl = senderPhotoUrl;
        this.color = color;
        this.string = mString;
        this.exactTime = exactTime;

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Spanned getMessage() {
        return message;
    }

    public void setMessage(Spanned message) {
        this.message = message;
    }

    public String getSenderPhotoUrl() {
        return senderPhotoUrl;
    }

    public void setSenderPhotoUrl(String senderPhotoUrl) {
        this.senderPhotoUrl = senderPhotoUrl;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getExactTime() {
        return exactTime;
    }

    public void setExactTime(String exactTime) {
        this.exactTime = exactTime;
    }
}
