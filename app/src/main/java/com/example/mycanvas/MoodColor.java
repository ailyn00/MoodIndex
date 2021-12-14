package com.example.mycanvas;

import android.widget.TextView;

public class MoodColor {


    public static void  moodColor (TextView view, int averageMoodIndex, boolean isOn){

        //More colors and conditions can be set

        if (isOn) {     //If moodbar switch is on, do..., else do nothing

            if (averageMoodIndex > 74) {
                view.setBackgroundResource(R.color.Excited);
            } else if (averageMoodIndex < 26) {
                view.setBackgroundResource(R.color.Panic);
            } else {
                view.setBackgroundResource(R.color.purple_200);
            }
        }

    }

}
