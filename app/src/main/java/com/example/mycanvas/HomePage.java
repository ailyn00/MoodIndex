package com.example.mycanvas;

import static com.example.mycanvas.MoodColor.moodColor;
import static com.example.mycanvas.StateManager.avgUserMood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

public class HomePage extends AppCompatActivity implements View.OnClickListener{
    StateManager stateManager;
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
        stateManager = ((MoodIndexApp)getApplicationContext()).getStateManager();

        avgMoodSeekBar = findViewById(R.id.avgMoodSeekBar);
        avgPctgTxt = findViewById(R.id.avgPctgTxt);
        descAvgTxt = findViewById(R.id.descAvgMood);

        avgMoodSeekBarBefore = findViewById(R.id.avgMoodSeekBarBefore);
        avgPctgBeforeTxt = findViewById(R.id.avgPctgBeforeTxt);
        descAvgBeforeTxt = findViewById(R.id.descAvgMoodBefore);

        // Fetch Average User Mood on Create
        fetchUsersMoodVal();
        fetchFavStocks();

        // Set Average Seekbar is disable (So Client cannot change the view)
        avgMoodSeekBar.setEnabled(false);
        avgMoodSeekBarBefore.setEnabled(false);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomViewNavigation);
        bottomNavigationView.setSelectedItemId(R.id.homePage);

        Navigation navigation = new Navigation(HomePage.this, bottomNavigationView);
        navigation.initializeNavBar();

        //Sets Mood bar Color according to average mood
        TextView headerView = findViewById(R.id.header);
        stateManager = ((MoodIndexApp) getApplicationContext()).getStateManager();
        moodColor(headerView,stateManager.getAvgUserMood());
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
            case R.id.SignOut:
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
                avgUserMood -= 100;
                if(avgUserMood <= 0 && avgUserMood >= -100){
                    descAvgTxt.setText("The market feels negative today.");
                    avgPctgTxt.setText(String.valueOf(avgUserMood) + "%");// In this line to change the average mood in the percentage text.
                } else {
                    descAvgTxt.setText("The market feels positive today.");
                    avgPctgTxt.setText("+" + String.valueOf(avgUserMood) + "%");// In this line to change the average mood in the percentage text.
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
                avgUserMoodBefore -= 100;
                if(avgUserMoodBefore <= 0 && avgUserMoodBefore >= -100){
                    descAvgBeforeTxt.setText("The market feels negative.");
                    avgPctgBeforeTxt.setText(String.valueOf(avgUserMoodBefore) + "%");// In this line to change the average mood in the percentage text.
                } else {
                    descAvgBeforeTxt.setText("The market feels positive.");
                    avgPctgBeforeTxt.setText("+" + String.valueOf(avgUserMoodBefore) + "%");// In this line to change the average mood in the percentage text.
                }
            }
        });
    }

    public void fetchFavStocks() {
        firebaseServices.fetchUserFavStocks(
                new FirebaseServices.FirebaseServicesListener() {
                    @Override
                    public void onError(String msg) {
                        Toast.makeText(HomePage.this, msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Object response) {
                        stateManager.setUserFavStocks((Map) response);
                    }
                }
        );

    }

    public int getAvgUserMood() {
        return avgUserMood;
    }
}