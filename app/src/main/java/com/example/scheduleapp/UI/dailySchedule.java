package com.example.scheduleapp.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scheduleapp.Adapters.dailyScheduleAdapter;
import com.example.scheduleapp.Objects.CourseInfo;
import com.example.scheduleapp.R;
import com.example.scheduleapp.Objects.Schedule;
import com.example.scheduleapp.SelectedSchedule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
    private static final String KEY_SELECTED_DAY = "KEY_SELECTED_DAY";
    private static final String TAG = "TAG";
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
    private Button map;
    private List<CourseInfo> currentDayList;
    private String currentDay;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule);
        findViewById(R.id.daily_home).setOnClickListener(v->home());
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        txtViewName = findViewById(R.id.textViewDailyName);
        txtViewDay = findViewById(R.id.daily_schedule_day);
        next = findViewById(R.id.daily_next);
        prev = findViewById(R.id.daily_previous);
        map = findViewById(R.id.buttonDailyToMap);
        recyclerView = findViewById(R.id.recyclerViewDaily);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        next.setOnClickListener(v -> nextDay());
        prev.setOnClickListener(v -> previousDay());
        map.setOnClickListener(v-> toMap());
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
        schedule = SelectedSchedule.getInstance().getSchedule();
        Log.d(TAG, "SelectedScheduleInstance: " + SelectedSchedule.getInstance());
        Log.d(TAG, "loadSchedule: " + schedule);
        currentDayList = schedule.getMonday();
        currentDay = "Monday";
        txtViewName.setText(schedule.getName());
        updateUI();
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
        schedule.sortByTime(currentDayList);
        Log.d("TAG", "Schedule size: " + currentDayList.size());
        Log.d("TAG", "Schedule: " + currentDayList);
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SELECTED_DAY, currentDay);
        editor.apply();
        Intent intent = new Intent(dailySchedule.this, joshMapScreen.class);
        startActivity(intent);
        finish();
    }
}
