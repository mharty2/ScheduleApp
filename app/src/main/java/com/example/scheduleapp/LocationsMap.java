package com.example.scheduleapp;

import com.example.scheduleapp.Objects.Location;
import com.example.scheduleapp.Objects.Schedule;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class LocationsMap {
    private Map<String, Location> locationsMap;

    private static LocationsMap instance;
    public static LocationsMap getInstance() {
        if (instance == null) {
            instance = new LocationsMap();
        }
        return instance;
    }

    private LocationsMap(){}

    public Map<String, Location> getLocationsMap() {
        if (locationsMap != null) {
            return locationsMap;
        }
        Map<String, Location> map = new HashMap<>();
        return map;
    }

    public void setLocationsMap(Map<String, Location> locationsMap) {
        this.locationsMap = locationsMap;
    }
}
