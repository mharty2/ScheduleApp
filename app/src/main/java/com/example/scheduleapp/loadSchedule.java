package com.example.scheduleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loadSchedule extends AppCompatActivity {

    private static final String PREFS_NAME = "ScheduleApp";
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_schedule);
        findViewById(R.id.load_home).setOnClickListener(v -> home());
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }
    @Override
    public void onStart() {
        super.onStart();
        try {
            mAuth.signInWithCustomToken(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
            FirebaseUser user = mAuth.getCurrentUser();
            Toast.makeText(loadSchedule.this, "Authentication with Shared Preferences worked", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(loadSchedule.this, "Authentication worked",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(PREF_USER_ID_TOKEN, user.getUid());
                                editor.apply();
                            } else {
                                Toast.makeText(loadSchedule.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    void home() {
        Intent intent = new Intent(loadSchedule.this, ChooseSchedule.class);
        startActivity(intent);
        finish();
    }
    //comment
}
