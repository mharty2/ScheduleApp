package com.example.scheduleapp;

import android.util.Log;
import android.util.Xml;
import android.widget.GridLayout;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {
    private static final String ns = null;

    //Class that stores a specific class's start time, end time, and building name
    public static class SpecificClassData {

        private final String type;
        private final String days;
        private final String start;
        private final String end;
        private final String buildingName;
        private final String roomNumber;



        private SpecificClassData(String setType, String setDays,
                                  String setStart, String setEnd,
                                  String setBuildingName, String setRoomNumber) {
            this.type = setType;
            this.days = setDays;
            this.start = setStart;
            this.end = setEnd;
            this.buildingName = setBuildingName;
            this.roomNumber = setRoomNumber;
        }
        public String getType() {return type; }
        public String getDays() {return days; }
        public String getStart() {
            return start;
        }
        public String getEnd() {
            return end;
        }
        public String getBuildingName() {
            return buildingName;
        }
        public String getRoomNumber() {return roomNumber; }
        public String printAll() {
            return type + ", " + days + ", " + start + ", " + end + ", " + buildingName + ", " + roomNumber;
        }
    }

    public List<SpecificClassData> parseSpecificClass(InputStream in) throws XmlPullParserException, IOException {
        try {
            Log.d("parse", "reached parse method");
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in , null);
            parser.nextTag();
            return readNS2C(parser);
        } finally {
            in.close();
        }
    }

    //Helper method that start the nested search for different data in xml (for specific class page)
    private List<SpecificClassData> readNS2C(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d("readNS2", "readNS2C method reached");

        List<SpecificClassData> toReturn = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "ns2:course");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("detailedSections")) {
                toReturn = readDetailedSections(parser);
            } else {
                skip(parser);
            }
        }
        return toReturn;
    }

    //Helper method that starts the nested search for detailedSection (calls readDetailedSection)
    private List<SpecificClassData> readDetailedSections(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d("readDetailedSections", "reached readDetailedSections method");

        List<SpecificClassData> toReturn = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "detailedSections");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("detailedSection")) {
                toReturn.add(readDetailedSection(parser));
            } else {
                skip(parser);
            }
        }
        return toReturn;
    }

    //Helper method that starts the nested search for meetings (calls readMeetings)
    private SpecificClassData readDetailedSection(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d("readDetailedSection", "reached readDetailedSection method");

        SpecificClassData toReturn = null;

        parser.require(XmlPullParser.START_TAG, ns, "detailedSection");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("meetings")) {
                toReturn = readMeetings(parser);
            } else {
                skip(parser);
            }
        }
        return toReturn;
    }

    //Helper method that starts the nested search for meeting (calls readMeeting)
    private SpecificClassData readMeetings(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d("readMeetings", "reached readMeetings");

        SpecificClassData toReturn = null;
        parser.require(XmlPullParser.START_TAG, ns, "meetings");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("meeting")) {
                toReturn = readMeeting(parser);
            } else {
                skip(parser);
            }
        }
        return toReturn;
    }

    //Reads meeting tag of a specific class. Gets start time, end time, and building name
    private SpecificClassData readMeeting(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Debug print statement
        Log.d("readMeeting", "reached readMeeting method");

        parser.require(XmlPullParser.START_TAG, ns, "meeting");
        String type = null;
        String days = null;
        String start = null;
        String end = null;
        String buildingName = null;
        String roomNumber = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("type")) {
                type = readType(parser);
            } else if (name.equals("daysOfTheWeek")) {
                days = readDays(parser);
            } else if (name.equals("start")) {
                start = readStart(parser);
            } else if (name.equals("end")) {
                end = readEnd(parser);
            } else if (name.equals("buildingName")) {
                buildingName = readBuildingName(parser);
            } else if (name.equals("roomNumber")) {
                roomNumber = readRoomNumber(parser);
            } else {
                skip(parser);
            }
        }
        Log.d("readMeeting OUTPUT", type + days + start + end + buildingName + roomNumber);
        return new SpecificClassData(type, days, start, end, buildingName, roomNumber);
    }

    //Gets start time of a specific class
    private String readType(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Debug print statement
        Log.d("readType", "reached readType method");

        parser.require(XmlPullParser.START_TAG, ns, "type");
        String type = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "type");
        return type;
    }

    //Gets start time of a specific class
    private String readDays(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Debug print statement
        Log.d("readDays", "reached readDays method");

        parser.require(XmlPullParser.START_TAG, ns, "daysOfTheWeek");
        String days = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "daysOfTheWeek");
        return days;
    }

    //Gets start time of a specific class
    private String readStart(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Debug print statement
        Log.d("readStart", "reached readStart method");

        parser.require(XmlPullParser.START_TAG, ns, "start");
        String start = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "start");
        return start;
    }

    //Gets end time of a specific class
    private String readEnd(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Debug print statement
        Log.d("readEnd", "reached readEnd method");

        parser.require(XmlPullParser.START_TAG, ns, "end");
        String end = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "end");
        return end;
    }

    //Gets building name of a specific class
    private String readBuildingName(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Debug print statement
        Log.d("readBuildingName", "reached readBuildingName method");

        parser.require(XmlPullParser.START_TAG, ns, "buildingName");
        String buildingName = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "buildingName");
        return buildingName;
    }

    //Gets start time of a specific class
    private String readRoomNumber(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Debug print statement
        Log.d("readRoomNumber", "reached readRoomNumber method");

        parser.require(XmlPullParser.START_TAG, ns, "roomNumber");
        String roomNumber = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "roomNumber");
        return roomNumber;
    }

    //Helper method to read text in tags in xml
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        //Debug print statement
        Log.d("readText", "reached readText method");

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    //Helper method that skips tag if it's not what we're looking for
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Debug print statement
        Log.d("skip", "reached skip method");
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
