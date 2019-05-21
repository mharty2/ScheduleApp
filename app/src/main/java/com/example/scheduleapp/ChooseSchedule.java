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

    private static final String TAG = "ChooseSchedule";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_schedule);
        findViewById(R.id.CreateSchedule).setOnClickListener(v -> createSchedule());
        findViewById(R.id.LoadSchedule).setOnClickListener(v -> loadSchedule());
        findViewById(R.id.test).setOnClickListener(v -> joshMapScreen());
    }


    //Test if user has most updated google services
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: Checking Google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ChooseSchedule.this);
        if (available == ConnectionResult.SUCCESS) {
            //Everything is fine and user can make map request
            Log.d(TAG, "isServicesOK: Google Play Services are working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //An error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: An error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ChooseSchedule.this, available, 1000);
            dialog.show();
        } else {
            Log.d(TAG, "isServicesOK: Nothing we can do dude");
        }
        return false;
    }

    void joshMapScreen() {
        if (isServicesOK()) {
            Intent intent = new Intent(ChooseSchedule.this, joshMapScreen.class);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "joshMapScreen: error with services");
            Toast.makeText(this, "Map failed to launch", Toast.LENGTH_SHORT).show();
        }
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
