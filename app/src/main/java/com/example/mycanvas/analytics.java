package com.example.mycanvas;

import static com.example.mycanvas.MoodColor.moodColor;
import static com.example.mycanvas.StateManager.getAvgUserMood;

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
        stateManager = ((MoodIndexApp) getApplicationContext()).getStateManager();

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
        fetchStockBtn.setOnClickListener(this);
        addFavStockBtn.setOnClickListener(this);

        // Disable protection so user cannot use the button when the stock is not fetched.
        addFavStockBtn.setEnabled(false);

        fetchStock("", true);

        //Sets Mood bar Color according to average mood
        TextView headerView = findViewById(R.id.header);
        stateManager = ((MoodIndexApp) getApplicationContext()).getStateManager();
        moodColor(headerView,stateManager.getAvgUserMood());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fetchStockBtn:
                fetchStock(et_stock_quote.getText().toString(), false);
                break;
            case R.id.addFavStockBtn:
                if (isHaveStocksFav()) {
                    boolean needAdd = addStockInFav(et_stock_quote.getText().toString());
                    if (needAdd) {
                        firebaseServices.addUserFavStock(et_stock_quote.getText().toString(), (String) ((Map) stateManager.getUserFavStocks()).get("id"), true, new FirebaseServices.FirebaseServicesListener() {
                            @Override
                            public void onError(String msg) {
                                Toast.makeText(analytics.this, "You failed to add stock to the watchlist!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(Object response) {
                                setUserFavStock(et_stock_quote.getText().toString(), "", "", true);
                                Toast.makeText(analytics.this, "Successfully add symbol to the watchlist!", Toast.LENGTH_LONG).show();
                                addFavStockBtn.setEnabled(false);
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
                            setUserFavStock(et_stock_quote.getText().toString(), (String) res.get("id"), (String) res.get("user_id"), false);
                            Toast.makeText(analytics.this, "You are successfully add stock symbol to your watchlist!", Toast.LENGTH_LONG).show();
                            addFavStockBtn.setEnabled(false);
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    public boolean isHaveStocksFav() { ;
        if (!stateManager.getUserFavStocks().containsKey("id")) return false;
        return true;
    }

    public boolean addStockInFav(String name) {
        ArrayList<String> userFavStocks = (ArrayList<String>) ((Map) stateManager.getUserFavStocks()).get("fav_stocks");
        if (userFavStocks.indexOf(name) != -1) return false;
        return true;
    }

    public void setUserFavStock(String name, String docId, String userId, boolean isHavingFav) {

        if (isHavingFav) {
            Map userStockFav = ((Map) stateManager.getUserFavStocks());
            ArrayList<String> oldUserStockFav = (ArrayList<String>) userStockFav.get("fav_stocks");
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

    public void fetchStock(String name, boolean fromClick) {
        addFavStockBtn.setEnabled(false);
        if (fromClick) {
            Intent intent = getIntent();
            String change = intent.getStringExtra("some");
            if (change == null) {
                addFavStockBtn.setEnabled(false);
            } else {
                requestService.getStockQuote(change, high_price, low_price, open_price, previous_price, current_price, tick, changes, percent, new RequestService.StockQuoteListener() {
                    @Override
                    public void onError(String error) {
                        addFavStockBtn.setEnabled(false);
                        Toast.makeText(analytics.this, error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String stock) {
                        if(isHaveStocksFav() && addStockInFav(stock)){
                            addFavStockBtn.setEnabled(true);
                        } else if(!isHaveStocksFav()){
                            addFavStockBtn.setEnabled(true);
                        }
                        et_stock_quote.setText(stock);
                    }
                });
            }
        } else {
            requestService.getStockQuote(name, high_price, low_price, open_price, previous_price, current_price, tick, changes, percent, new RequestService.StockQuoteListener() {
                @Override
                public void onError(String error) {
                    Toast.makeText(analytics.this, error, Toast.LENGTH_SHORT).show();
                    addFavStockBtn.setEnabled(false);
                }

                @Override
                public void onResponse(String stock) {
                    if(isHaveStocksFav() && addStockInFav(stock)){
                        addFavStockBtn.setEnabled(true);
                    } else if(!isHaveStocksFav()){
                        addFavStockBtn.setEnabled(true);
                    }
                    addFavStockBtn.setEnabled(true);
                }
            });
        }
    }
}