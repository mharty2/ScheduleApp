package com.example.scheduleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class dailySchedule extends AppCompatActivity {

    private final String KEY_SELECTED_SCHEDULE = "KEY_SELECTED_SCHEDULE";
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";
    private static final String PREFS_NAME = "ScheduleApp";
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private Schedule schedule;
    private CollectionReference usersCollecRef;
    private DocumentReference userDocRef;
    private CollectionReference usersSchedulesCollec;
    private FirebaseFirestore db;
    private String scheduleName;
    private TextView txtViewName;
    private TextView txtViewDay;
    private Button next;
    private Button prev;
    private List<CourseInfo> currentDayList;
    private String currentDay;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule);
        findViewById(R.id.daily_home).setOnClickListener(v->home());
        findViewById(R.id.goToMapbtn).setOnClickListener(v -> toMap());
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        txtViewName = findViewById(R.id.textViewDailyName);
        txtViewDay = findViewById(R.id.daily_schedule_day);
        next = findViewById(R.id.daily_next);
        prev = findViewById(R.id.daily_previous);
        next.setOnClickListener(v -> nextDay());
        prev.setOnClickListener(v -> previousDay());
        recyclerView = findViewById(R.id.recyclerViewDaily);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.signInWithCustomToken(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
        usersCollecRef = db.collection("Users");
        userDocRef = db.collection("Users").document(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
        usersSchedulesCollec = userDocRef.collection("Schedules");
        scheduleName = sharedPreferences.getString(KEY_SELECTED_SCHEDULE, null);
        loadSchedule();
    }

    public void loadSchedule() {
        //Queries all of the users schedule to find the one with name = to the one they chose in loadSchedules
        //duplication shouldn't be a problem as if they use the same name for a schedule, it will actually overwrite it
        //which is something we should warn them about
        usersSchedulesCollec.whereEqualTo("name", scheduleName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Is only looping through 1 item because it only loops through items with the name of the schedule
                        //Instead, can just go through the path with the schedule name, but keep here for reference for
                        //when query is actually needed.
                        for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                            schedule = documentSnapshot.toObject(Schedule.class);
                            txtViewName.setText(schedule.getName());
                        }
                        currentDayList = schedule.getMonday();
                        currentDay = "Monday";
                        updateUI();
                        next.setOnClickListener(v -> nextDay());
                        prev.setOnClickListener(v -> previousDay());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(dailySchedule.this, "error with loading", Toast.LENGTH_LONG).show();
                        Log.d("TAG", e.getMessage());
                    }
                });
    }

    public void nextDay() {
        if (currentDay.equals("Monday")) {
            currentDayList = schedule.getTuesday();
            currentDay = "Tuesday";
            updateUI();
            return;
        }
        if (currentDay.equals("Tuesday")) {
            currentDayList = schedule.getWednesday();
            currentDay = "Wednesday";
            updateUI();
            return;
        }
        if (currentDay.equals("Wednesday")) {
            currentDayList = schedule.getThursday();
            currentDay = "Thursday";
            updateUI();
            return;
        }
        if (currentDay.equals("Thursday")) {
            currentDayList = schedule.getFriday();
            currentDay = "Friday";
            updateUI();
            return;
        }
        if (currentDay.equals("Friday")) {
            currentDayList = schedule.getMonday();
            currentDay = "Monday";
            updateUI();
            return;
        }
    }

    public void previousDay() {
        if (currentDay.equals("Monday")) {
            currentDayList = schedule.getFriday();
            currentDay = "Friday";
            updateUI();
            return;
        }
        if (currentDay.equals("Tuesday")) {
            currentDayList = schedule.getMonday();
            currentDay = "Monday";
            updateUI();
            return;
        }
        if (currentDay.equals("Wednesday")) {
            currentDayList = schedule.getTuesday();
            currentDay = "Tuesday";
            updateUI();
            return;
        }
        if (currentDay.equals("Thursday")) {
            currentDayList = schedule.getWednesday();
            currentDay = "Wednesday";
            updateUI();
            return;
        }
        if (currentDay.equals("Friday")) {
            currentDayList = schedule.getThursday();
            currentDay = "Thursday";
            updateUI();
            return;
        }
    }

    public void updateUI() {
        txtViewDay.setText(currentDay);
        Log.d("TAG", "Schedule size: " + currentDayList.size());
        Log.d("TAG", "Schedule: " + currentDayList);
        TextView txt = findViewById(R.id.textViewDailyTest);
        txt.setText(schedule.dailyToString(currentDayList));
        adapter = new dailyScheduleAdapter(currentDayList, dailySchedule.this);
        recyclerView.setAdapter(adapter);


    }
    /** currently integrated into button switching methods, but might? need later
    public void updateDay() {
        if (currentDayList.equals(schedule.getMonday())) {
            txtViewDay.setText("Monday");
            return;
        }
        if (currentDayList.equals(schedule.getTuesday())) {
            txtViewDay.setText("Tuesday");
            return;
        }
        if (currentDayList.equals(schedule.getWednesday())) {
            txtViewDay.setText("Wednesday");
            return;
        }
        if (currentDayList.equals(schedule.getThursday())) {
            txtViewDay.setText("Thursday");
            return;
        }
        if (currentDayList.equals(schedule.getFriday())) {
            txtViewDay.setText("Friday");
            return;
        }
    }
     */

    void home() {
        Intent intent = new Intent(dailySchedule.this, ChooseSchedule.class);
        startActivity(intent);
        finish();
    }
    void toMap() {
        Intent intent = new Intent(dailySchedule.this, mapScreen.class);
        startActivity(intent);
        finish();
    }
}
