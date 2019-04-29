package com.example.scheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class dailySchedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule);
        findViewById(R.id.daily_home).setOnClickListener(v->home());
        findViewById(R.id.goToMapbtn).setOnClickListener(v -> toMap());
    }
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
