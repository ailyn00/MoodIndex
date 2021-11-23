package com.example.mycanvas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class analytics extends AppCompatActivity implements View.OnClickListener {

    FirebaseServices firebaseServices;
    RequestService requestService;
    StateManager stateManager;

    EditText et_stock_quote;
    TextView current_price;
    TextView open_price;
    TextView high_price;
    TextView low_price;
    TextView previous_price;
    TextView tick;
    TextView changes;
    TextView percent;
    Button fetchStockBtn;
    Button addFavStockBtn;
    Button delFavStockBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomViewNavigation);
        bottomNavigationView.setSelectedItemId(R.id.analytics);
        Navigation navigation = new Navigation(analytics.this, bottomNavigationView);
        navigation.initializeNavBar();

        firebaseServices = new FirebaseServices();
        requestService = new RequestService(analytics.this);
        stateManager = ((MoodIndexApp)getApplicationContext()).getStateManager();

        et_stock_quote = findViewById(R.id.et_stock); // Input Fields

        current_price = findViewById(R.id.current); // TextView
        open_price = findViewById(R.id.open); // TextView
        high_price = findViewById(R.id.high); // TextView
        low_price = findViewById(R.id.low); // TextView
        previous_price = findViewById(R.id.previous); // TextView
        tick = findViewById(R.id.tick); // TextView
        changes = findViewById(R.id.changes); // TextView
        percent = findViewById(R.id.percents); // TextView

        fetchStockBtn = findViewById(R.id.fetchStockBtn);
        addFavStockBtn = findViewById(R.id.addFavStockBtn);
        delFavStockBtn = findViewById(R.id.deleteFavStockBtn);
        fetchStockBtn.setOnClickListener(this);
        addFavStockBtn.setOnClickListener(this);
        delFavStockBtn.setOnClickListener(this);

       autoclicked();

    }

    public void autoclicked(){
        Intent intent = getIntent();
        String change = intent.getStringExtra("some");
        et_stock_quote.setText(change);
        requestService.getStockQuote(change, high_price, low_price, open_price, previous_price, current_price, tick, changes, percent, new RequestService.StockQuoteListener() {
            @Override
            public void onError(String error) {
                Toast.makeText(analytics .this ,"The ticker is error , try again later ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String stock) {

            }
       }  );
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fetchStockBtn:


                requestService.getStockQuote(et_stock_quote.getText().toString(), high_price, low_price, open_price, previous_price, current_price, tick, changes, percent, new RequestService.StockQuoteListener() {
                    @Override
                    public void onError(String error) {
                        Toast.makeText(analytics .this ,"The ticker is error , try again later ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String stock) {

                    }
                });
            case R.id.addFavStockBtn:
                // Request Service benerin, kalo ada ticker yang ga valid masa toastnya masih success (Coba pake interface kaya di contoh youtube yg gw kasi ke lu dulu)
                // Kalo pake interface yang kaya di youtube ato yang kaya di Firebase ini bakal kasi 2 class yg bisa nerima kalo onError ato onSuccess
                // Kalo Error keluarnya toast
                // kudunya di check isTickerValid ? if yes baru lanjut line bawah kalo else berarti kasi toast ticker invalid ato gimana....
                if(isHaveStocksFav()){
                    boolean needAdd = addStockInFav(et_stock_quote.getText().toString());
                    if(needAdd){
                        firebaseServices.addUserFavStock(et_stock_quote.getText().toString(), (String) ((Map) stateManager.getUserFavStocks()).get("id"), true, new FirebaseServices.FirebaseServicesListener() {
                            @Override
                            public void onError(String msg) {
                                Toast.makeText(analytics.this, "You failed to add stock to the watchlist!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(Object response) {
                                setUserFavStock(et_stock_quote.getText().toString(), "", "",true);
                                Toast.makeText(analytics.this, "Successfully add symbol to the watchlist!", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(analytics.this, "You already have this stock in your watchlist!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    firebaseServices.addUserFavStock(et_stock_quote.getText().toString(), "", false, new FirebaseServices.FirebaseServicesListener() {
                        @Override
                        public void onError(String msg) {
                            Toast.makeText(analytics.this, "You failed to add stock to the watchlist!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(Object response) {
                            Map res = (Map) response;
                            setUserFavStock(et_stock_quote.getText().toString(), (String) res.get("id"),(String) res.get("user_id"), false);
                            Toast.makeText(analytics.this, "You are successfully add stock symbol to your watchlist!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            case R.id.deleteFavStockBtn:
                if(isHaveStocksFav()){
                    if(!addStockInFav(et_stock_quote.getText().toString())){
                        firebaseServices.deleteUserFavStock(et_stock_quote.getText().toString(), (String) ((Map) stateManager.getUserFavStocks()).get("id"), new FirebaseServices.FirebaseServicesListener() {
                            @Override
                            public void onError(String msg) {
                                Toast.makeText(analytics.this, "You failed to remove stock to the watchlist!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(Object response) {
                                delUserFavStock(et_stock_quote.getText().toString());
                                Toast.makeText(analytics.this, "You successfully remove stock from the watchlist!", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(analytics.this, "You dont have this stock in your watchlist!", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }

    public boolean isTickerValid(){
        // WARFRAME TROS LIAT LAH COMMENT NYA SEKALIAN DAH GTA V TROS
        return false;
    }

    public boolean isHaveStocksFav(){
        System.out.println(stateManager.getUserFavStocks().containsKey("id"));
        if(!stateManager.getUserFavStocks().containsKey("id")) return false;
        return true;
    }

    public boolean addStockInFav(String name){
        ArrayList<String> userFavStocks = (ArrayList<String>) ((Map) stateManager.getUserFavStocks()).get("fav_stocks");
        if(userFavStocks.indexOf(name) != -1) return false;
        return true;
    }

    public void setUserFavStock(String name, String docId, String userId, boolean isHavingFav){

        if(isHavingFav){

            Map userStockFav = ((Map) stateManager.getUserFavStocks());
            ArrayList<String> oldUserStockFav =  (ArrayList<String>) userStockFav.get("fav_stocks");
            oldUserStockFav.add(name);
            userStockFav.remove("fav_stocks");
            userStockFav.put("fav_stocks", oldUserStockFav);
            stateManager.setUserFavStocks(userStockFav);

        } else {
            Map userStockFav = new HashMap<>();
            ArrayList<String> favStocks = new ArrayList<>();
            favStocks.add(name);

            userStockFav.put("id", docId);
            userStockFav.put("user_id", userId);
            userStockFav.put("fav_stocks", favStocks);

            stateManager.setUserFavStocks(userStockFav);
        }
    }
    public void delUserFavStock(String name){
        Map userStockFav = ((Map) stateManager.getUserFavStocks());
        ArrayList<String> oldUserStockFav =  (ArrayList<String>) userStockFav.get("fav_stocks");
        oldUserStockFav.remove(oldUserStockFav.indexOf(name));
        userStockFav.remove("fav_stocks");
        userStockFav.put("fav_stocks", oldUserStockFav);
        stateManager.setUserFavStocks(userStockFav);
    }
}