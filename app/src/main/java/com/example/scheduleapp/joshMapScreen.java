package com.example.scheduleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class joshMapScreen extends AppCompatActivity {

    private GoogleMap mMap;
    private Schedule currentSchedule;

    //ToDo: get current day from day spinner
    private String currentDay;

    private int counter = 0;
    private JsonParser parser = new JsonParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView time = findViewById(R.id.joshMapScreenTime);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_josh_map_screen);

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

        /*ToDo: figure out how the Schedule is loaded in. This whole class bases off
        a single stored Schedule (see currentSchedule above) */
        //currentSchedule = GET SAVED SCHEDULE SOMEHOW
        List<CourseInfo> monday = new ArrayList<>();
        monday.add(new CourseInfo("Foellinger Auditorium"));
        monday.add(new CourseInfo("Altgelt Hall"));
        currentSchedule = new Schedule();
        currentSchedule.schedule.put("Monday", monday);

        setTime();

    }

    //JOSH GIVES UP!
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Do stuff with the map here!
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    void reload() {
        getGeocodeJson();
        getDistanceMatrixJson();
    } */

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
