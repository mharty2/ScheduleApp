package com.example.scheduleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Map;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class createSchedule extends AppCompatActivity {
    private String name;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<CourseInfo> schedule;
    private static final String PREFS_NAME = "ScheduleApp";
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        recyclerView = findViewById(R.id.createScheduleRecycler);
        findViewById(R.id.createScheduleCancel).setOnClickListener(v -> cancel());
        findViewById(R.id.create).setOnClickListener(v -> addCourse());
        findViewById(R.id.create_schedule_save).setOnClickListener(v -> saveSchedule());
        mFirestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CreateScheduleAdapter(schedule, createSchedule.this);
        recyclerView.setAdapter(adapter);
        try {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithCustomToken(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
            Toast.makeText(createSchedule.this, "Authentication with Shared Preferences worked", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }
    /**
    @Override
    public void onStart() {
        super.onStart();
    }
    */
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Schedule?");

// Set up the input
        final EditText input = new EditText(this);
        input.setHint("Schedule name");


        // Set an EditText view to get user input
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = input.getText().toString();

                Intent intent = new Intent(createSchedule.this, ChooseSchedule.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
