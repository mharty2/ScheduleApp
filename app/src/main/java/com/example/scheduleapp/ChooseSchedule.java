package com.example.scheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChooseSchedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_schedule);
        findViewById(R.id.CreateSchedule).setOnClickListener(v -> createSchedule());
        findViewById(R.id.LoadSchedule).setOnClickListener(v -> loadSchedule());
        findViewById(R.id.test).setOnClickListener(v -> joshMapScreen());
    }

    void joshMapScreen() {
        Intent intent = new Intent(ChooseSchedule.this, joshMapScreen.class);
        startActivity(intent);
        finish();
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
