package com.example.scheduleapp.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.scheduleapp.LocationsMap;
import com.example.scheduleapp.Objects.CourseInfo;
import com.example.scheduleapp.Objects.Location;
import com.example.scheduleapp.R;
import com.example.scheduleapp.Objects.Schedule;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ChooseSchedule extends AppCompatActivity {

    //Tag for debugging
    private static final String TAG = "ChooseSchedule";

    //Variables
    private boolean mLocationPermissionGranted = false;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";
    private static final String PREFS_NAME = "ScheduleApp";
    private CollectionReference sharedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_schedule);
        findViewById(R.id.CreateSchedule).setOnClickListener(v -> createSchedule());
        findViewById(R.id.LoadSchedule).setOnClickListener(v -> loadSchedule());
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        auth.signInWithCustomToken(sharedPreferences.getString(PREF_USER_ID_TOKEN, ""));
        db = FirebaseFirestore.getInstance();
        Map<String, Location> map = new HashMap<>();
        //contains a single hashmap which pairs a location to a LatLng
        sharedLocations = db.collection("SharedLocations");
        sharedLocations.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        Location loc = snapshot.toObject(Location.class);
                        map.put(loc.getName(), loc);
                    }
                    LocationsMap.getInstance().setLocationsMap(map);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }

    void createSchedule() {
        Intent intent = new Intent(ChooseSchedule.this, createSchedule.class);
        startActivity(intent);
        finish();
    }
    void loadSchedule() {
        Intent intent = new Intent(ChooseSchedule.this, loadSchedule.class);
        startActivity(intent);
        finish();
    }
}
