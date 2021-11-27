package com.example.mycanvas;

import static com.example.mycanvas.MoodColor.moodColor;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Map;

public class PersonalTracker extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, MyAdapter.OnNoteListener {

    // Variables
    private FirebaseServices firebaseServices;
    private StateManager stateManager;

    private TextView headerView;

    private SeekBar moodBar;
    private TextView pctgTxt;
    private Button submitBtn;

    private Map userMood;

    private SeekBar moodBarBefore;
    private TextView pctgBeforeTxt;
    private TextView descMoodBeforeTxt;

    private RecyclerView watchListView;

    private int daysBefore = 15;

    private int usrAvgMoodVal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_tracker);

        // Initialize Variables
        firebaseServices = new FirebaseServices();
        stateManager = ((MoodIndexApp) getApplicationContext()).getStateManager();

        moodBar = findViewById(R.id.moodSeekBar);
        pctgTxt = findViewById(R.id.moodPctTxt);
        submitBtn = findViewById(R.id.moodSbmtBtn);

        moodBar.setOnSeekBarChangeListener(this);
        submitBtn.setOnClickListener(this);

        moodBarBefore = findViewById(R.id.moodSeekBarBefore);
        pctgBeforeTxt = findViewById(R.id.moodPctBeforeTxt);
        descMoodBeforeTxt = findViewById(R.id.usrAvgMoodDescBefore);

        moodBarBefore.setEnabled(false);

        // Initialize RecyclerView
        watchListView = (RecyclerView) this.findViewById(R.id.watchListView);
        watchListView.setLayoutManager(new LinearLayoutManager(PersonalTracker.this, LinearLayoutManager.VERTICAL, false));
        watchListView.setAdapter(new MyAdapter());


        // Fetch State on create
        fetchPersonalData();
        fetchFavStocks();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomViewNavigation);
        bottomNavigationView.setSelectedItemId(R.id.personalTracker);
        Navigation navigation = new Navigation(PersonalTracker.this, bottomNavigationView);
        navigation.initializeNavBar();

        //Sets Mood bar Color according to average mood
        headerView = findViewById(R.id.header);
        stateManager = ((MoodIndexApp) getApplicationContext()).getStateManager();
        moodColor(headerView, stateManager.getAvgUserMood(), StateManager.isMoodSwitchOn());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch personal state data on resume
        fetchPersonalData();

        // Fetch user favorite stocks list or watchlist
        fetchFavStocks();

        // Change header according to mood color
        moodColor(headerView, stateManager.getAvgUserMood(), false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.moodSeekBar:
                if (progressCalculation(i) <= 0 && progressCalculation(i) >= -100) {
                    submitBtn.setBackgroundTintList(this.getResources().getColorStateList(R.color.red));
                    pctgTxt.setText(Integer.toString(progressCalculation(i)) + "%");
                } else {
                    submitBtn.setBackgroundTintList(this.getResources().getColorStateList(R.color.green));
                    pctgTxt.setText("+" + Integer.toString(progressCalculation(i)) + "%");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.moodSbmtBtn:
                boolean update = false;
                String id = "";
                if (userMood != null) {
                    update = true;
                    id = (String) userMood.get("id");
                }
                firebaseServices.userMoodChange(moodBar.getProgress(), id, update, new FirebaseServices.FirebaseServicesListener() {
                    @Override
                    public void onError(String msg) {
                        Log.w("[FIREBASE SERVICE]", msg);
                    }

                    @Override
                    public void onSuccess(Object response) {
                        userMood = (Map) response;
                        stateManager.setUserMood((Map) response);
                        Toast.makeText(PersonalTracker.this, "Successfully submit your mood today!", Toast.LENGTH_LONG).show();
                        Log.d("[FIREBASE SERVICE]", "Show Response" + userMood.toString());
                    }
                });
                fetchPersonalData();
                break;
            default:
                break;
        }
    }

    @Override
    public void oNoteClick(int position) {
        ArrayList<String> favStocks = (ArrayList<String>) ((Map) stateManager.getUserFavStocks()).get("fav_stocks");
        Intent intent = new Intent(this, analytics.class);
        intent.putExtra("some", favStocks.get(position));
        startActivity(intent);
    }

    @Override
    public void delBtnClicked(int position) {
        Map userFavStock = (Map) stateManager.getUserFavStocks();
        ArrayList<String> favStocks = (ArrayList<String>) userFavStock.get("fav_stocks");
        firebaseServices.deleteUserFavStock(favStocks.get(position), (String) userFavStock.get("id"), new FirebaseServices.FirebaseServicesListener() {
            @Override
            public void onError(String msg) {

            }

            @Override
            public void onSuccess(Object response) {
                favStocks.remove(position);
                userFavStock.remove("fav_stocks");
                userFavStock.put("fav_stocks", favStocks);

                stateManager.setUserFavStocks(userFavStock);
                MyAdapter myAdapter = new MyAdapter(PersonalTracker.this, favStocks, PersonalTracker.this);
                watchListView.setAdapter(myAdapter);
            }
        });

    }

    //---------- Helpers ----------
    public int progressCalculation(int val) {
        return (val - 100);
    }
    //---------- Helpers ----------

    //---------- Functions ----------
    /*
        fetchPersonalData function
        parameters none
        return void

        This function to handle fetch user mood value from the database
        and handle the UI change according to database value.
     */
    private void fetchPersonalData() {
        firebaseServices.userMoodFetch(new FirebaseServices.FirebaseServicesListener() {
            @Override
            public void onError(String msg) {
                userMood = null;
                Toast.makeText(PersonalTracker.this, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object response) {
                userMood = (Map) response;
                stateManager.setUserMood((Map) response);
                moodBar.setProgress((int) (long) userMood.get("value"));
            }
        });

        firebaseServices.userMoodFetchBefore(daysBefore, new FirebaseServices.FirebaseServicesListener() {
            @Override
            public void onError(String msg) {
                Toast.makeText(PersonalTracker.this, "Failed to fetch average user mood for n days before, please try again later...", Toast.LENGTH_LONG).show();
                usrAvgMoodVal = 0;
                stateManager.setAvgUserMood(usrAvgMoodVal);
            }

            @Override
            public void onSuccess(Object response) {
                usrAvgMoodVal = (int) response;
                moodBarBefore.setProgress(usrAvgMoodVal);
                usrAvgMoodVal -= 100;
                stateManager.setAvgUserMood(usrAvgMoodVal);
                if (usrAvgMoodVal <= 0 && usrAvgMoodVal >= -100) {
                    descMoodBeforeTxt.setText("You felt negative in the past few days.");
                    pctgBeforeTxt.setText(String.valueOf(usrAvgMoodVal) + "%");// In this line to change the average mood in the percentage text.
                } else {
                    descMoodBeforeTxt.setText("You felt positive in the past few days.");
                    pctgBeforeTxt.setText("+" + String.valueOf(usrAvgMoodVal) + "%");// In this line to change the average mood in the percentage text.
                }
            }
        });
    }

    /*
        fetchFavStocks function
        parameters none
        return void

        This function to handle fetch user favorite stocks or user watchlist value from the database
        and handle the UI change according to database value.
     */
    public void fetchFavStocks() {
        firebaseServices.fetchUserFavStocks(
                new FirebaseServices.FirebaseServicesListener() {
                    @Override
                    public void onError(String msg) {
                        Toast.makeText(PersonalTracker.this, msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Object response) {
                        stateManager.setUserFavStocks((Map) response);
                        ArrayList<String> favStocks = (ArrayList<String>) ((Map) stateManager.getUserFavStocks()).get("fav_stocks");
                        MyAdapter myAdapter = new MyAdapter(PersonalTracker.this, favStocks, PersonalTracker.this);
                        watchListView.setAdapter(myAdapter);
                    }
                }
        );

    }
    //---------- Functions ----------
}
