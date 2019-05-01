package com.example.scheduleapp;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class Schedule {
    private String name;
    private HashMap<String, List<CourseInfo>> schedule = new HashMap();
    private List<CourseInfo> monday = new ArrayList<>();
    private List<CourseInfo> tuesday = new ArrayList<>();
    private List<CourseInfo> wednesday = new ArrayList<>();
    private List<CourseInfo> thursday = new ArrayList<>();
    public List<CourseInfo> friday = new ArrayList<>();
    Schedule(String name) {
        this.name = name;
        schedule.put("Monday", monday);
        schedule.put("Tuesday", tuesday);
        schedule.put("Wednesday", wednesday);
        schedule.put("Thursday", thursday);
        schedule.put("Friday", friday);
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
        sortSchedule();
    }
    private void sortSchedule() {
        for(Map.Entry<String, List<CourseInfo>> entry : schedule.entrySet()) {
            sortByTime(entry.getValue());
        }
    }
    private void sortByTime(List<CourseInfo> input) {
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
}