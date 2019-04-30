package com.example.scheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CourseSearch extends AppCompatActivity {

    private List<XMLParser.SpecificClassData> internalClassList;
    private String xmlAsString;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<CourseInfo> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);
        findViewById(R.id.courseSearchCancel).setOnClickListener(v -> cancel());
        findViewById(R.id.courseSearchAdd).setOnClickListener(v -> addCourse());
        findViewById(R.id.courseSearchSearchButton).setOnClickListener(v -> loadRecyclerViewData());
        recyclerView = findViewById(R.id.courseSearchRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();
        loadRecyclerViewData();
    }
    private void loadRecyclerViewData() {
        for (int i = 0; i < 10; i++) {
            CourseInfo course = new CourseInfo("Course" + (i+1),
                    "Type" + (i+1),
                    "Section" + (i+1),
                    "Time" + (i+1),
                    "Days" + (i+1),
                    "Location" + (i+1),
                    "CreditHours" + i,
                    "CRN" + i);
            listItems.add(course);
        }
        adapter = new CourseSearchAdapter(listItems, CourseSearch.this);
        recyclerView.setAdapter(adapter);
    }

    private String getURL() {
        String courseSubject = ((TextView) findViewById(R.id.courseSearchSubject)).getText().toString();
        String courseNumber = ((TextView) findViewById(R.id.courseSearchNumber)).getText().toString();
        return "https://courses.illinois.edu/cisapp/explorer/schedule/2019/fall/"
                + courseSubject + "/" + courseNumber
                + ".xml?mode=cascade";
    }

    void showClasses() {
        for (XMLParser.SpecificClassData current : internalClassList) {
            ((TextView) findViewById(R.id.courseSearchRecycler)).append(current.printAll());
        }
    }

    private void getXmlAsString() {
        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            xmlAsString = getRequest.execute(getURL()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void parseXml() {
        Log.d("parseXML", "xmlAsStringBefore: " + xmlAsString);
        getXmlAsString();
        Log.d("parseXML", "xmlAsStringAfter: " + xmlAsString);
        InputStream stream = new ByteArrayInputStream(xmlAsString.getBytes(Charset.forName("UTF-8")));
        XMLParser parser = new XMLParser();

        try {
            internalClassList = parser.parseSpecificClass(stream);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            showClasses();
        }
    }

    void cancel() {
        Intent intent = new Intent(CourseSearch.this, createSchedule.class);
        startActivity(intent);
        finish();
    }

    void addCourse() {
        Intent intent = new Intent(CourseSearch.this, createSchedule.class);
        startActivity(intent);
        finish();
    }
}
