package com.example.mycanvas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Navigation {

    private Context ctx;
    private static BottomNavigationView bottomNavigationView;

    public Navigation(Context ctx, BottomNavigationView bottomNavigationView){
        this.ctx = ctx;
        this.bottomNavigationView = bottomNavigationView;
    }

    public void initializeNavBar(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.homePage:
                        ctx.startActivity(new Intent(ctx.getApplicationContext(), HomePageActivity.class));
                        ((Activity) ctx).overridePendingTransition(0, 0);
                        return true;
                    case R.id.personalTracker:
                        ctx.startActivity(new Intent(ctx.getApplicationContext(), PersonalTrackerActivity.class));
                        ((Activity) ctx).overridePendingTransition(0, 0);
                        return true;
                    case R.id.analytics:
                        ctx.startActivity(new Intent(ctx.getApplicationContext(), AnalyticsActivity.class));
                        ((Activity) ctx).overridePendingTransition(0, 0);
                        return true;
                    case R.id.settings:
                        ctx.startActivity(new Intent(ctx.getApplicationContext(), SettingsActivity.class));
                        ((Activity) ctx).overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

}
