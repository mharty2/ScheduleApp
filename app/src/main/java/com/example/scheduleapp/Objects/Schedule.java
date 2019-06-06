package com.example.scheduleapp.Objects;

import com.example.scheduleapp.Objects.CourseInfo;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Schedule
    {

    private String name;
    private List<CourseInfo> monday = new ArrayList<>();
    private List<CourseInfo> tuesday = new ArrayList<>();
    private List<CourseInfo> wednesday = new ArrayList<>();
    private List<CourseInfo> thursday = new ArrayList<>();
    private List<CourseInfo> friday = new ArrayList<>();
    private List<CourseInfo> classList = new ArrayList<>();

    public Schedule(String name) {
        this.name = name;
    }
    public Schedule() { }
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
        if (course.getDays() != null) {
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
    }

    public String stringify() {
        return name + "\n" + monday + "\n" + tuesday+ "\n" + wednesday+ "\n" + thursday+ "\n" + friday;
    }
    public void sortSchedule() {
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
        //Collections.reverse(list);
    }

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