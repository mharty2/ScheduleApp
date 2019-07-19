package com.example.scheduleapp.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.scheduleapp.LocationsMap;
import com.example.scheduleapp.Objects.CourseInfo;
import com.example.scheduleapp.Objects.HttpGetRequest;
import com.example.scheduleapp.Objects.Location;
import com.example.scheduleapp.Objects.Schedule;
import com.example.scheduleapp.R;
import com.example.scheduleapp.SelectedSchedule;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class CourseSearchAdapter extends RecyclerView.Adapter<CourseSearchAdapter.ViewHolder> {

    private List<CourseInfo> listItems;
    private Activity mActivity;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ScheduleApp";
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";
    private Schedule scheduleInProgress = SelectedSchedule.getInstance().getSchedule();
    private double walkingSpeed = 1.4;
    //arbitrary multiplier to compensate for straight line calculations
    private double timeMultiplier = 1.2;
    private String distInfo;
    private JsonParser parser = new JsonParser();
    Context context;
    private static final String KEY_LOCATION_MAP = "Schedule Locations";
    private CollectionReference sharedLocations;
    private HashMap<String, Location> locationMap;

    public CourseSearchAdapter(List<CourseInfo> listItems, Activity mActivity) {
        this.listItems = listItems;
        this.mActivity = mActivity;
        context = mActivity.getApplicationContext();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = mActivity.getSharedPreferences(PREFS_NAME, mActivity.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCustomToken(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        //contains a single hashmap which pairs a location to a LatLng
        sharedLocations = db.collection("SharedLocations");
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_cardview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseSearchAdapter.ViewHolder viewHolder, int i) {
        CourseInfo item = listItems.get(i);
        viewHolder.txtViewHeading.setText(item.getName() + " - " + item.getType());
        viewHolder.txtViewSection.setText(item.getSection());
        viewHolder.txtViewTime.setText(item.getTime());
        viewHolder.txtViewDays.setText(item.getDays());
        viewHolder.txtViewLocation.setText(item.getLocation());
        viewHolder.txtViewCreditHours.setText(item.getCreditHours());
        viewHolder.txtViewCRN.setText(item.getCrn());
        if (item.checkForInterference(scheduleInProgress.getClassList())) {
            viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#FF0000"));
            Log.d(TAG, "onBindViewHolder: There is interference with" + item.getSection());
        } else {
            ArrayList<CourseInfo> nearestClasses = item.getNearestClasses(scheduleInProgress.getClassList());
            double maxTimeToWalk;
            if (nearestClasses != null) {
                switch (nearestClasses.size()) {
                    case 0:
                        maxTimeToWalk = 0;
                        break;
                    case 1:
                        maxTimeToWalk = approxTime(item, nearestClasses.get(0));
                        break;
                    default:
                        maxTimeToWalk = 0;
                        for (CourseInfo course : nearestClasses) {
                            if (approxTime(item, course) > maxTimeToWalk) {
                                maxTimeToWalk = approxTime(item, course);
                            }
                        }
                }
                Log.d(TAG, "onBindViewHolder: maxTime value:" + maxTimeToWalk);
                if (maxTimeToWalk >= 12) {
                    viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (maxTimeToWalk >= 8) {
                    viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#FFFF00"));
                } else {
                    viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#00FF00"));
                }
            }
        }



        //when card is clicked.
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Choose section \"" + item.getSection() + "\" ?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toCreateSchedule(i);
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listItems == null) {
            return 0;
        }
        return listItems.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        //Were public before, made private
        private TextView txtViewHeading;
        private TextView txtViewSection;
        private TextView txtViewTime;
        private TextView txtViewDays;
        private TextView txtViewLocation;
        private TextView txtViewCreditHours;
        private TextView txtViewCRN;
        private LinearLayout linearLayout;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewHeading = itemView.findViewById(R.id.txtViewHeading);
            txtViewSection = itemView.findViewById(R.id.txtViewSection);
            txtViewTime = itemView.findViewById(R.id.txtViewTime);
            txtViewDays = itemView.findViewById(R.id.txtViewDays);
            txtViewLocation = itemView.findViewById(R.id.txtViewLocation);
            txtViewCreditHours = itemView.findViewById(R.id.txtViewCreditHours);
            txtViewCRN = itemView.findViewById(R.id.txtViewCRN);
            linearLayout = itemView.findViewById(R.id.linLayout);
        }
    }
    private void toCreateSchedule(int i) {

        //Log.d("toCreateSchedule","toCreateSchedule reached");
        Intent returnIntent = new Intent();
        //Log.d("asdf", "Stored courseInfo: " + listItems.get(i).getName());
        Bundle storedCourse = new Bundle();
        storedCourse.putParcelable("storedCourse", listItems.get(i));
        //Log.d("toAdd", "toAdd: " + listItems.get(i));
        returnIntent.putExtras(storedCourse);

        mActivity.setResult(2, returnIntent);
        mActivity.finish();
    }

    private JsonObject getGeocodeJson(CourseInfo course) {
        String urlStart = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String urlAddress = checkForSpacesAndEtc(course.getLocation() + " University of Illinois at Urbana Champaign");
        String urlEnd = "&key=" + context.getString(R.string.google_maps_key);
        String urlTotal = urlStart + urlAddress + urlEnd;
        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            return parser.parse(getRequest.execute(urlTotal).get()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getGeocodeJson: could not get location for:" + course.getLocation());
        }
        return null;
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

    private double parseLat(JsonObject toParse) {
        try {
            JsonArray results = toParse.getAsJsonArray("results");
            JsonObject object = results.get(0).getAsJsonObject();
            JsonObject geometry = object.getAsJsonObject("geometry");
            JsonObject location = geometry.get("location").getAsJsonObject();
            return location.get("lat").getAsDouble();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double parseLong(JsonObject toParse) {
        try {
            JsonArray results = toParse.getAsJsonArray("results");
            JsonObject object = results.get(0).getAsJsonObject();
            JsonObject geometry = object.getAsJsonObject("geometry");
            JsonObject location = geometry.get("location").getAsJsonObject();
            return location.get("lng").getAsDouble();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private LatLng convertToLatLng(CourseInfo input) {
        LatLng toReturn = new LatLng(parseLat(getGeocodeJson(input)), parseLong(getGeocodeJson(input)));
        return toReturn;
    }

    public double approxTime(CourseInfo a, CourseInfo b) {
        if (locationMap == null) {
            locationMap = (HashMap<String, Location>) LocationsMap.getInstance().getLocationsMap();
        }
        LatLng coor1 = null;
        LatLng coor2 = null;
        try {
            coor1 = locationMap.get(a.getLocation()).returnLatLng();
            coor2 = locationMap.get(b.getLocation()).returnLatLng();
        } catch (Exception e) {
            Log.d(TAG, "it's intended if its null pointer: " + e.getMessage());
        }
        if (coor1 == null || coor2 == null) {
            if (coor1 == null) {
                coor1 = convertToLatLng(a);
                Location location = new Location(a.getLocation(), coor1);
                locationMap.put(a.getLocation(), location);
                sharedLocations.document(a.getLocation()).set(location);
            }
            if (coor2 == null) {
                coor2 = convertToLatLng(b);
                Location loc2 = new Location(b.getLocation(), coor2);
                locationMap.put(b.getLocation(), loc2);
                sharedLocations.document(b.getLocation()).set(loc2);
            }
            LocationsMap.getInstance().setLocationsMap(locationMap);
        }
        double dist = haversineDist(coor1, coor2);
        Log.d(TAG, "approxTime: Haversine dist: "+dist);
        double time = (dist/walkingSpeed)*timeMultiplier;
        int minutes = (int) time/60;
        int seconds = (int) (time-minutes*60);
        distInfo = "Duration: " + minutes + " minute(s) " + seconds + " seconds";
        return minutes;
    }

    private double haversineDist(LatLng a, LatLng b) {
        double lat1Rad = Math.toRadians(a.latitude);
        double lat2Rad = Math.toRadians(b.latitude);
        double latDist = Math.toRadians(b.latitude - a.latitude);
        double longDist = Math.toRadians(b.longitude - a.longitude);
        double x = Math.sin(latDist/2) * Math.sin(latDist/2) + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(longDist/2) * Math.sin(longDist/2);
        double y = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1-x));
        double radius = 6371000;
        return  y * radius;
    }

    public void setWalkingSpeed(double input) {
        walkingSpeed = input;
    }
}
