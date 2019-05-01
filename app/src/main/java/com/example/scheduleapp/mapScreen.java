package com.example.scheduleapp;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class mapScreen extends AppCompatActivity
        implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    /**Stores the current selected day as an integer. Monday is 1, Tuesday 2, ect.*/
    private static int currentDay;
    /**Coordinates, these will be initialized in accordance to which day it is.*/
    private double[] latitudes;
    private double[] longitudes;
    /**Stores the indexes which the two selected classes are. These are the indexes which will
     * be used to access the right coordinates.
     */
    private static int currentIndex1 = 0;
    private static int currentIndex2 = 0;
    /**Google map stuff.*/
    private MapView mapView = null;
    private GoogleMap googleMap = null;
    public boolean centered = false;
    /**Stores the list of days for the spinner to display.*/
    private static String[] items = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    /**Stores the list of classes for the other spinners to display.**/
    private static String[] classes = new String[]{};
    /**Spinners.*/
    private Spinner selectDay, classOne, classTwo;
    /**Stores the distance between the two points*/
    private static float distance = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        findViewById(R.id.map_home).setOnClickListener(v->home());
        findViewById(R.id.backToDailyBtn).setOnClickListener(v -> returnToDaily());
        //This needs to be filled out to set the building placements. Check the Locator class to see how to input
        //these correctly. You technically can do this at any point in any other class.
        Locator.inputCourseCoordinates(null, null, null);
        //This accepts the list of courses a person is taking so the spinner includes the proper list and the coordinates are set.
        Locator.processCoordinates(null);

        /**
         * These are the spinners which select the day. It's a mess, but all you need to know is that
         * when an item is selected, the "onItemSelected" method is called.
         */
        selectDay = findViewById(R.id.mapScreenDay);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        selectDay.setAdapter(adapter);
        selectDay.setOnItemSelectedListener(this);

        classOne = findViewById(R.id.map_spinner_one);
        ArrayAdapter<String> adapterClassOne = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classes);
        classOne.setAdapter(adapter);
        classOne.setOnItemSelectedListener(this);

        classTwo = findViewById(R.id.map_spinner_two);
        ArrayAdapter<String> adapterClassTwo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classes);
        classTwo.setAdapter(adapter);
        classTwo.setOnItemSelectedListener(this);

        try {
            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        } catch (ClassCastException ignore) { }
    }

    @Override
    public void onItemSelected(final AdapterView<?> arg0, final View arg1, final int position, final long id) {
        Toast.makeText(getApplicationContext(), items[position], Toast.LENGTH_LONG).show();
        setCurrentDay((String) selectDay.getSelectedItem());
        setIndex((String) classOne.getSelectedItem(), 1);
        setIndex((String) classTwo.getSelectedItem(), 2);
        latitudes = Locator.getLatitudes()[currentDay];
        longitudes = Locator.getLongitudes()[currentDay];
        processNewLocation();
    }
    /**
     *Helper function.
     */
    public void setIndex(final String className, final int spinner) {
        for (int i = 0; i < classes.length; i++) {
            if (className.equals(classes[i])) {
                if (spinner == 1) {
                    currentIndex1 = i;
                } else {
                    currentIndex2 = i;
                }
                break;
            }
        }
    }
    /**
     * Sets the currentDay variable.
     * @param day string with the day.
     */
    public void setCurrentDay(final String day) {
        switch (day) {
            case "Monday":
                currentDay = 0;
                break;
            case "Tuesday":
                currentDay = 1;
                break;
            case "Wednesday":
                currentDay = 2;
                break;
            case "Thursday":
                currentDay = 2 + 1;
                break;
            case "Friday":
                currentDay = 2 + 2;
                break;
            default:
        }
    }
    @Override
    public void onNothingSelected(final AdapterView<?> arg0) {
        googleMap.clear();
        centered = false;
    }

    /**
     * This method places the markers and calculates the distance.
     */
    public void processNewLocation() {
        float hue = BitmapDescriptorFactory.HUE_RED;
        googleMap.clear();
        if (googleMap == null || latitudes.length == 0) {
            return;
        }
        centered = false;
        distance = 0;
        try {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitudes[currentIndex1], longitudes[currentIndex1]))
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            Location loc1 = new Location("");
            loc1.setLatitude(latitudes[currentIndex1]);
            loc1.setLongitude(longitudes[currentIndex1]);
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitudes[currentIndex2], longitudes[currentIndex2]))
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            Location loc2 = new Location("");
            loc2.setLatitude(latitudes[currentIndex2]);
            loc2.setLongitude(longitudes[currentIndex2]);
            distance = loc1.distanceTo(loc2);
            //This line displays the distance. You can add the textview wherever and convert it to time.
            //displayDistance.setText("Distance: " + distance);
        } catch (ArrayIndexOutOfBoundsException e) {
            googleMap.clear();
        }
    }
    void returnToDaily() {
        Intent intent = new Intent(mapScreen.this, dailySchedule.class);
        startActivity(intent);
        finish();
    }
    void home() {
        Intent intent = new Intent(mapScreen.this, ChooseSchedule.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onMapReady(final GoogleMap setGoogleMap) {
        googleMap = setGoogleMap;
    }
}
