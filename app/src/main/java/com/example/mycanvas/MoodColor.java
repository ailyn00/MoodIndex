package com.example.mycanvas;

import android.widget.TextView;

public class MoodColor {


    public static void  moodColor (TextView view, int averageMoodIndex){

        //More colors and conditions can be set

        if (averageMoodIndex>75){
            view.setBackgroundResource(R.color.Excited);
        }
        else if (averageMoodIndex<25){
            view.setBackgroundResource(R.color.Panic);
        }
        else{
            view.setBackgroundResource(R.color.purple_200);
        }

    }

}
