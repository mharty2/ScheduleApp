package com.example.scheduleapp.UI;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scheduleapp.Objects.CourseInfo;
import com.example.scheduleapp.Objects.HttpGetRequest;
import com.example.scheduleapp.Objects.PolylineData;
import com.example.scheduleapp.R;
import com.example.scheduleapp.Objects.Schedule;
import com.example.scheduleapp.SelectedSchedule;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

public class joshMapScreen extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {
    //Tag for debugging
    private static final String TAG = "JoshMapScreen";

    //Global Variables

    //TODO Do we need these? If not just delete, I'm for it
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private final String KEY_SELECTED_SCHEDULE = "KEY_SELECTED_SCHEDULE";
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";
    private static final String PREFS_NAME = "ScheduleApp";
    private static final String KEY_SELECTED_DAY = "KEY_SELECTED_DAY";

    //Variables
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private Schedule schedule;
    private int counter = 0;
    private JsonParser parser = new JsonParser();
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private DocumentReference userDocRef;
    private CollectionReference usersSchedulesCollec;
    private FirebaseFirestore db;
    private List<CourseInfo> currentDayList;
    private String currentDay;
    private String scheduleName;
    private TextView txtViewClassOne;
    private TextView txtViewClassTwo;
    private TextView txtViewDist;
    private GeoApiContext mGeoApiContext;
    private ArrayList<PolylineData> mPolylinesData = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: reached");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_josh_map_screen);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        txtViewClassOne = findViewById(R.id.joshMapScreenClassOne);
        txtViewClassTwo = findViewById(R.id.joshMapScreenClassTwo);
        txtViewDist = findViewById(R.id.textViewsJoshMapScreenDist);


        initSpinner();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.joshMapScreenFragment);
        mapFragment.getMapAsync(this);
        mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();

        findViewById(R.id.joshMapScreenNext).setOnClickListener(v -> next());
        findViewById(R.id.joshMapScreenBack).setOnClickListener(v -> back());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.signInWithCustomToken(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
        userDocRef = db.collection("Users").document(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
        usersSchedulesCollec = userDocRef.collection("Schedules");
        scheduleName = sharedPreferences.getString(KEY_SELECTED_SCHEDULE, null);
        //if we want to make the default the day they clicked "see mapview" on. needs a method for assigning the list then.
        currentDay = sharedPreferences.getString(KEY_SELECTED_DAY, null);
    }

    public void loadSchedule() {
        schedule = SelectedSchedule.getInstance().getSchedule();
    }

    //Function for handling map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.setOnPolylineClickListener(this);
            updateMap();
        } catch (Error error) {

        } catch (Exception e) {

        }
    }

    //Button functions
    void next() {
        if (counter < currentDayList.size() - 2) {
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
        if(schedule == null) {
            loadSchedule();
        }
        getCurrentDayList();
        if (currentDayList != null && currentDayList.size()>1) {
            double lat1 = parseLat(getGeocodeJson(counter));
            double lng1 = parseLong(getGeocodeJson(counter));
            LatLng firstLocation = new LatLng(lat1, lng1);
            double lat2 = parseLat(getGeocodeJson(counter + 1));
            double lng2 = parseLong(getGeocodeJson(counter + 1));
            //Log.d(TAG, "updateMap lat1, lng1, lat2, lng2: " + lat1 + " " + lng1 + " " + lat2 + " " + lng2);
            LatLng secondLocation = new LatLng(lat2, lng2);
            Marker marker1 = mMap.addMarker(new MarkerOptions()
                    .position(firstLocation)
                    .title(currentDayList.get(counter).getLocation()));
            Marker marker2 = mMap.addMarker(new MarkerOptions()
                    .position(secondLocation)
                    .title(currentDayList.get(counter + 1).getLocation()));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(marker1.getPosition());
            builder.include(marker2.getPosition());
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
            mMap.animateCamera(cu);
            //Could set to location?
            txtViewClassOne.setText(currentDayList.get(counter).getName());
            txtViewClassTwo.setText(currentDayList.get(counter + 1).getName());

            com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                    marker2.getPosition().latitude,
                    marker2.getPosition().longitude
            );
            DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
            directions.mode(TravelMode.WALKING);

            directions.alternatives(true);
            directions.origin(
                    new com.google.maps.model.LatLng(
                            marker1.getPosition().latitude,
                            marker1.getPosition().longitude
                    )
            );
            //checking that request is made
            Log.d(TAG, "calculateDirections: destination: " + destination.toString());
            directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    addPolylinesToMap(result);
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

                }
            });
        }
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if(mPolylinesData.size() > 0) {
                    for (PolylineData polylineData: mPolylinesData) {
                        polylineData.getPolyline().remove();
                    }
                    mPolylinesData.clear();
                    mPolylinesData = new ArrayList<>();
                }
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    polyline.setClickable(true);
                    mPolylinesData.add(new PolylineData(polyline, route.legs[0]));

                }
            }
        });
    }

    void initSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.joshMapScreenDaySpin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.daysOfTheWeek,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMap != null) {
                    Log.d(TAG, "onItemSelected: new Item Selected");
                    updateMap();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //TODO Do we need this?
    void setTime() {
        TextView time = findViewById(R.id.joshMapScreenTime);
        time.setText("");
        time.setText(parseDistanceTime(getDistanceMatrixJson()));
    }

    private void getCurrentDayList() {
        if (schedule != null) {
            Log.d(TAG, "getCurrentDayList: Reached");
            Spinner spinner = (Spinner) findViewById(R.id.joshMapScreenDaySpin);
            String currentDay = spinner.getSelectedItem().toString();
            Log.d(TAG, "schedule: " + schedule);
            Log.d(TAG, "currentDay: " + currentDay);
            if (currentDay.equals("Monday")) {
                currentDayList = schedule.getMonday();
            } else if (currentDay.equals("Tuesday")) {
                currentDayList = schedule.getTuesday();
            } else if (currentDay.equals("Wednesday")) {
                currentDayList = schedule.getWednesday();
            } else if (currentDay.equals("Thursday")) {
                currentDayList = schedule.getThursday();
            } else if (currentDay.equals("Friday")) {
                currentDayList = schedule.getFriday();
            }
            Log.d(TAG, "getCurrentDayList: Returned null");
        }
    }

    private String checkForSpacesAndEtc(String input) {
        String toReturn = input;
        if (input.contains(" ")) {
            toReturn = toReturn.replace(" ", "%20");
        }
        if (toReturn.contains("&")) {
            toReturn = toReturn.replace("&", "and");
        }
        if (toReturn.contains("Bldg")) {
            toReturn = toReturn.replace("Bldg", "building");
        }
        if (toReturn.contains("Eng")) {
            toReturn = toReturn.replace("Eng","engineering");
        }
        return toReturn;
    }

    //Parsing functions
    private JsonObject getDistanceMatrixJson() {
        Log.d(TAG, "getDistanceMatrixJson: Reached");
        String urlStart = "https://maps.googleapis.com/maps/api/distancematrix/json?mode=walking";
        String urlOrigins = "&origins=" + checkForSpacesAndEtc(currentDayList.get(counter).getLocation());
        String urlDestinations = "&destinations=" + checkForSpacesAndEtc(currentDayList.get(counter + 1).getLocation());
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
        String urlAddress = checkForSpacesAndEtc(currentDayList.get(index).getLocation() + " University of Illinois at Urbana Champaign");
        String urlEnd = "&key=" + getString(R.string.google_maps_key);
        String urlTotal = urlStart + urlAddress + urlEnd;
        Toast.makeText(joshMapScreen.this, "Url used: " + urlTotal, Toast.LENGTH_SHORT);
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

    @Override
    public void onPolylineClick(Polyline polyline) {

        for(PolylineData polylineData: mPolylinesData){
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSelectedPolyline));
                polylineData.getPolyline().setZIndex(1);
                txtViewDist.setText("Time: " + polylineData.getLeg().duration);
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }


}
