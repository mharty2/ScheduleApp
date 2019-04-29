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
    }
    void createSchedule() {
        Intent intent = new Intent(ChooseSchedule.this, createSchedule.class);
        startActivity(intent);
        finish();
    }
    void loadSchedule() {

        /*
         * In Android we launch another screen using a so-called <em>Intent</em>. The Intent has to be configured
         * with which Activity we want to launch: in this case GameActivity.class.
         */
        Intent intent = new Intent(ChooseSchedule.this, loadSchedule.class);
        // Actually start the second Activity, causing that screen to launch.
        startActivity(intent);
        /*
         * At that point the SetupActivity is no longer needed and can exit. In Android we do this using finish,
         * which cleans up and then destroys this activity.
         */
        finish();
    }
}
