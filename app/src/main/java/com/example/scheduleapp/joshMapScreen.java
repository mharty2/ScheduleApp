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
    TextView time = findViewById(R.id.joshMapScreenTime);
    private int counter = 0;
    private JsonParser parser = new JsonParser();
    Spinner spinner = (Spinner) findViewById(R.id.joshMapScreenDaySpin);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: reached");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_josh_map_screen);

        initSpinner();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.joshMapScreenFragment);
        mapFragment.getMapAsync(this);

        //Intent intent = getIntent();
        //differentSchedule = intent.getExtras().getParcelable("schedule");

        /*ToDo: figure out how the Schedule is loaded in. This whole class bases off
        a single stored Schedule (see currentSchedule above) */
        //currentSchedule = GET SAVED SCHEDULE SOMEHOW\

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    //Helper functions
    void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.daysOfTheWeek,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    void setTime() {
        time.setText("");
        time.setText(parseDistanceTime(getDistanceMatrixJson()));
    }

    void nextClass() {
        counter++;
        setTime();
    }
    
    private List<CourseInfo> getCurrentDayList() {
        Log.d(TAG, "getCurrentDayList: Reached");
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
            input.replace(" ", "%20");
        }
        return input;
    }

    //Parsing functions
    private JsonObject getDistanceMatrixJson() {
        Log.d(TAG, "getDistanceMatrixJson: Reached");
        String urlStart = "https://maps.googleapis.com/maps/api/distancematrix/json?mode=walking";
        String urlOrigins = "&origins=" + checkForSpaces(getCurrentDayList().get(counter).getLocation());
        String urlDestinations = "&destinations=" + checkForSpaces(getCurrentDayList().get(counter + 1).getLocation());
        String urlEnd = "&key=" + "@string/google_maps_key";
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
    
    private JsonObject getGeocodeJson() {
        Log.d(TAG, "getGeocodeJson: Reached");
        String urlStart = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String urlAddress = checkForSpaces(getCurrentDayList().get(counter).getLocation());
        String urlEnd = "&key=" + "@string/google_maps_key";
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

    private String parseLat(JsonObject toParse) {
        Log.d(TAG, "parseLat: Reached");
        try {
            Log.d(TAG, "parseLat: Parsing started");
            JsonArray results = toParse.getAsJsonArray("results");
            JsonObject object = results.get(0).getAsJsonObject();
            JsonObject geometry = object.getAsJsonObject("geometry");
            JsonObject location = geometry.get("location").getAsJsonObject();
            Log.d(TAG, "parseLat: Parsing ended");
            return location.get("lat").getAsString();
        } catch (Exception e) {
            Log.d(TAG, "parseLat: Parsing error");
            e.printStackTrace();
        }
        return null;
    }

    private String parseLong(JsonObject toParse) {
        Log.d(TAG, "parseLong: Reached");
        try {
            Log.d(TAG, "parseLong: Parsing started");
            JsonArray results = toParse.getAsJsonArray("results");
            JsonObject object = results.get(0).getAsJsonObject();
            JsonObject geometry = object.getAsJsonObject("geometry");
            JsonObject location = geometry.get("location").getAsJsonObject();
            Log.d(TAG, "parseLong: Parsing ended");
            return location.get("lng").getAsString();
        } catch (Exception e) {
            Log.d(TAG, "parseLong: Parsing error");
            e.printStackTrace();
        }
        return null;
    }
}
