package com.example.mycanvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class analytics extends AppCompatActivity {
    Button btn_stock_quote;
    EditText et_stock_quote;
    TextView current_price;
    TextView open_price;
    TextView high_price;
    TextView low_price;
    TextView previous_price;
    TextView tick;
    TextView changes;
    TextView percent;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomViewNavigation);
        bottomNavigationView.setSelectedItemId(R.id.analytics);
        Navigation navigation = new Navigation(analytics.this, bottomNavigationView);
        btn_stock_quote = findViewById(R.id.change);
        et_stock_quote = findViewById(R.id.et_stock);
        current_price = findViewById(R.id.current);
        open_price = findViewById(R.id.open);
        high_price = findViewById(R.id.high);
        low_price = findViewById(R.id.low);
        previous_price = findViewById(R.id.previous);
        tick = findViewById(R.id.tick);
        changes = findViewById(R.id.changes);
        percent = findViewById(R.id.percents);
        button = findViewById(R.id.add);
        navigation.initializeNavBar();
        RequestService requestService = new RequestService(analytics.this);
        btn_stock_quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestService.getStockQuote(et_stock_quote.getText().toString() , high_price , low_price,open_price,previous_price, current_price,tick, changes ,percent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}