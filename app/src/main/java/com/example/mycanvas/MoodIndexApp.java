package com.example.mycanvas;

import android.app.Application;

public class MoodIndexApp extends Application {
    private StateManager myStateManager = new StateManager();

    public StateManager getStateManager(){
        return myStateManager ;
    }
}
