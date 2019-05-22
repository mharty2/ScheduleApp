package com.example.scheduleapp;

import android.os.Parcel;
import android.os.Parcelable;

/** Class for holding all data for google-services.json single course */
public class CourseInfo implements Parcelable {
    private String name;
    private String type;
    private String section;
    private String time;
    private String days;
    private String location;
    private String creditHours;
    private String crn;

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

    public String getCreditHours() {
        return creditHours;
    }

    public String getCrn() {
        return crn;
    }

    public CourseInfo() {}

    public CourseInfo(String name, String type, String section, String time, String days, String location, String creditHours, String crn) {
        this.name = name;
        this.type = type;
        this.section = section;
        this.time = time;
        this.days = days;
        this.location = location;
        this.creditHours = creditHours;
        this.crn = crn;
    }

    public CourseInfo(String setLocation) {
        location = setLocation;
    }

    protected CourseInfo(Parcel in) {
        name = in.readString();
        type = in.readString();
        section = in.readString();
        time = in.readString();
        days = in.readString();
        location = in.readString();
        creditHours = in.readString();
        crn = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(section);
        dest.writeString(time);
        dest.writeString(days);
        dest.writeString(location);
        dest.writeString(creditHours);
        dest.writeString(crn);
    }
    //I think all CRNs are unique, right?
    public boolean equals(CourseInfo other) {
        return this.getCrn().equals(other.getCrn());
    }

    public String getBasicInfo() {
        return name + " - " + type;
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CourseInfo> CREATOR = new Parcelable.Creator<CourseInfo>() {
        @Override
        public CourseInfo createFromParcel(Parcel in) {
            return new CourseInfo(in);
        }

        @Override
        public CourseInfo[] newArray(int size) {
            return new CourseInfo[size];
        }
    };
}