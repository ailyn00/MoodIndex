package com.example.mycanvas;

import java.util.HashMap;
import java.util.Map;

class StateManager {

    private static Map userFavStocks;
    private static Map userMood;
    static int avgUserMood;
    private static boolean MoodSwitchOn;



    public StateManager() {
        userFavStocks = new HashMap<>();
        userMood = new HashMap<>();
        avgUserMood = 0;
        MoodSwitchOn = false;
    }

    public static int getAvgUserMood() {
        return avgUserMood;
    }

    public static void setAvgUserMood(int avgUserMood) {
        StateManager.avgUserMood = avgUserMood;
    }

    public static Map getUserFavStocks() {
        return userFavStocks;
    }

    public static void setUserFavStocks(Map userFavStocks) {
        StateManager.userFavStocks = userFavStocks;
    }

    public static Map getUserMood() {
        return userMood;
    }

    public static void setUserMood(Map userMood) {
        StateManager.userMood = userMood;
    }

    public static boolean isMoodSwitchOn() {
        return MoodSwitchOn;
    }

    public static void setMoodSwitchOn(boolean moodSwitchOn) {
        StateManager.MoodSwitchOn = moodSwitchOn;
    }
}
