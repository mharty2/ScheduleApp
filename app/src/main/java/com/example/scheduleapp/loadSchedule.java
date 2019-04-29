package com.example.scheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class loadSchedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_schedule);
        findViewById(R.id.load_home).setOnClickListener(v -> home());
    }
    void home() {
        Intent intent = new Intent(loadSchedule.this, ChooseSchedule.class);
        startActivity(intent);
        finish();
    }
}
