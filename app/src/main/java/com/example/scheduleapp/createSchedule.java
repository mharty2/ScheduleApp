package com.example.scheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class createSchedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        findViewById(R.id.createScheduleCancel).setOnClickListener(v -> cancel());
        findViewById(R.id.create).setOnClickListener(v -> addCourse());
        findViewById(R.id.create_schedule_save).setOnClickListener(v -> saveSchedule());
    }
    void cancel() {
        Intent intent = new Intent(createSchedule.this, ChooseSchedule.class);
        startActivity(intent);
        finish();
    }
    void addCourse() {
        Intent intent = new Intent(createSchedule.this, CourseSearch.class);
        startActivity(intent);
        finish();
    }
    void saveSchedule() {
        Intent intent = new Intent(createSchedule.this, ChooseSchedule.class);
        startActivity(intent);
        finish();
    }
}
