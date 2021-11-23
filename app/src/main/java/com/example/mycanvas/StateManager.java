package com.example.mycanvas;

import java.util.HashMap;
import java.util.Map;

class StateManager {

    private static Map userFavStocks;
    private static Map userMood;

    public StateManager() {
        userFavStocks = new HashMap<>();
        userMood = new HashMap<>();
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
}
