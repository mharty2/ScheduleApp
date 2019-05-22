package com.example.scheduleapp;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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
        //findViewById(R.id.test).setOnClickListener(v -> joshMapScreen());

        testSchedule = new Schedule("Test");
        testSchedule.addCourse(new CourseInfo("Intro to Diff Eq",
                "Lecture - Discussion",
                "C1",
                "10:00 AM - 10:50 AM",
                "MWF",
                "Altgelt Hall",
                "3",
                "51206"));
        testSchedule.addCourse(new CourseInfo("Physics: Elec & Magnet",
                "Lecture",
                "A3",
                "11:00 AM - 11:50 AM",
                "MWF",
                "Loomis Laboratory",
                "3",
                "12345"));
        testSchedule.addCourse(new CourseInfo("Introduction to Statics",
                "Discussion",
                "ADB",
                "12:00 AM - 12:50 AM",
                "MWF",
                "Engineering Hall",
                "3",
                "33414"));
    }
/**
    void joshMapScreen() {
        Intent intent = new Intent(ChooseSchedule.this, joshMapScreen.class);
        Bundle testScheduleBundle = new Bundle();
        testScheduleBundle.putParcelable("TestSchedule", testSchedule);
        intent.putExtras(testScheduleBundle);
        startActivity(intent);
        finish();
    }
*/
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
