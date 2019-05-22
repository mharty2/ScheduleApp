package com.example.scheduleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class loadSchedule extends AppCompatActivity {

    private static final String PREFS_NAME = "ScheduleApp";
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private List<Schedule> scheduleList = new ArrayList<>();
    private FirebaseFirestore db;
    private CollectionReference usersCollecRef;
    private DocumentReference userDocRef;
    private CollectionReference usersSchedulesCollec;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private final String TAG = "loadSchedule";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_schedule);
        findViewById(R.id.load_home).setOnClickListener(v -> home());
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.loadScheduleRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public void onStart() {
        super.onStart();
        //loadRecyclerViewData();
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
        usersCollecRef = db.collection("Users");
        userDocRef = db.collection("Users").document(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
        usersSchedulesCollec = userDocRef.collection("Schedules");
        usersSchedulesCollec.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!=null) {
                    Log.d(TAG, e.getMessage());
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                    Schedule schedule = documentSnapshot.toObject(Schedule.class);
                    try {
                        scheduleList.add(schedule);
                        Log.d(TAG, schedule.getName());
                        Log.d(TAG, "ScheduleList inside try block: " + scheduleList.toString());
                    } catch (Exception error) {
                        Log.d(TAG, "error with adding: " + e.toString());
                    }
                }
                loadRecyclerViewData();
            }
        });
        //loadRecyclerViewData();
    }
    void loadRecyclerViewData() {
        adapter = new loadScheduleAdapter(scheduleList, loadSchedule.this);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "ScheduleList in loadData: " + scheduleList.toString());
    }
    void home() {
        Intent intent = new Intent(loadSchedule.this, ChooseSchedule.class);
        startActivity(intent);
        finish();
    }
    //comment
}
