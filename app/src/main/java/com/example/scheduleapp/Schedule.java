package com.example.scheduleapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class Schedule //implements Parcelable
    {

    private String name;
    private List<CourseInfo> monday = new ArrayList<>();
    private List<CourseInfo> tuesday = new ArrayList<>();
    private List<CourseInfo> wednesday = new ArrayList<>();
    private List<CourseInfo> thursday = new ArrayList<>();
    private List<CourseInfo> friday = new ArrayList<>();
    private List<CourseInfo> classList = new ArrayList<>();

    Schedule(String name) {
        this.name = name;
    }
    Schedule() { }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CourseInfo> getMonday() {
        return monday;
    }
    public List<CourseInfo> getTuesday() {
        return tuesday;
    }
    public List<CourseInfo> getWednesday() {
        return wednesday;
    }
    public List<CourseInfo> getThursday() {
        return thursday;
    }
    public List<CourseInfo> getFriday() {
        return friday;
    }

    public void addCourse(CourseInfo course) {
        if (course.getDays().contains("M")) {
            monday.add(course);
        }
        if (course.getDays().contains("T")) {
            tuesday.add(course);
        }
        if (course.getDays().contains("W")) {
            wednesday.add(course);
        }
        if (course.getDays().contains("R")) {
            thursday.add(course);
        }
        if (course.getDays().contains("F")) {
            friday.add(course);
        }
        classList.add(course);
        sortSchedule();
    }

    public String stringify() {
        return name + "\n" + monday + "\n" + tuesday+ "\n" + wednesday+ "\n" + thursday+ "\n" + friday;
    }
    private void sortSchedule() {
        sortByTime(monday);
        sortByTime(tuesday);
        sortByTime(wednesday);
        sortByTime(thursday);
        sortByTime(friday);
    }
    /**
    public void sortByTime(List<CourseInfo> input) {
        for (int i = 1; i < input.size(); i++) {
            for (int j = i - 1; j >= 0; j--) {
                if (compareTimes(input.get(j).getTime(), input.get(i).getTime()) > 0) {
                    CourseInfo temp = input.get(j);
                    input.set(j, input.get(i));
                    input.set(i, temp);
                    i--;
                }
            }
        }
    }
     */

    public void sortByTime(List<CourseInfo> list) {
        Collections.sort(list, (CourseInfo a, CourseInfo b) -> a.compareTimes(b));
        //todo make it right the first time lol
        Collections.reverse(list);
    }
    /**
    private int compareTimes(String a, String b) {
        String[] arr1 = a.split(":");
        String[] arr2 = b.split(":");
        int int1 = Integer.parseInt(arr1[0]);
        int int2 = Integer.parseInt(arr2[0]);
        if (a.contains("PM") && int1 != 12) {
            int1 += 12;
        }
        if (b.contains("PM") && int2 != 12) {
            int2 += 12;
        }
        if (a.contains("AM") && int1 == 12) {
            int1 = 0;
        }
        if (b.contains("AM") && int2 == 12) {
            int2 = 0;
        }
        if (int1 > int2) {
            return 1;
        }
        if (int1 == int2) {
            return 0;
        }
        return -1;
    }
     */
/**
    //Parcel functions
    protected Schedule(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            monday = new ArrayList<CourseInfo>();
            in.readList(monday, CourseInfo.class.getClassLoader());
        } else {
            monday = null;
        }
        if (in.readByte() == 0x01) {
            tuesday = new ArrayList<CourseInfo>();
            in.readList(tuesday, CourseInfo.class.getClassLoader());
        } else {
            tuesday = null;
        }
        if (in.readByte() == 0x01) {
            wednesday = new ArrayList<CourseInfo>();
            in.readList(wednesday, CourseInfo.class.getClassLoader());
        } else {
            wednesday = null;
        }
        if (in.readByte() == 0x01) {
            thursday = new ArrayList<CourseInfo>();
            in.readList(thursday, CourseInfo.class.getClassLoader());
        } else {
            thursday = null;
        }
        if (in.readByte() == 0x01) {
            friday = new ArrayList<CourseInfo>();
            in.readList(friday, CourseInfo.class.getClassLoader());
        } else {
            friday = null;
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (monday == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(monday);
        }
        if (tuesday == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tuesday);
        }
        if (wednesday == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(wednesday);
        }
        if (thursday == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(thursday);
        }
        if (friday == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(friday);
        }
    }
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Schedule> CREATOR = new Parcelable.Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };
*/
    public List<CourseInfo> getClassList() {
        return classList;
    }

    public void setClassList(List<CourseInfo> classList) {
        this.classList = classList;
    }

    public void setMonday(List<CourseInfo> monday) {
        this.monday = monday;
    }

    public void setTuesday(List<CourseInfo> tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(List<CourseInfo> wednesday) {
        this.wednesday = wednesday;
    }

    public void setThursday(List<CourseInfo> thursday) {
        this.thursday = thursday;
    }

    public void setFriday(List<CourseInfo> friday) {
        this.friday = friday;
    }

    public String classListToString() {
        String toReturn = "";
        for (CourseInfo course : classList) {
            toReturn += "\n" + course.getBasicInfo() + "\n";
        }
        return toReturn;
    }

    public String dailyToString(List<CourseInfo> list) {
        String toReturn ="";
        for (CourseInfo course : list) {
            toReturn += course.getName() + " - " + course.getType() + "\n\n";
            toReturn += course.getSection() +"\n\n";
            toReturn += course.getTime() +"\n\n";
            toReturn += course.getDays() +"\n\n";
            toReturn += course.getLocation() +"\n\n";
            toReturn += course.getCreditHours() +"\n\n";
            toReturn += course.getCrn() +"\n\n";
            toReturn += "\n\n\n";
        }
        return toReturn;
    }
}