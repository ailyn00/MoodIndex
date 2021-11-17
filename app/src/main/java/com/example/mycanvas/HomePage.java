package com.example.mycanvas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends AppCompatActivity implements View.OnClickListener{
    FirebaseServices firebaseServices = new FirebaseServices();

    private boolean isLogged = false;

    private int daysBefore = 15;
    private int avgUserMood = 0;
    private int avgUserMoodBefore = 0;

    private SeekBar avgMoodSeekBar;
    private TextView avgPctgTxt;
    private TextView descAvgTxt;

    private SeekBar avgMoodSeekBarBefore;
    private TextView avgPctgBeforeTxt;
    private TextView descAvgBeforeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Check if user is logged in or not
        isLoggedIn();

        Button testBtn1 = findViewById(R.id.testBtn1);
        Button testBtn2 = findViewById(R.id.testBtn2);

        testBtn1.setOnClickListener(this);
        testBtn2.setOnClickListener(this);

        avgMoodSeekBar = findViewById(R.id.avgMoodSeekBar);
        avgPctgTxt = findViewById(R.id.avgPctgTxt);
        descAvgTxt = findViewById(R.id.descAvgMood);

        avgMoodSeekBarBefore = findViewById(R.id.avgMoodSeekBarBefore);
        avgPctgBeforeTxt = findViewById(R.id.avgPctgBeforeTxt);
        descAvgBeforeTxt = findViewById(R.id.descAvgMoodBefore);

        // Fetch Average User Mood on Create
        fetchUsersMoodVal();

        // Set Average Seekbar is disable (So Client cannot change the view)
        avgMoodSeekBar.setEnabled(false);
        avgMoodSeekBarBefore.setEnabled(false);
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Check if user is logged in or not
        isLoggedIn();

        // Fetch Average User Mood on Resume.
        fetchUsersMoodVal();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.testBtn1:
                startActivity(new Intent(HomePage.this, PersonalTracker.class));
                break;
            case R.id.testBtn2:
                firebaseServices.userSignOut(new FirebaseServices.FirebaseServicesListener() {
                    @Override
                    public void onError(String msg) {
                        System.out.println(msg);
                    }

                    @Override
                    public void onSuccess(Object response) {
                        System.out.println(response);
                        startActivity(new Intent(HomePage.this, MainActivity.class));
                    }
                });
                break;
            default:
                break;
        }
    }

    private void isLoggedIn() {
        firebaseServices.isLoggedIn(new FirebaseServices.FirebaseServicesListener() {
            @Override
            public void onError(String msg) {
                Toast.makeText(HomePage.this, "Failed to check authentication! Please check your credentials", Toast.LENGTH_LONG).show();
                isLogged = false;
            }

            @Override
            public void onSuccess(Object response) {
                isLogged = (boolean) response;
                if(!isLogged){
                    startActivity(new Intent(HomePage.this, MainActivity.class));
                }
            }
        });
    }

    private void fetchUsersMoodVal(){
        firebaseServices.usersMoodAverage(new FirebaseServices.FirebaseServicesListener() {
            @Override
            public void onError(String msg) {
                Toast.makeText(HomePage.this, "Failed to fetch average user mood for today, please try again later...", Toast.LENGTH_LONG).show();
                avgUserMood = 0;
            }

            @Override
            public void onSuccess(Object response) {
                avgUserMood = (int) response;
                avgMoodSeekBar.setProgress(avgUserMood);
                if(avgUserMood <= 0 && avgUserMood >= -100){
                    descAvgTxt.setText("The market feels negative today.");
                    avgPctgTxt.setText("-" + String.valueOf(avgUserMood-100) + "%");// In this line to change the average mood in the percentage text.
                } else {
                    descAvgTxt.setText("The market feels positive today.");
                    avgPctgTxt.setText("+" + String.valueOf(avgUserMood-100) + "%");// In this line to change the average mood in the percentage text.
                }
            }
        });

        firebaseServices.usersMoodAverageBefore(daysBefore, new FirebaseServices.FirebaseServicesListener() {
            @Override
            public void onError(String msg) {
                Toast.makeText(HomePage.this, "Failed to fetch average users mood for n days before, please try again later...", Toast.LENGTH_LONG).show();
                avgUserMoodBefore = 0;
            }

            @Override
            public void onSuccess(Object response) {
                avgUserMoodBefore = (int) response;
                avgMoodSeekBarBefore.setProgress(avgUserMoodBefore);
                if(avgUserMoodBefore <= 0 && avgUserMoodBefore >= -100){
                    descAvgBeforeTxt.setText("The market feels negative.");
                    avgPctgBeforeTxt.setText("-" + String.valueOf(avgUserMoodBefore-100) + "%");// In this line to change the average mood in the percentage text.
                } else {
                    descAvgBeforeTxt.setText("The market feels positive.");
                    avgPctgBeforeTxt.setText("+" + String.valueOf(avgUserMoodBefore-100) + "%");// In this line to change the average mood in the percentage text.
                }
            }
        });
    }
}