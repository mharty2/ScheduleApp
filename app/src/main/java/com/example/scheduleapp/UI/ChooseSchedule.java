package com.example.scheduleapp.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.scheduleapp.Objects.CourseInfo;
import com.example.scheduleapp.R;
import com.example.scheduleapp.Objects.Schedule;

public class ChooseSchedule extends AppCompatActivity {

    //Tag for debugging
    private static final String TAG = "ChooseSchedule";

    //Variables
    private boolean mLocationPermissionGranted = false;
    private Schedule testSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_schedule);
        findViewById(R.id.CreateSchedule).setOnClickListener(v -> createSchedule());
        findViewById(R.id.LoadSchedule).setOnClickListener(v -> loadSchedule());
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
