package com.example.scheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.MapView;

public class mapScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_screen);
        findViewById(R.id.map_home).setOnClickListener(v->home());
        findViewById(R.id.backToDailyBtn).setOnClickListener(v -> returnToDaily());

    }

    void returnToDaily() {
        Intent intent = new Intent(mapScreen.this, dailySchedule.class);
        startActivity(intent);
        finish();
    }
    void home() {
        Intent intent = new Intent(mapScreen.this, ChooseSchedule.class);
        startActivity(intent);
        finish();
    }
}
