package com.example.mycanvas;

import android.content.res.ColorStateList;
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

import java.util.Map;

public class PersonalTracker extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{

    // Variables
    private FirebaseServices firebaseServices;

    private SeekBar moodBar;
    private TextView pctgTxt;
    private Button submitBtn;
    private Map userMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_tracker);

        // Initialize Variables
        firebaseServices = new FirebaseServices();

        moodBar = findViewById(R.id.moodSeekBar);
        pctgTxt = findViewById(R.id.moodPctTxt);
        submitBtn = findViewById(R.id.moodSbmtBtn);

        moodBar.setOnSeekBarChangeListener(this);
        submitBtn.setOnClickListener(this);

        // Fetch State
        fetchPersonalData();

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch(seekBar.getId()){
            case R.id.moodSeekBar:
                if(progressCalculation(i) <= 0 && progressCalculation(i) >= -100){
                    submitBtn.setBackgroundTintList(this.getResources().getColorStateList(R.color.red));
                    pctgTxt.setText("-" + Integer.toString(progressCalculation(i)) + "%");
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
        switch(view.getId()){
            case R.id.moodSbmtBtn:
                boolean update = false;
                if(userMood != null) update = true;
                firebaseServices.userMoodChange(moodBar.getProgress(), (String) userMood.get("id"), update, new FirebaseServices.FirebaseServicesListener() {
                    @Override
                    public void onError(String msg) {
                        Log.w("[FIREBASE SERVICE]", msg);
                    }

                    @Override
                    public void onSuccess(Object response) {
                        userMood = (Map) response;
                        Toast.makeText(PersonalTracker.this, "Successfully submit your mood today!", Toast.LENGTH_LONG).show();
                        Log.d("[FIREBASE SERVICE]", "Show Response" + userMood.toString());
                    }
                });
                break;
            default:
                break;
        }
    }

    public int progressCalculation(int val){
        return (val - 100);
    }

    private void fetchPersonalData(){
        firebaseServices.userMoodFetch(new FirebaseServices.FirebaseServicesListener() {
            @Override
            public void onError(String msg) {
                Toast.makeText(PersonalTracker.this, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(Object response) {
                userMood = (Map) response;
                moodBar.setProgress((int) (long) userMood.get("value"));
            }
        });
    }
}
