package com.example.mycanvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class analytics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomViewNavigation);
        bottomNavigationView.setSelectedItemId(R.id.analytics);
        Navigation navigation = new Navigation(analytics.this, bottomNavigationView);
        navigation.initializeNavBar();
    }
}