package com.l3cube.catchup.models;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by push on 31/8/16.
 */
public class Catchup {
    private String objectId;
    private int placeImage;
    private String title, place, time;
    private ParseUser inviter;

    public Catchup(int placeImage, String title, ParseUser inviter, String place, String time, String objectId) {
        this.placeImage = placeImage;
        this.title = title;
        this.inviter = inviter;
        this.place = place;
        this.time = time;
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getPlaceImage() {
        return placeImage;
    }

    public void setPlaceImage(int placeImage) {
        this.placeImage = placeImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ParseUser getInviter() {
        return inviter;
    }

    public void setInviter(ParseUser inviter) {
        this.inviter = inviter;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
