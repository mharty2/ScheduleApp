package com.example.scheduleapp.Objects;

import com.google.android.gms.maps.model.LatLng;

public class Location {
    private String name;
    private double lat;
    private double lng;

    public Location(String name, LatLng coor) {
        this.name = name;
        lat = coor.latitude;
        lng = coor.longitude;

    }

    public Location(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public void setLatLng(LatLng input) {
        this.lat = input.latitude;
        this.lng = input.longitude;
    }

    public LatLng returnLatLng() {
        return new LatLng(lat, lng);
    }
}
