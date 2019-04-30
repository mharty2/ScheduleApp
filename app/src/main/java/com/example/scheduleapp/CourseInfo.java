package com.example.scheduleapp;
/** Class for holding all data for google-services.json single course */
public class CourseInfo {
    private String name;
    private String type;
    private String section;
    private String time;
    private String days;
    private String location;
    private int creditHours;
    private int crn;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSection() {
        return section;
    }

    public String getTime() {
        return time;
    }

    public String getDays() {
        return days;
    }

    public String getLocation() {
        return location;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public int getCrn() {
        return crn;
    }

    public CourseInfo(String name, String type, String section, String time, String days, String location, int creditHours, int crn) {
        this.name = name;
        this.type = type;
        this.section = section;
        this.time = time;
        this.days = days;
        this.location = location;
        this.creditHours = creditHours;
        this.crn = crn;
    }
}
