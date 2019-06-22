package com.example.scheduleapp.Objects;

//helper class for figuring out schedule conflicts etc. may be deleted
public class Time {
    private double start;
    private double end;
    private double duration;

    public Time(String input) {
        String[] arr = input.split("-");
        String one = arr[0];
        String two = arr[1];
        String[] arr1 = one.split(":");
        String[] arr2 = two.split(":");
        double doub1 = Integer.parseInt(arr1[0].trim());
        double doub2 = Integer.parseInt(arr2[0].trim());
        if (one.contains("PM") && doub1 != 12) {
            doub1 += 12;
        }
        if (two.contains("PM") && doub2 != 12) {
            doub2 += 12;
        }
        if (one.contains("AM") && doub1 == 12) {
            doub1 = 0;
        }
        if (two.contains("AM") && doub2 == 12) {
            doub2 = 0;
        }
        doub1 +=  (double) (Integer.parseInt(arr1[1].trim()))/60;
        doub2 += (double) (Integer.parseInt(arr2[1].trim()))/60;
        start = doub1;
        end = doub2;
        duration = Math.abs(start - end);
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getTimeBetween(Time other) {
        double firstDif = Math.abs(getStart() - other.getEnd());
        double secondDif = Math.abs(other.getStart() - getEnd());
        return Math.min(firstDif, secondDif);
    }

    public boolean checkTimeInterference(Time other) {
        if (start >= other.getStart() && start <= other.getEnd()) {
            return true;
        }
        if (end >= other.getStart() && end <= other.getEnd()) {
            return true;
        }
        if (other.getStart() >= start && other.getStart() <= end) {
            return true;
        }
        if (other.getEnd() >= start && other.getEnd() <= end) {
            return true;
        }
        return false;
    }
}
