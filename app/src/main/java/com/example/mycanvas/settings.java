package com.example.mycanvas;

import static com.example.mycanvas.MoodColor.moodColor;
import static com.google.android.material.button.MaterialButtonToggleGroup.*;

import  androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class settings extends AppCompatActivity implements View.OnClickListener {
    FirebaseServices firebaseServices = new FirebaseServices();


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
        MaterialButtonToggleGroup buttonToggleGroup = findViewById(R.id.btg_theme);

        buttonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {

                if (checkedId == R.id.btnDefault) {//Default
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                else if (checkedId == R.id.btnDark) { //Dark
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else if (checkedId == R.id.btnLight) {//Light
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

            }
        });

        TextView headerView = findViewById(R.id.header);
        moodColor(headerView,100);

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
                        startActivity(new Intent(settings.this, MainActivity.class));
                    }
                });
                break;
            default:
                break;
        }
    }

}