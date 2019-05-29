package com.example.scheduleapp.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that runs implements several helper functions on location data.
 */
public class Locator {

    /**All coordinates of UIUC places. */
    private static Map<String, double[]> locations = new HashMap<>();

    /**Stores the list of each place for each day. */
    private static double[][] longitudes;
    /** */
    private static double[][] latitudes;
    /**
     * Takes the name of the buildings/places on UIUC campus and sets the coordinate variables accordingly.
     * @param places a.
     */
    public static void processCoordinates(final ArrayList<ArrayList<String>> places) {
        latitudes = new double[places.size()][];
        longitudes = new double[places.size()][];
        double[] temp;
        for (int i = 0; i < places.size(); i++) {
            latitudes[i] = new double[places.get(i).size()];
            longitudes[i] = new double[places.get(i).size()];
            for (int j = 0; j < places.get(i).size(); j++) {
                temp = locations.get(places.get(i).get(j));
                latitudes[i][j] = temp[0];
                longitudes[i][j] = temp[1];
            }
        }
    }
    /**
     * Sets up the hashmap of the courses and their locations. If the arguments differ in size, it throws
     * an illegal argument exception.
     * @param buildings names of the buildings.
     * @param inputLatitudes latitudes of the buildings.
     * @param inputLongitudes longitudes of the buildings.
     */
    public static void inputCourseCoordinates(final ArrayList<String> buildings, final ArrayList<Double>
            inputLatitudes, final ArrayList<Double> inputLongitudes) {
        if (!(buildings.size() == inputLatitudes.size() && buildings.size() == inputLongitudes.size())) {
            throw new IllegalArgumentException();
        }
        double[] temp = new double[2];
        for (int i = 0; i < buildings.size(); i++) {
            temp[0] = inputLatitudes.get(i);
            temp[1] = inputLongitudes.get(i);
            locations.put(buildings.get(i), temp);
        }
    }
    /**
     * E.
     * @return longitude locations.
     */
    public static double[][] getLongitudes() {
        return longitudes;
    }
    /**
     * E.
     * @return latitude locations.
     */
    public static double[][] getLatitudes() {
        return latitudes;
    }
}
