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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import java.util.Set;

public class createSchedule extends AppCompatActivity {
    private String name;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<CourseInfo> schedule;
    private CourseInfo toAdd;
    private static final String PREFS_NAME = "ScheduleApp";
    private static final String PREF_USER_ID_TOKEN = "UserIdToken";
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        schedule = new ArrayList<CourseInfo>();
        recyclerView = findViewById(R.id.createScheduleRecycler);
        findViewById(R.id.createScheduleCancel).setOnClickListener(v -> cancel());
        findViewById(R.id.create).setOnClickListener(v -> addCourse());
        findViewById(R.id.create_schedule_save).setOnClickListener(v -> saveSchedule());
        mFirestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CreateScheduleAdapter(schedule, this);
        recyclerView.setAdapter(adapter);
        try {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithCustomToken(sharedPreferences.getString(PREF_USER_ID_TOKEN, null));
            //Toast.makeText(createSchedule.this, "Authentication with Shared Preferences worked", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("tag", "signInAnonymously:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(PREF_USER_ID_TOKEN, mAuth.getUid());
                                editor.apply();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("tag", "signInAnonymously:failure", task.getException());
                                Toast.makeText(createSchedule.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadRecyclerViewData();
    }

    private void loadRecyclerViewData() {
        if (toAdd != null) {
            schedule.add(toAdd);
        }
        checkForDuplicates();
        Log.d("schedule check","stored schedule: " + schedule);
        Log.d("schedule check pt.2","stored schedule size: " + schedule.size());
        adapter = new CreateScheduleAdapter(schedule, createSchedule.this);
        recyclerView.setAdapter(adapter);
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
        //THIS LINE IS DIFFERENT. We want a course info class back!!
        startActivityForResult(intent, 1337);
    }

    void checkForDuplicates() {
        if (schedule != null) {
            Set<CourseInfo> set = new LinkedHashSet<>();
            set.addAll(schedule);
            schedule.clear();
            schedule.addAll(set);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "onActivityResult reached");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1337) {
            Log.d("firstResultCheck", "firstResultCheck reached");
            if (resultCode == 2) {
                Log.d("secondResultCheck", "secondResultCheck reached");
                toAdd = data.getExtras().getParcelable("storedCourse");
                Log.d("toAdd", "Stored courseInfo: " + toAdd);
            }
        }
    }

    void saveSchedule() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please name your schedule");

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
                if(name == null || name.equals("")) {
                    //need to figure out how to relay this to the user because it happens too fast for them to see atm.
                    input.setHint("Schedule must have a name!");
                    return;
                }
                //Is the empty constructor necessary for parcelable? I changed it to correspond with the other constructor
                //Even then, we might not need the parcelable part for the schedule once we have firebase working
                Schedule scheduleClasses = new Schedule(name);
                for (CourseInfo current : schedule) {
                    scheduleClasses.addCourse(current);
                }

                Map<String, Schedule> scheduleMap = new HashMap<>();
                scheduleMap.put(scheduleClasses.getName(), scheduleClasses);
                mFirestore.collection(sharedPreferences.getString(PREF_USER_ID_TOKEN, null)).add(scheduleMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(createSchedule.this, "schedule added to Firestore", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(createSchedule.this, "schedule addition failed" + e, Toast.LENGTH_LONG).show();
                        Log.d("tag", e.toString());
                    }
                });

                /**
                //For demo purposes right?
                Intent intent = new Intent(createSchedule.this, joshMapScreen.class);
                Bundle scheduleBundle = new Bundle();
                scheduleBundle.putParcelable("schedule", scheduleClasses);
                startActivity(intent);
                finish();
                 */

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
