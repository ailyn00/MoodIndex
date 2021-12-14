package com.example.mycanvas;

import static com.example.mycanvas.MoodColor.moodColor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseServices firebaseServices;
    private RequestService requestService;
    private StateManager stateManager;

    private TextView headerView;
    private EditText et_stock_quote;
    private TextView current_price;
    private TextView open_price;
    private TextView high_price;
    private TextView low_price;
    private TextView previous_price;
    private TextView tick;
    private TextView changes;
    private TextView percent;
    private Button fetchStockBtn;
    private Button addFavStockBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomViewNavigation);
        bottomNavigationView.setSelectedItemId(R.id.analytics);
        Navigation navigation = new Navigation(AnalyticsActivity.this, bottomNavigationView);
        navigation.initializeNavBar();

        firebaseServices = new FirebaseServices();
        requestService = new RequestService(AnalyticsActivity.this);
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
        headerView = findViewById(R.id.header);
        moodColor(headerView, StateManager.getAvgUserMood(), StateManager.isMoodSwitchOn());

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Change header color on resume based on mood
        moodColor(headerView,stateManager.getAvgUserMood(), false);
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
                        firebaseServices.addUserFavStock(et_stock_quote.getText().toString(), (String) ((Map) StateManager.getUserFavStocks()).get("id"), true, new FirebaseServices.FirebaseServicesListener() {
                            @Override
                            public void onError(String msg) {
                                Toast.makeText(AnalyticsActivity.this, "You failed to add stock to the watchlist!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(Object response) {
                                setUserFavStock(et_stock_quote.getText().toString(), "", "", true);
                                Toast.makeText(AnalyticsActivity.this, "Successfully add symbol to the watchlist!", Toast.LENGTH_LONG).show();
                                addFavStockBtn.setEnabled(false);
                            }
                        });
                    } else {
                        Toast.makeText(AnalyticsActivity.this, "You already have this stock in your watchlist!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    firebaseServices.addUserFavStock(et_stock_quote.getText().toString(), "", false, new FirebaseServices.FirebaseServicesListener() {
                        @Override
                        public void onError(String msg) {
                            Toast.makeText(AnalyticsActivity.this, "You failed to add stock to the watchlist!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(Object response) {
                            Map res = (Map) response;
                            setUserFavStock(et_stock_quote.getText().toString(), (String) res.get("id"), (String) res.get("user_id"), false);
                            Toast.makeText(AnalyticsActivity.this, "You are successfully add stock symbol to your watchlist!", Toast.LENGTH_LONG).show();
                            addFavStockBtn.setEnabled(false);
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    //---------- Validations ----------
    public boolean isHaveStocksFav() {
        return StateManager.getUserFavStocks().containsKey("id");
    }

    public boolean addStockInFav(String name) {
        ArrayList<String> userFavStocks = (ArrayList<String>) ((Map) StateManager.getUserFavStocks()).get("fav_stocks");
        return userFavStocks.indexOf(name) == -1;
    }
    //---------- Validations ----------

    //---------- Helpers ----------
    /*
        setUserFavStock function
        parameters String name, String docId, string userId, boolean isHavingFav
        return void

        This function to handle UI State change when user successfully add the stock name to watchlist on firebase.
     */
    public void setUserFavStock(String name, String docId, String userId, boolean isHavingFav) {

        if (isHavingFav) {
            Map userStockFav = ((Map) StateManager.getUserFavStocks());
            ArrayList<String> oldUserStockFav = (ArrayList<String>) userStockFav.get("fav_stocks");
            oldUserStockFav.add(name);
            userStockFav.remove("fav_stocks");
            userStockFav.put("fav_stocks", oldUserStockFav);
            StateManager.setUserFavStocks(userStockFav);

        } else {
            Map userStockFav = new HashMap<>();
            ArrayList<String> favStocks = new ArrayList<>();
            favStocks.add(name);

            userStockFav.put("id", docId);
            userStockFav.put("user_id", userId);
            userStockFav.put("fav_stocks", favStocks);

            StateManager.setUserFavStocks(userStockFav);
        }
    }

    /*
        fetchStock function
        parameters String name, boolean fromClick
        return void

        This function for handle the fetch stock request
        The bool fromClick to know whether the fetch from the search button or from change activity.
     */
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
                        Toast.makeText(AnalyticsActivity.this, error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String stock) {
                        if(isHaveStocksFav() && addStockInFav(stock)){
                            addFavStockBtn.setEnabled(true);
                        } else if(!isHaveStocksFav()){
                            addFavStockBtn.setEnabled(true);
                        } else {
                            addFavStockBtn.setEnabled(false);
                        }
                        et_stock_quote.setText(stock);
                    }
                });
            }
        } else {
            requestService.getStockQuote(name, high_price, low_price, open_price, previous_price, current_price, tick, changes, percent, new RequestService.StockQuoteListener() {
                @Override
                public void onError(String error) {
                    Toast.makeText(AnalyticsActivity.this, error, Toast.LENGTH_SHORT).show();
                    addFavStockBtn.setEnabled(false);
                }

                @Override
                public void onResponse(String stock) {
                    if(isHaveStocksFav() && addStockInFav(stock)){
                        addFavStockBtn.setEnabled(true);
                    } else if(!isHaveStocksFav()){
                        addFavStockBtn.setEnabled(true);
                    } else {
                        addFavStockBtn.setEnabled(false);
                    }
                }
            });
        }
    }
    //---------- Helpers ----------
}