package com.example.scheduleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class createSchedule extends AppCompatActivity {
    List<CourseInfo> schedule;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        findViewById(R.id.createScheduleCancel).setOnClickListener(v -> cancel());
        findViewById(R.id.create).setOnClickListener(v -> addCourse());
        findViewById(R.id.create_schedule_save).setOnClickListener(v -> saveSchedule());
    }
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
