package com.example.mycanvas;

import static com.example.mycanvas.MoodColor.moodColor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class settings extends AppCompatActivity implements View.OnClickListener {

    FirebaseServices firebaseServices = new FirebaseServices();
    StateManager stateManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomViewNavigation);
        bottomNavigationView.setSelectedItemId(R.id.settings);
        Navigation navigation = new Navigation(settings.this, bottomNavigationView);
        navigation.initializeNavBar();

        Button SignOut = findViewById(R.id.SignOut);
        SignOut.setOnClickListener(this);

        //Setting Theme ---------------------------------------------------
        Switch darkTheme = findViewById(R.id.DarkTheme);
        setTheme(darkTheme);

        //Sets Mood bar Color according to average mood
        TextView headerView = findViewById(R.id.header);
        stateManager = ((MoodIndexApp) getApplicationContext()).getStateManager();
        moodColor(headerView,stateManager.getAvgUserMood());

        // Initialize StateManager
        //stateManager = ((MoodIndexApp) getApplicationContext()).getStateManager(); // this will get the stateManager that initialize in MoodIndexApp application class
        // After the initialization you can use it to get the variable such as
        //int avgUserMood = stateManager.getAvgUserMood();
        // Or you can set the value
        //stateManager.setAvgUserMood(someVal);


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
                        startActivity(new Intent(settings.this, Login.class));
                    }
                });
                break;
            default:
                break;
        }
    }

    public void setTheme(Switch _switch) {

        boolean isNightMode = (this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        SharedPreferences sharedPreferences = getSharedPreferences("SWITCH",MODE_PRIVATE);
        _switch.setChecked(sharedPreferences.getBoolean("value",false));

        _switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                    if(isNightMode){

                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        recreate();
                    }
                SharedPreferences.Editor editor = getSharedPreferences("SWITCH",MODE_PRIVATE).edit();
                editor.putBoolean("value",true);
                editor.apply();
                _switch.setChecked(true);
            }else {
                    if(!isNightMode){

                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        recreate();
                    }
                SharedPreferences.Editor editor = getSharedPreferences("SWITCH",MODE_PRIVATE).edit();
                editor.putBoolean("value",false);
                editor.apply();
                _switch.setChecked(false);
            }
        });

    }
}