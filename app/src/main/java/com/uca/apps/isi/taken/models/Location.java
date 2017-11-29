package com.uca.apps.isi.taken.models;

import java.io.Serializable;

/**
 * Created by Mario Arce on 03/11/2017.
 */

public class Location implements Serializable {
    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
