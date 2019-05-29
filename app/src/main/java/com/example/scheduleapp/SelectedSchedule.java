package com.example.scheduleapp;

import com.example.scheduleapp.Objects.Schedule;

public class SelectedSchedule {
    private Schedule schedule;

    private static SelectedSchedule instance;
    public static SelectedSchedule getInstance() {
        if (instance == null) {
            instance = new SelectedSchedule();
        }
        return instance;
    }

    private SelectedSchedule(){}


    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
