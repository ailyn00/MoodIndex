package com.example.mycanvas;

import static com.example.mycanvas.MoodColor.moodColor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseServices firebaseServices = new FirebaseServices();
    StateManager stateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomViewNavigation);
        bottomNavigationView.setSelectedItemId(R.id.settings);
        Navigation navigation = new Navigation(SettingsActivity.this, bottomNavigationView);
        navigation.initializeNavBar();

        Button SignOut = findViewById(R.id.SignOut);
        SignOut.setOnClickListener(this);

        //Setting Theme ---------------------------------------------------
        Switch darkTheme = findViewById(R.id.DarkTheme);
        darkModeSwitch(darkTheme);

        //Setting
        Switch moodBarSwitch = findViewById(R.id.MoodColorBarSwitch);
        moodBarSwitch(moodBarSwitch);

        //Sets Mood bar Color according to average mood
        TextView headerView = findViewById(R.id.header);
        stateManager = ((MoodIndexApp) getApplicationContext()).getStateManager();
        moodColor(headerView,stateManager.getAvgUserMood(),StateManager.isMoodSwitchOn());

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {

            case R.id.SignOut:
                firebaseServices.userSignOut(new FirebaseServices.FirebaseServicesListener() {
                    @Override
                    public void onError(String msg) {
                        Log.d("[Firebase Service]", "Failed to log out, please try again later!");
                    }

                    @Override
                    public void onSuccess(Object response) {
                        Log.d("[Firebase Service]", "Successfully to log out from the application.");
                        startActivity(new Intent(SettingsActivity.this, Login.class));
                    }
                });
                break;
            default:
                break;
        }
    }

    public void darkModeSwitch(Switch _switch) {

        boolean isNightMode = (this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        SharedPreferences sharedPreferences = getSharedPreferences("SWITCHDARK",MODE_PRIVATE);
        _switch.setChecked(sharedPreferences.getBoolean("value",false));

        _switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                    if(isNightMode){

                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        recreate();
                    }
                SharedPreferences.Editor editor = getSharedPreferences("SWITCHDARK",MODE_PRIVATE).edit();
                editor.putBoolean("value",true);
                editor.apply();
                _switch.setChecked(true);
            }else {
                    if(!isNightMode){

                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        recreate();
                    }
                SharedPreferences.Editor editor = getSharedPreferences("SWITCHDARK",MODE_PRIVATE).edit();
                editor.putBoolean("value",false);
                editor.apply();
                _switch.setChecked(false);
            }
        });

    }

    public void moodBarSwitch(Switch _switch) {

        SharedPreferences sharedPreferences = getSharedPreferences("SWITCHMOOD",MODE_PRIVATE);
        _switch.setChecked(sharedPreferences.getBoolean("value",true));

        _switch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked){
                SharedPreferences.Editor editor = getSharedPreferences("SWITCHMOOD",MODE_PRIVATE).edit();
                editor.putBoolean("value",true);
                editor.apply();
                _switch.setChecked(true);

                StateManager.setMoodSwitchOn(true);
            }else {
                SharedPreferences.Editor editor = getSharedPreferences("SWITCHMOOD",MODE_PRIVATE).edit();
                editor.putBoolean("value",false);
                editor.apply();
                _switch.setChecked(false);

                StateManager.setMoodSwitchOn(false);
            }

        });
    }
}