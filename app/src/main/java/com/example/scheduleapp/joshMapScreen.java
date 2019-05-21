package com.example.scheduleapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.LogDescriptor;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class joshMapScreen extends AppCompatActivity implements OnMapReadyCallback {
    //Tag for debugging
    private static final String TAG = "JoshMapScreen";

    //Global Variables
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //Variables
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private Schedule currentSchedule;

    //ToDo: get current day from day spinner
    private String currentDay;

    private int counter = 0;
    private JsonParser parser = new JsonParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: reached");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_josh_map_screen);

        TextView time = findViewById(R.id.joshMapScreenTime);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.joshMapScreenFragment);
        mapFragment.getMapAsync(this);

        //getLocationPermission();

        //Making spinner functional
        Spinner spinner = (Spinner) findViewById(R.id.joshMapScreenDaySpin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.daysOfTheWeek,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        currentDay = spinner.getSelectedItem().toString();

        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.joshMapScreenFragment);
        //mapFragment.getMapAsync(this);

        //Intent intent = getIntent();
        //differentSchedule = intent.getExtras().getParcelable("schedule");

        /*ToDo: figure out how the Schedule is loaded in. This whole class bases off
        a single stored Schedule (see currentSchedule above) */
        //currentSchedule = GET SAVED SCHEDULE SOMEHOW\

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.joshMapScreenFragment);

        mapFragment.getMapAsync(joshMapScreen.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocationPermission: first check passed");
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission: second check passed, boolean set true");
                mLocationPermissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        Log.d(TAG, "getLocationPermission: exited if statement");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionsGranted = false;

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    initMap();
                }
            }
        }
    }

    void setTime() {
        TextView time = (TextView) findViewById(R.id.joshMapScreenTime);
        time.setText("");
        time.setText(parseDistanceTime(getDistanceMatrixJson()));
    }

    void nextClass() {
        counter++;
        setTime();
    }

    private String checkForSpaces(String input) {
        if (input.contains(" ")) {
            input.replace(" ", "%20");
        }
        return input;
    }

    //Need to fix
    private JsonObject getGeocodeJson() {
        if (currentDay == null) {
            currentDay = "Monday";
        }
        String urlStart = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String urlAddress = currentSchedule.schedule.get(currentDay).get(counter).getLocation();
        String urlEnd = "&key=AIzaSyCi6iFmEMZZOweaUQyA60i86HE90mV4XpU";
        String urlTotal = urlStart + urlAddress + urlEnd;
        Log.d("GeocodeUrl", urlTotal);
        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            return parser.parse(getRequest.execute(urlTotal).get()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonObject getDistanceMatrixJson() {
        String urlStart = "https://maps.googleapis.com/maps/api/distancematrix/json?mode=walking";
        String urlOrigins = "&origins=" + checkForSpaces(currentSchedule.schedule.get(currentDay).get(counter).getLocation());
        String urlDestinations = "&destinations=" + checkForSpaces(currentSchedule.schedule.get(currentDay).get(counter + 1).getLocation());
        String urlEnd = "&key=AIzaSyCi6iFmEMZZOweaUQyA60i86HE90mV4XpU";
        String urlTotal = urlStart + urlOrigins + urlDestinations + urlEnd;
        Log.d("DistanceMatrixUrl", urlTotal);
        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            return parser.parse(getRequest.execute(urlTotal).get()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String parseDistanceTime(JsonObject toParse) {
        try {
            JsonArray rows = toParse.getAsJsonArray("rows");
            JsonObject object = rows.get(0).getAsJsonObject();
            JsonArray elements = object.get("elements").getAsJsonArray();
            JsonObject object2 = elements.get(0).getAsJsonObject();
            JsonObject duration = object2.get("duration").getAsJsonObject();
            return duration.get("text").getAsString();
        } catch (Exception e) {
            Log.d("Parsing error", "you done messed up son");
            e.printStackTrace();
        }
        return null;
    }

    //Need to fix
    private String parseLat(JsonObject toParse) {
        try {
            JsonArray results = toParse.getAsJsonArray("results");
            JsonObject geometry = results.get(2).getAsJsonObject();
            JsonObject location = geometry.get("location").getAsJsonObject();
            return location.get("lat").getAsString();
        } catch (Exception e) {
            Log.d("Parsing error", "you done messed up son");
            e.printStackTrace();
        }
        return null;
    }

    //Need to fix
    private String parseLong(JsonObject toParse) {
        try {
            JsonArray results = toParse.getAsJsonArray("results");
            JsonObject geometry = results.get(2).getAsJsonObject();
            JsonObject location = geometry.get("location").getAsJsonObject();
            return location.get("lng").getAsString();
        } catch (Exception e) {
            Log.d("Parsing error", "you done messed up son");
            e.printStackTrace();
        }
        return null;
    }
}
