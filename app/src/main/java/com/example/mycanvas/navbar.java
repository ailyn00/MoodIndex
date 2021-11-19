package com.example.mycanvas;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static androidx.core.content.ContextCompat.startActivity;

public class navbar extends Activity {

    public void setBottomNavigationView(BottomNavigationView bottomNavigationView ) {
//      BottomNavigationView bottomNavigationView =    findViewById(R.id.bottomViewNavigation);
       bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()) {

                   case R.id.homePage:
                     startActivity(new Intent(getApplicationContext(), HomePage.class));
                    overridePendingTransition(0, 0);
                       return true;

                   case R.id.personalTracker:
                      startActivity(new Intent(getApplicationContext(), PersonalTracker.class));
                      overridePendingTransition(0, 0);
                       return true;

                   case R.id.analytics:
                       startActivity(new Intent(getApplicationContext(), analytics.class));
                     overridePendingTransition(0, 0);
                       return true;

                   case R.id.settings:
                       startActivity(new Intent(getApplicationContext(), settings.class));
                      overridePendingTransition(0, 0);
                       return true;

               }
               return false;
           }


       });
   }
}
