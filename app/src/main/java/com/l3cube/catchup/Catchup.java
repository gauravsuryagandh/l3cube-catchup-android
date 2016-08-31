package com.l3cube.catchup;

import android.graphics.drawable.Drawable;

/**
 * Created by push on 31/8/16.
 */
public class Catchup {
    private int placeImage;
    private String title, inviter, place, time;

    public Catchup(int placeImage, String title, String inviter, String place, String time) {
        this.placeImage = placeImage;
        this.title = title;
        this.inviter = inviter;
        this.place = place;
        this.time = time;
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

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
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
