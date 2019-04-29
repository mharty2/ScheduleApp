package com.example.scheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CourseSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);
        findViewById(R.id.courseSearchCancel).setOnClickListener(v -> cancel());
        findViewById(R.id.courseSearchAdd).setOnClickListener(v -> addCouse());
    }
    void cancel() {
        Intent intent = new Intent(CourseSearch.this, createSchedule.class);
        startActivity(intent);
        finish();
    }
    void addCouse() {
        Intent intent = new Intent(CourseSearch.this, createSchedule.class);
        startActivity(intent);
        finish();
    }
}
