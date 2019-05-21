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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
    private int counter = 0;
    private JsonParser parser = new JsonParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: reached");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_josh_map_screen);

        initSpinner();

        Intent getIntent = getIntent();
        currentSchedule = getIntent.getExtras().getParcelable("TestSchedule");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.joshMapScreenFragment);
        mapFragment.getMapAsync(this);

        findViewById(R.id.joshMapScreenNext).setOnClickListener(v -> next());
        findViewById(R.id.joshMapScreenBack).setOnClickListener(v -> back());
    }

    //Function for handling map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMap();
    }

    //Button functions
    void next() {
        if (counter < getCurrentDayList().size() - 2) {
            counter++;
            updateMap();
        }
    }

    void back() {
        if (counter > 0) {
            counter--;
            updateMap();
        }
    }

    //Helper functions
    void updateMap() {
        mMap.clear();
        double lat1 = parseLat(getGeocodeJson(counter));
        double lng1 = parseLong(getGeocodeJson(counter));
        LatLng firstLocation = new LatLng(lat1, lng1);
        double lat2 = parseLat(getGeocodeJson(counter + 1));
        double lng2 = parseLong(getGeocodeJson(counter + 1));
        Log.d(TAG, "updateMap lat1, lng1, lat2, lng2: " + lat1 + " " + lng1 + " " + lat2 + " " + lng2);
        LatLng secondLocation = new LatLng(lat2, lng2);
        Marker marker1 = mMap.addMarker(new MarkerOptions()
                .position(firstLocation)
                .title(getCurrentDayList().get(counter).getLocation()));
        Marker marker2 = mMap.addMarker(new MarkerOptions()
                .position(secondLocation)
                .title(getCurrentDayList().get(counter + 1).getLocation()));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker1.getPosition());
        builder.include(marker2.getPosition());
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        mMap.animateCamera(cu);
    }

    void initSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.joshMapScreenDaySpin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.daysOfTheWeek,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    void setTime() {
        TextView time = findViewById(R.id.joshMapScreenTime);
        time.setText("");
        time.setText(parseDistanceTime(getDistanceMatrixJson()));
    }
    
    private List<CourseInfo> getCurrentDayList() {
        Log.d(TAG, "getCurrentDayList: Reached");
        Spinner spinner = (Spinner) findViewById(R.id.joshMapScreenDaySpin);
        String currentDay = spinner.getSelectedItem().toString();
        if (currentDay.equals("Monday")) {
            return currentSchedule.getMonday();
        } else if (currentDay.equals("Tuesday")) {
            return currentSchedule.getTuesday();
        } else if (currentDay.equals("Wednesday")) {
            return currentSchedule.getWednesday();
        } else if (currentDay.equals("Thursday")) {
            return currentSchedule.getThursday();
        } else if (currentDay.equals("Friday")) {
            return currentSchedule.getFriday();
        }
        Log.d(TAG, "getCurrentDayList: Returned null");
        return null;
    }

    private String checkForSpaces(String input) {
        if (input.contains(" ")) {
            input = input.replace(" ", "%20");
        }
        return input;
    }

    //Parsing functions
    private JsonObject getDistanceMatrixJson() {
        Log.d(TAG, "getDistanceMatrixJson: Reached");
        String urlStart = "https://maps.googleapis.com/maps/api/distancematrix/json?mode=walking";
        String urlOrigins = "&origins=" + checkForSpaces(getCurrentDayList().get(counter).getLocation());
        String urlDestinations = "&destinations=" + checkForSpaces(getCurrentDayList().get(counter + 1).getLocation());
        String urlEnd = "&key=" + getString(R.string.google_maps_key);
        String urlTotal = urlStart + urlOrigins + urlDestinations + urlEnd;
        //Toast.makeText(joshMapScreen.this, "Url used: " + urlTotal, Toast.LENGTH_SHORT);
        Log.d(TAG, "getDistanceMatrixJson total url used: " + urlTotal);
        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            Log.d(TAG, "getDistanceMatrixJson: JsonObject successfully returned");
            return parser.parse(getRequest.execute(urlTotal).get()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    
    
    private JsonObject getGeocodeJson(int index) {
        Log.d(TAG, "getGeocodeJson: Reached");
        String urlStart = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String urlAddress = checkForSpaces(getCurrentDayList().get(index).getLocation()) + "%20Urbana";
        String urlEnd = "&key=" + getString(R.string.google_maps_key);
        String urlTotal = urlStart + urlAddress + urlEnd;
        //Toast.makeText(joshMapScreen.this, "Url used: " + urlTotal, Toast.LENGTH_SHORT);
        Log.d(TAG, "getGeocodeJson total url used: " + urlTotal);
        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            return parser.parse(getRequest.execute(urlTotal).get()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String parseDistanceTime(JsonObject toParse) {
        Log.d(TAG, "parseDistanceTime: Reached");
        try {
            Log.d(TAG, "parseDistanceTime: Parsing started");
            JsonArray rows = toParse.getAsJsonArray("rows");
            JsonObject object = rows.get(0).getAsJsonObject();
            JsonArray elements = object.get("elements").getAsJsonArray();
            JsonObject object2 = elements.get(0).getAsJsonObject();
            JsonObject duration = object2.get("duration").getAsJsonObject();
            Log.d(TAG, "parseDistanceTime: Parsing ended");
            return duration.get("text").getAsString();
        } catch (Exception e) {
            Log.d(TAG, "parseDistanceTime: Parsing error");
            e.printStackTrace();
        }
        return null;
    }

    private double parseLat(JsonObject toParse) {
        Log.d(TAG, "parseLat: Reached");
        try {
            Log.d(TAG, "parseLat: Parsing started");
            JsonArray results = toParse.getAsJsonArray("results");
            JsonObject object = results.get(0).getAsJsonObject();
            JsonObject geometry = object.getAsJsonObject("geometry");
            JsonObject location = geometry.get("location").getAsJsonObject();
            Log.d(TAG, "parseLat: Parsing ended");
            Log.d(TAG, "parseLat result: " + location.get("lat").getAsInt());
            return location.get("lat").getAsDouble();
        } catch (Exception e) {
            Log.d(TAG, "parseLat: Parsing error");
            e.printStackTrace();
        }
        return 0;
    }

    private double parseLong(JsonObject toParse) {
        Log.d(TAG, "parseLong: Reached");
        try {
            Log.d(TAG, "parseLong: Parsing started");
            JsonArray results = toParse.getAsJsonArray("results");
            JsonObject object = results.get(0).getAsJsonObject();
            JsonObject geometry = object.getAsJsonObject("geometry");
            JsonObject location = geometry.get("location").getAsJsonObject();
            Log.d(TAG, "parseLong: Parsing ended");
            Log.d(TAG, "parseLong result: " + location.get("lng").getAsInt());
            return location.get("lng").getAsDouble();
        } catch (Exception e) {
            Log.d(TAG, "parseLong: Parsing error");
            e.printStackTrace();
        }
        return 0;
    }
}
