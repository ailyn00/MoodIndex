package com.example.mycanvas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomePage extends AppCompatActivity implements View.OnClickListener{
    FirebaseServices firebaseServices = new FirebaseServices();
    private boolean isLogged = false;
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
    }
    @Override
    protected void onResume() {
        super.onResume();
        isLoggedIn();
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
}