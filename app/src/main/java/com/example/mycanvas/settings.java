package com.example.mycanvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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