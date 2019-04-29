package com.example.scheduleapp;

import java.lang.reflect.Array;
import java.util.List;
import java.util.ArrayList;

public class Schedule {
    //Constructor
    Schedule() {
        internalClassData = new ArrayList<XMLParser.SpecificClassData>();
        schedule.add(Monday);
        schedule.add(Tuesday);
        schedule.add(Wednesday);
        schedule.add(Thursday);
        schedule.add(Friday);
    }

    //Internal class to store in each day list. Only stores one day and location
    private class simplifiedClassData {
        private String day;
        private String buildingName;
        private String roomNumber;
        private String start;
        private String end;

        public String getDay() {
            return day;
        }
        public String getBuildingName() {
            return buildingName;
        }
        public String getRoomNumber() {
            return roomNumber;
        }
        public String getStart() {
            return start;
        }
        public String getEnd() {
            return end;
        }

        simplifiedClassData(String setDay, String setBuildingName, String setRoomNumber, String setStart, String setEnd) {
            day = setDay;
            buildingName = setBuildingName;
            roomNumber = setRoomNumber;
            start = setStart;
            end = setEnd;
        }
    }

    private List<XMLParser.SpecificClassData> internalClassData;

    private List<simplifiedClassData> Monday = new ArrayList<>();
    private List<simplifiedClassData> Tuesday = new ArrayList<>();
    private List<simplifiedClassData> Wednesday = new ArrayList<>();
    private List<simplifiedClassData> Thursday = new ArrayList<>();
    private List<simplifiedClassData> Friday = new ArrayList<>();

    private List<List<simplifiedClassData>> schedule = new ArrayList<>();


    public void addClass(XMLParser.SpecificClassData toAdd) {
        internalClassData.add(toAdd);
        schedule(toAdd);
    }

    private void schedule(XMLParser.SpecificClassData toSchedule) {
        char[] toEdit = toSchedule.getDays().toCharArray();
        for (char letter : toEdit) {
            if (letter == 'M') {
                Monday.add(new simplifiedClassData("Monday",
                        toSchedule.getBuildingName(),
                        toSchedule.getRoomNumber(),
                        toSchedule.getStart(),
                        toSchedule.getEnd()));
            } else if (letter == 'T') {
                Tuesday.add(new simplifiedClassData("Tuesday",
                        toSchedule.getBuildingName(),
                        toSchedule.getRoomNumber(),
                        toSchedule.getStart(),
                        toSchedule.getEnd()));
            } else if (letter == 'W') {
                Wednesday.add(new simplifiedClassData("Wednesday",
                        toSchedule.getBuildingName(),
                        toSchedule.getRoomNumber(),
                        toSchedule.getStart(),
                        toSchedule.getEnd()));
            } else if (letter == 'R') {
                Thursday.add(new simplifiedClassData("Thursday",
                        toSchedule.getBuildingName(),
                        toSchedule.getRoomNumber(),
                        toSchedule.getStart(),
                        toSchedule.getEnd()));
            } else if (letter == 'F') {
                Friday.add(new simplifiedClassData("Friday",
                        toSchedule.getBuildingName(),
                        toSchedule.getRoomNumber(),
                        toSchedule.getStart(),
                        toSchedule.getEnd()));
            }
        }
    }

    /* private void sortDayLists(List<simplifiedClassData> dayList) {
        List<simplifiedClassData> toSet = new ArrayList<>();

        char[] minTime = dayList.get(0).getStart().toCharArray();
        while(dayList.size() > 0) {
            for (int i = 1; i < dayList.size(); i++) {
                char[] currentTime = dayList.get(i).getStart().toCharArray();
                if (minTime[0] == 1 && currentTime[0] == 1)
            }
        }
    } */
}
