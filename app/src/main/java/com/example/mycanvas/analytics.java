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
      bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              switch (item.getItemId()) {

                    case  R.id.homePage:
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        overridePendingTransition(0,0);
                        return true;


                  case R.id.personalTracker:
                      startActivity(new Intent(getApplicationContext(), PersonalTracker.class));
                      overridePendingTransition(0, 0);
                      return true;



                   case R.id.analytics:
//
                        return true;

                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(),settings.class));
                        overridePendingTransition(0,0);
                        return  true;

                }
               return false;
              }
       });
//       navbar navbar = new navbar();
//       navbar.setBottomNavigationView(bottomNavigationView);
              }
          }
